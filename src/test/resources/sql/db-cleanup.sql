use zamrad_dev;
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS=0;

delete from zamrad_dev.profile;
delete from zamrad_dev.artist_genres;
delete from zamrad_dev.music_genre;
delete from zamrad_dev.event;

drop schema zamrad_dev;

drop table artist;
drop table event;
drop table event_slot;