INSERT INTO `tmp` (`key`, `value`, `desc`) VALUES ('System.Start', NOW(), 'System start time.');

INSERT INTO `members` (`email`, `name`, `password`) VALUES ('test@gmail.com','testname','555');


INSERT INTO member_interest (member_id, is_movie_checked, is_food_checked, is_sport_checked, is_travel_checked, is_music_checked)
VALUES (1, TRUE, FALSE, TRUE, FALSE, TRUE)
    ON DUPLICATE KEY UPDATE
                         is_movie_checked = VALUES(is_movie_checked),
                         is_food_checked = VALUES(is_food_checked),
                         is_sport_checked = VALUES(is_sport_checked),
                         is_travel_checked = VALUES(is_travel_checked),
                         is_music_checked = VALUES(is_music_checked);
