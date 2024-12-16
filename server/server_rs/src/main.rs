use actix_web::{get, post, web, App, Error, HttpResponse, HttpServer, Responder};
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
    HttpServer::new(|| App::new().service(hello).service(echo).service(meow))
        .bind(("0.0.0.0", 8080))?
        .run()
        .await
}
