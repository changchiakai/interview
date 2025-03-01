set mode MySQL;

DROP TABLE IF EXISTS tmp;
CREATE TABLE tmp (
    key VARCHAR(50) NOT NULL, 
    value VARCHAR(200) DEFAULT NULL, 
    desc VARCHAR(200) DEFAULT NULL, 
    PRIMARY KEY (key)
);


DROP TABLE IF EXISTS members;
CREATE TABLE members (
     member_id INT AUTO_INCREMENT PRIMARY KEY,
     email VARCHAR(255) NOT NULL UNIQUE,
     password VARCHAR(255) NOT NULL,
     name VARCHAR(100) NOT NULL
);

CREATE TABLE blacklisted_tokens (
    token VARCHAR(500) PRIMARY KEY,
    expiry_time TIMESTAMP
);


CREATE TABLE profile_operation_log (
    operation VARCHAR(500) ,
    update_time TIMESTAMP PRIMARY KEY
);
