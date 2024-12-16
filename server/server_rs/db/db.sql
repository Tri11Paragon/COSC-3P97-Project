PRAGMA foreign_keys = ON;

--DROP TABLE IF EXISTS walk_instant_info;
--DROP TABLE IF EXISTS walk_info;
--DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    user_id   VARCHAR(16) NOT NULL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS walk_info(
    user_id   VARCHAR(16) NOT NULL,
    walk_id  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    start_time INT8 NOT NULL,
    end_time INT8 NOT NULL,
    name TEXT,
    comment TEXT,
    rating REAL NOT NULL,
  
  	UNIQUE(user_id, walk_id)
    
    FOREIGN KEY (user_id) 
      REFERENCES users (user_id) 
         ON DELETE CASCADE 
         ON UPDATE NO ACTION
);


CREATE TABLE IF NOT EXISTS walk_instant_info(
    walk_id INT NOT NULL,
    inst_time INT8 NOT NULL,

    lon REAL NOT NULL,
    lat REAL NOT NULL,

    FOREIGN KEY (walk_id) 
      REFERENCES walk_info (walk_id) 
         ON DELETE CASCADE 
         ON UPDATE NO ACTION
);