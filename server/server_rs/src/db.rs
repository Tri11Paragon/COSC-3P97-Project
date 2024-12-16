
// https://github.com/actix/examples/tree/master/databases/sqlite

use actix_web::{error, web, Error};
use rusqlite::{named_params, Statement};
use serde::{Deserialize, Serialize};

pub type Pool = r2d2::Pool<r2d2_sqlite::SqliteConnectionManager>;
pub type Connection = r2d2::PooledConnection<r2d2_sqlite::SqliteConnectionManager>;
type DbResult<T> = Result<T, rusqlite::Error>;
pub type UserID = String;

#[derive(Debug, Serialize, Deserialize)]
pub struct UserInfo{

}

async fn get_conn<F, T>(pool: &Pool, func: F) -> Result<T, Error>
        where 
            F:  FnOnce(Connection) -> DbResult<T>,
            F: Send + 'static,
            T: Send + 'static {
    let pool = pool.clone();

    let conn = web::block(move || pool.get())
        .await?
        .map_err(error::ErrorInternalServerError)?;

    web::block(move || func(conn))
    .await?
    .map_err(error::ErrorInternalServerError)
}

pub async fn get_user(pool: &Pool, user_id: UserID) -> Result<Vec<UserInfo>, Error> {
    get_conn(pool, move |conn| {
        let stmt = conn.prepare(
            "
        SELECT * FROM users
            WHERE user_id == :user_id",
        )?;
        meow(stmt, user_id)
    }).await
}

fn meow(mut stmt: Statement, user_id: UserID) -> DbResult<Vec<UserInfo>>{
    stmt.query_map(named_params![":user_id": user_id], |row| {
        Ok(UserInfo {
            // year: row.get(0)?,
            // month: row.get(1)?,
            // total: row.get(2)?,
        })
    })?.collect::<Result<Vec<UserInfo>, rusqlite::Error>>()
}