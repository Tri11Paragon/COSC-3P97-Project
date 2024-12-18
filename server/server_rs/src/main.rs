pub mod db;

use std::{path::{Path, PathBuf}, str::FromStr};

use actix_web::{get, guard, middleware::Logger, post, web, App, Error, HttpResponse, HttpServer, Responder};
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
        db::create_user(&db, info.0).await?;
        Ok(HttpResponse::Ok().finish())
    }

    #[post("/update_user")]
    pub async fn update_user(db: web::Data<db::Pool>, info: web::Json<db::User>) -> Result<HttpResponse, Error> {
        db::update_user(&db, info.0).await?;
        Ok(HttpResponse::Ok().finish())
    }

    #[post("/get_user")]
    pub async fn get_user(db: web::Data<db::Pool>, user_id: web::Json<db::UserID>) -> Result<HttpResponse, Error> {
        Ok(HttpResponse::Ok().json(
            db::get_user(&db, user_id.0).await?
        ))
    }

    #[post("/delete_user")]
    pub async fn delete_user(db: web::Data<db::Pool>, info: web::Json<db::UserID>) -> Result<HttpResponse, Error> {
        db::delete_user(&db, info.0).await?;
        Ok(HttpResponse::Ok().finish())
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

    #[derive(Debug, Serialize, Deserialize)]
    pub struct ListWalks{
        user_id: db::UserID,
        start: db::UnixTime,
        end: db::UnixTime,
    }

    #[post("/list_walks")]
    pub async fn list_walks(db: web::Data<db::Pool>, list_walks: web::Json<ListWalks>) -> Result<HttpResponse, Error> {
        let list_walks = list_walks.0;
        Ok(HttpResponse::Ok().json(
            db::list_walks(&db, list_walks.user_id, list_walks.start, list_walks.end).await?
        ))
    }

    #[derive(Debug, Serialize, Deserialize)]
    pub struct WalkInfoId{
        pub(super) user_id: db::UserID,
        pub(super) walk_id: db::DbId,
    }

    #[post("/delete_walk")]
    pub async fn delete_walk(db: web::Data<db::Pool>, walk_info: web::Json<WalkInfoId>) -> Result<HttpResponse, Error> {
        let walk_info = walk_info.0;

        db::delete_walk(&db, walk_info.user_id, walk_info.walk_id).await?;
        Ok(HttpResponse::Ok().finish())
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
        db::update_walk(&db, walk_info.user_id, walk_info.walk_id, walk_info.rating, walk_info.name, walk_info.comment).await?;
        Ok(HttpResponse::Ok().finish())
    }
}

mod analysis_routes{
    use super::*;

    #[derive(Debug, Serialize, Deserialize)]
    pub struct InstantInfo{
        pub(super) user_id: db::UserID,
        pub(super) conditions: db::WeatherInfo,
    }

    #[post("/analyze_walk_conditions")]
    pub async fn analyze_walk_conditions(_db: web::Data<db::Pool>, _walk_info: web::Json<InstantInfo>) -> Result<HttpResponse, Error> {
        let number = 1.0;
        Ok(HttpResponse::Ok().json(number))
    }
}

fn api_config(cfg: &mut web::ServiceConfig) {
    cfg
    .service(web::scope("/db").configure(db_config))
    .service(web::scope("/analysis").configure(analysis_config));
}

fn analysis_config(cfg: &mut web::ServiceConfig) {
    cfg
        .service(analysis_routes::analyze_walk_conditions);
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
    
    env_logger::init_from_env(env_logger::Env::new().default_filter_or("info"));
    
    let path = std::env::var("DB_PATH")
        .map(PathBuf::from)
        .map(|mut v|{v.push("weather.db");v})
        .unwrap_or("weather.db".into());

    let manager = SqliteConnectionManager::file(path);
    let pool = Pool::new(manager).unwrap();
    pool.get()
        .expect("Failed to initialize DB")
        .execute_batch(include_str!("../db/db.sql"))
        .expect("Failed to initialize DB");

    HttpServer::new(move || 
            App::new()
                .wrap(Logger::default())
                .app_data(web::Data::new(pool.clone()))
                .service(
                    web::scope("/api")
                        .configure(api_config)
                        .guard(guard::Header("x-meow", "this is a really absolutely secure token that will prevent all spam in user creation"))
                    )
                .service(hello)
                .service(echo)
                .service(meow)
        )
        .bind(("0.0.0.0", 8080))?
        .run()
        .await
}
