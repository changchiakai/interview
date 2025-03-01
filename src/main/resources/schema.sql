set
mode MySQL;

DROP TABLE IF EXISTS tmp;
CREATE TABLE tmp
(
    key   VARCHAR(50) NOT NULL,
    value VARCHAR(200) DEFAULT NULL,
    desc  VARCHAR(200) DEFAULT NULL,
    PRIMARY KEY (key)
);


DROP TABLE IF EXISTS members;
CREATE TABLE members
(
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    email     VARCHAR(255) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    name      VARCHAR(100) NOT NULL
);

CREATE TABLE blacklisted_tokens
(
    token       VARCHAR(500) PRIMARY KEY,
    expiry_time TIMESTAMP
);


CREATE TABLE profile_operation_log
(
    operation   VARCHAR(500),
    update_time TIMESTAMP PRIMARY KEY
);

CREATE TABLE member_picture
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    member_id     INT  NOT NULL,
    pictureBase64 TEXT NOT NULL
);

CREATE TABLE member_interest
(
    member_id         INT     NOT NULL PRIMARY KEY,
    is_movie_checked  BOOLEAN NOT NULL DEFAULT FALSE,
    is_food_checked   BOOLEAN NOT NULL DEFAULT FALSE,
    is_sport_checked  BOOLEAN NOT NULL DEFAULT FALSE,
    is_travel_checked BOOLEAN NOT NULL DEFAULT FALSE,
    is_music_checked  BOOLEAN NOT NULL DEFAULT FALSE
);
