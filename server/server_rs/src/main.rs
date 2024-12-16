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

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    // connect to SQLite DB
    let manager = SqliteConnectionManager::file("weather.db");
    let pool = Pool::new(manager).unwrap();

    HttpServer::new(move || 
            App::new()
                .service(hello)
                .service(echo)
                .service(meow)
                .app_data(web::Data::new(pool.clone()))
        )
        .bind(("0.0.0.0", 8080))?
        .run()
        .await
}
