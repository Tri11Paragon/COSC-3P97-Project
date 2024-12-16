pub mod db;

use actix_web::{get, post, web, App, Error, HttpResponse, HttpServer, Responder};
use r2d2::Pool;
use r2d2_sqlite::SqliteConnectionManager;
use serde::{Deserialize, Serialize};

#[derive(Deserialize, Debug)]
struct Info {
    username: String,
}

#[derive(Serialize)]
struct Response {
    meows: String,
}

#[post("/meow")]
async fn meow(info: web::Json<Info>) -> impl Responder {
    HttpResponse::Ok().json(Response {
        meows: info.username.clone(),
    })
}

#[get("/")]
async fn hello() -> impl Responder {
    HttpResponse::Ok().body("Hello world!")
}

#[post("/echo")]
async fn echo(req_body: String) -> impl Responder {
    HttpResponse::Ok().body(req_body)
}



mod db_routes{

    use super::*;

    #[post("/create_user")]
    pub async fn create_user(db: web::Data<db::Pool>, info: web::Json<db::User>) -> Result<HttpResponse, Error> {
        Ok(HttpResponse::Ok().json(
            db::create_user(&db, info.0).await?
        ))
    }

    #[post("/update_user")]
    pub async fn update_user(db: web::Data<db::Pool>, info: web::Json<db::User>) -> Result<HttpResponse, Error> {
        Ok(HttpResponse::Ok().json(
            db::update_user(&db, info.0).await?
        ))
    }

    #[post("/get_user")]
    pub async fn get_user(db: web::Data<db::Pool>, user_id: web::Json<db::UserID>) -> Result<HttpResponse, Error> {
        Ok(HttpResponse::Ok().json(
            db::get_user(&db, user_id.0).await?
        ))
    }

    #[post("/delete_user")]
    pub async fn delete_user(db: web::Data<db::Pool>, info: web::Json<db::UserID>) -> Result<HttpResponse, Error> {
        Ok(HttpResponse::Ok().json(
            db::delete_user(&db, info.0).await?
        ))
    }

    #[derive(Debug, Serialize, Deserialize)]
    pub struct AllWalkInfo{
        user_id: db::UserID,
        walk: db::WalkInfo,
        conditions: Vec<db::WalkInstantInfo>,
    }

    #[post("/create_walk")]
    pub async fn create_walk(db: web::Data<db::Pool>, walk: web::Json<AllWalkInfo>) -> Result<HttpResponse, Error> {
        let walk = walk.0;
        Ok(HttpResponse::Ok().json(
            db::create_walk(&db, walk.user_id, walk.walk, walk.conditions).await?
        ))
    }

    #[post("/list_walks")]
    pub async fn list_walks(db: web::Data<db::Pool>, user_id: web::Json<db::UserID>) -> Result<HttpResponse, Error> {
        Ok(HttpResponse::Ok().json(
            db::list_walks(&db, user_id.0).await?
        ))
    }

    #[derive(Debug, Serialize, Deserialize)]
    pub struct WalkInfoId{
        user_id: db::UserID,
        walk_id: db::DbId,
    }

    #[post("/delete_walk")]
    pub async fn delete_walk(db: web::Data<db::Pool>, walk_info: web::Json<WalkInfoId>) -> Result<HttpResponse, Error> {
        let walk_info = walk_info.0;
        Ok(HttpResponse::Ok().json(
            db::delete_walk(&db, walk_info.user_id, walk_info.walk_id).await?
        ))
    }

    #[post("/list_walk_info")]
    pub async fn list_walk_info(db: web::Data<db::Pool>, walk_info: web::Json<WalkInfoId>) -> Result<HttpResponse, Error> {
        let walk_info = walk_info.0;
        Ok(HttpResponse::Ok().json(
            db::list_walk_info(&db, walk_info.user_id, walk_info.walk_id).await?
        ))
    }

    #[derive(Debug, Serialize, Deserialize)]
    pub struct WalkInfoUpdate{
        user_id: db::UserID,
        walk_id: db::DbId,
        rating: f32,
        name: Option<String>,
        comment: Option<String>
    }


    #[post("/update_walk")]
    pub async fn update_walk(db: web::Data<db::Pool>, walk_info: web::Json<WalkInfoUpdate>) -> Result<HttpResponse, Error> {
        let walk_info = walk_info.0;
        Ok(HttpResponse::Ok().json(
            db::update_walk(&db, walk_info.user_id, walk_info.walk_id, walk_info.rating, walk_info.name, walk_info.comment).await?
        ))
    }
}

fn api_config(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/db").configure(db_config)
    );
}

fn db_config(cfg: &mut web::ServiceConfig) {
    cfg
        .service(db_routes::create_user)
        .service(db_routes::update_user)
        .service(db_routes::get_user)
        .service(db_routes::delete_user)
        .service(db_routes::create_walk)
        .service(db_routes::list_walks)
        .service(db_routes::list_walk_info)
        .service(db_routes::update_walk)
        .service(db_routes::delete_walk);
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    let manager = SqliteConnectionManager::file("weather.db");
    let pool = Pool::new(manager).unwrap();
    pool.get()
        .expect("Failed to initialize DB")
        .execute_batch(include_str!("../db/db.sql"))
        .expect("Failed to initialize DB");

    HttpServer::new(move || 
            App::new()
                .app_data(web::Data::new(pool.clone()))
                .service(web::scope("/api").configure(api_config))
                .service(hello)
                .service(echo)
                .service(meow)
        )
        .bind(("0.0.0.0", 8080))?
        .run()
        .await
}
