
// https://github.com/actix/examples/tree/master/databases/sqlite

use actix_web::{error, web, Error};
use rusqlite::{named_params, Params, Statement};
use serde::{Deserialize, Serialize};

pub type Pool = r2d2::Pool<r2d2_sqlite::SqliteConnectionManager>;
pub type Connection = r2d2::PooledConnection<r2d2_sqlite::SqliteConnectionManager>;
type DbResult<T> = Result<T, rusqlite::Error>;


pub type UserID = String;
pub type UnixTime = u64;
pub type DbId = u64;

#[derive(Debug, Serialize, Deserialize)]
pub struct User{
    id: UserID,
    name: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct WalkInfo{
    id: Option<DbId>,
    start: UnixTime,
    end: UnixTime,
    name: Option<String>,
    comment: Option<String>,
    rating: f32,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct WalkInstantInfo{
    time: UnixTime,
    lon: f32,
    lat: f32,
    conditions: WeatherInfo,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct WeatherInfo{}

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

pub async fn create_user(pool: &Pool, user: User) -> Result<(), Error> {
    let created = get_conn(pool, move |conn| {
        conn.execute(
            "
            --PRAGMA foreign_keys = ON;
            INSERT INTO users(user_id, name)
            VALUES(:user_id, :name);", 
        named_params! [":user_id": user.id, ":name": user.name])
    }).await?;

    if created == 1{
        Ok(())
    }else{
        Err(error::ErrorInternalServerError("Failed to create user"))
    }
}

pub async fn create_walk(pool: &Pool, user_id: UserID, walk: WalkInfo, conditions: Vec<WalkInstantInfo>) -> Result<DbId, Error> {
    let id = get_conn(pool, move |mut conn| {
        let trans = conn.transaction()?;
    
        let id = trans.query_row(
            "
            --PRAGMA foreign_keys = ON;
            INSERT INTO walk_info(user_id, start_time, end_time, rating, name, comment)
            VALUES(:user_id, :start_time, :end_time, :rating, :name, :comment)
            RETURNING walk_id;",
            named_params! [
                ":user_id": user_id, 
                ":start_time": walk.start, 
                ":end_time": walk.end, 
                ":rating": walk.rating, 
                ":name": walk.name, 
                ":comment": walk.comment
            ],|row|{
                row.get::<_, u64>(0)
            })?;

            let mut stmt = trans.prepare_cached("
            --PRAGMA foreign_keys = ON;
            INSERT INTO walk_instant_info(walk_id, inst_time, lon, lat)
            VALUES(:walk_id, :inst_time, :lon, :lat);"
            )?;
            for condition in conditions{
                let updated = stmt.execute(named_params![":walk_id": id, ":inst_time": condition.time, ":lon": condition.lon, ":lat": condition.lat])?;
                if updated != 1{
                    return Err(rusqlite::Error::InvalidQuery)
                }
            }
            drop(stmt);
            trans.commit()?;
            Ok(id)
    }).await?;

    Ok(id)
}

pub async fn update_walk(pool: &Pool, user_id: UserID, walk_id: DbId, rating: f32, name: Option<String>, comment: Option<String>) -> Result<(), Error>{
    let update = get_conn(pool, move |conn| {
        conn.execute(
            "
            --PRAGMA foreign_keys = ON;
            UPDATE walk_info
            SET name=:name, comment=:comment, rating=:rating
            WHERE user_id=:user_id AND walk_id=:walk_id
            ",
        named_params! [":user_id": user_id, ":walk_id": walk_id, ":rating": rating, ":name": name, ":comment": comment])
    }).await?;

    if update == 1{
        Ok(())
    }else{
        Err(error::ErrorInternalServerError("Failed to update walk"))
    }
}

pub async fn delete_walk(pool: &Pool, user_id: UserID, walk_id: DbId) -> Result<(), Error>{
    let deleted = get_conn(pool, move |conn| {
        conn.execute(
            "
            --PRAGMA foreign_keys = ON;
            DELETE FROM walk_info
            WHERE user_id = :user_id AND walk_id=:walk_id",
            named_params! [
                ":user_id": user_id, 
                ":walk_id": walk_id,
            ])
    }).await?;

    if deleted == 1{
        Ok(())
    }else{
        Err(error::ErrorInternalServerError("Failed to delete walk"))
    }
}

pub async fn update_user(pool: &Pool, user: User) -> Result<(), Error> {
    let created = get_conn(pool, move |conn| {
        conn.execute(
            "
            --PRAGMA foreign_keys = ON;
            UPDATE users
            SET name=:name
            WHERE user_id=:user_id
            ",
        named_params! [":user_id": user.id, ":name": user.name])
    }).await?;

    if created == 1{
        Ok(())
    }else{
        Err(error::ErrorInternalServerError("Failed to create user"))
    }
}

pub async fn get_user(pool: &Pool, user_id: UserID) -> Result<User, Error> {
    get_conn(pool, move |conn| {
        conn.query_row(
            "
            --PRAGMA foreign_keys = ON;
        SELECT name FROM users
            WHERE user_id = :user_id",
            named_params![":user_id": user_id.clone()], |row| {
                Ok(User {
                    id: user_id,
                    name: row.get(0)?
                })
            }
        )
    }).await
}

pub async fn delete_user(pool: &Pool, user_id: UserID) -> Result<(), Error> {
    let deleted: usize = get_conn(pool, move |conn| {
        conn.execute(
            "
            --PRAGMA foreign_keys = ON;
            DELETE FROM users
            WHERE user_id = :user_id",
            named_params![":user_id": user_id.clone()]
        )
    }).await?;

    if deleted == 1{
        Ok(())
    }else{
        Err(error::ErrorInternalServerError("Failed to delete user"))
    }
}


pub async fn list_walks(pool: &Pool, user_id: UserID, start: UnixTime, end: UnixTime) -> Result<Vec<WalkInfo>, Error> {
    get_conn(pool, move |conn| {
        let stmt = conn.prepare(
            "
            --PRAGMA foreign_keys = ON;
        SELECT walk_id, start_time, end_time, name, comment, rating FROM walk_info
            WHERE user_id = :user_id AND :start <= start_time AND start_time <= :end 
            ORDER BY start_time DESC;",
        )?;
        make_list_walks(stmt, named_params![":user_id": user_id, ":start": start, ":end": end])
    }).await
}

fn make_list_walks(mut stmt: Statement, params: impl Params) -> DbResult<Vec<WalkInfo>>{
    stmt.query_map(params, |row| {
        Ok(WalkInfo {
            id: Some(row.get(0)?),
            start: row.get(1)?,
            end: row.get(2)?,
            name: row.get(3)?,
            comment: row.get(4)?,
            rating: row.get(5)?,
        })
    })?.collect()
}



pub async fn list_walk_info(pool: &Pool, user_id: UserID, walk_id: DbId) -> Result<Vec<WalkInstantInfo>, Error> {
    get_conn(pool, move |conn| {
        let stmt = conn.prepare(
            "
            --PRAGMA foreign_keys = ON;
            SELECT inst_time, lon, lat, walk_info.walk_id, walk_info.user_id 
            FROM walk_instant_info
            INNER JOIN walk_info ON walk_info.walk_id=walk_instant_info.walk_id AND user_id = :user_id AND walk_instant_info.walk_id = :walk_id
            ORDER BY
                inst_time ASC
        ",
        )?;
        make_list_walk_info(stmt, named_params![":user_id": user_id, ":walk_id": walk_id])
    }).await
}

fn make_list_walk_info(mut stmt: Statement, params: impl Params) -> DbResult<Vec<WalkInstantInfo>>{
    stmt.query_map(params, |row| {
        Ok(WalkInstantInfo {
            time: row.get(0)?,
            lon: row.get(1)?, 
            lat: row.get(2)?,
            conditions: WeatherInfo { 
            },
        })
    })?.collect()
}
