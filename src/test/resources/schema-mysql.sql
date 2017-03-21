CREATE TABLE zamrad_dev.artist_genres (
  profile_id BINARY(16) NOT NULL,
  genre_id   BINARY(16) NOT NULL,
  PRIMARY KEY (profile_id, genre_id)
);

CREATE TABLE zamrad_dev.event (
  id                          BINARY(16) NOT NULL,
  address                     VARCHAR(255),
  cancellation_policy         VARCHAR(255),
  date                        TINYBLOB,
  description                 VARCHAR(255),
  payment_type                VARCHAR(255),
  photo_url                   VARCHAR(255),
  status                      VARCHAR(255),
  title                       VARCHAR(255),
  type                        VARCHAR(255),
  venue_capacity              BIGINT,
  venue_type                  VARCHAR(255),
  event_additional_details_id BINARY(16),
  profile_id                  BINARY(16),
  PRIMARY KEY (id)
);

CREATE TABLE zamrad_dev.event_additional_details (
  id                BINARY(16) NOT NULL,
  age_restriction   CHAR(1),
  dj_booth          CHAR(1),
  noise_restriction CHAR(1),
  pa_system         CHAR(1),
  sound_engineer    CHAR(1),
  stage_depth       BIGINT,
  stage_lighting    CHAR(1),
  stage_width       BIGINT,
  venue_capacity    BIGINT,
  wheelchair        CHAR(1),
  PRIMARY KEY (id)
);

CREATE TABLE zamrad_dev.event_slot (
  id          BINARY(16) NOT NULL,
  artist_type VARCHAR(255),
  end_time    VARCHAR(255),
  genre       VARCHAR(255),
  price       DECIMAL(19, 2),
  start_time  VARCHAR(255),
  status      VARCHAR(255),
  event_id    BINARY(16),
  PRIMARY KEY (id)
);

CREATE TABLE zamrad_dev.event_slot_pitchers (
  profile_id    BINARY(16) NOT NULL,
  event_slot_id BINARY(16) NOT NULL,
  PRIMARY KEY (profile_id, event_slot_id)
);

CREATE TABLE zamrad_dev.music_genre (
  id   BINARY(16) NOT NULL,
  name VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE zamrad_dev.post (
  id              BINARY(16) NOT NULL,
  content         VARCHAR(255),
  date_time       TINYBLOB,
  link            VARCHAR(255),
  poster_id       BINARY(16),
  tagged_profiles VARCHAR(255),
  title           VARCHAR(255),
  type            VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE zamrad_dev.post_image (
  id      BINARY(16) NOT NULL,
  url     VARCHAR(255),
  post_id BINARY(16),
  PRIMARY KEY (id)
);

CREATE TABLE zamrad_dev.profile_showcase (
  id BINARY(16) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE zamrad_dev.profiles (
  id                  BINARY(16) NOT NULL,
  address             VARCHAR(255),
  alias               VARCHAR(255),
  average_rating      DOUBLE PRECISION,
  bio                 VARCHAR(255),
  email               VARCHAR(255),
  facebook_id         BIGINT,
  first_name          VARCHAR(255),
  gender              VARCHAR(255),
  max_travel_distance INTEGER,
  minimum_fee         DECIMAL(19, 2),
  mobile_number       VARCHAR(255),
  photo_url           VARCHAR(255),
  profile_type        VARCHAR(255),
  second_name         VARCHAR(255),
  type                VARCHAR(255),
  years_experience    INTEGER,
  PRIMARY KEY (id)
);

CREATE TABLE zamrad_dev.review (
  id         BINARY(16) NOT NULL,
  content    VARCHAR(255),
  rating     DOUBLE PRECISION,
  title      VARCHAR(255),
  profile_id BINARY(16),
  PRIMARY KEY (id)
);

CREATE TABLE zamrad_dev.showcase_image (
  id          BINARY(16) NOT NULL,
  image_url   VARCHAR(255),
  showcase_id BINARY(16),
  PRIMARY KEY (id)
);

CREATE TABLE zamrad_dev.stripe_charge (
  id          BINARY(16) NOT NULL,
  amount      DECIMAL(19, 2),
  captured    VARCHAR(255),
  charge_id   VARCHAR(255),
  customer_id VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE zamrad_dev.stripe_customer (
  id             BINARY(16) NOT NULL,
  customer_token VARCHAR(255),
  email          VARCHAR(255),
  profile        BINARY(16),
  PRIMARY KEY (id)
);

ALTER TABLE zamrad_dev.artist_genres
  ADD CONSTRAINT FK_my5sftj3xpdqc2ea17qbnjsad
FOREIGN KEY (genre_id)
REFERENCES zamrad_dev.music_genre (id);

ALTER TABLE zamrad_dev.artist_genres
  ADD CONSTRAINT FK_7lkoyqkv94m3t4o7vkjfcvtay
FOREIGN KEY (profile_id)
REFERENCES zamrad_dev.profiles (id);

ALTER TABLE zamrad_dev.event
  ADD CONSTRAINT FK_ej0hn6j64ju38qand90p6q499
FOREIGN KEY (event_additional_details_id)
REFERENCES zamrad_dev.event_additional_details (id);

ALTER TABLE zamrad_dev.event
  ADD CONSTRAINT FK_h3l8glmo3itbtihgth000p0q7
FOREIGN KEY (profile_id)
REFERENCES zamrad_dev.profiles (id);

ALTER TABLE zamrad_dev.event_slot
  ADD CONSTRAINT FK_257tbtbe2xe1a4git0yqxe9h6
FOREIGN KEY (event_id)
REFERENCES zamrad_dev.event (id);

ALTER TABLE zamrad_dev.event_slot_pitchers
  ADD CONSTRAINT FK_rdghqgi6alx5dma63kfia3hil
FOREIGN KEY (event_slot_id)
REFERENCES zamrad_dev.profiles (id);

ALTER TABLE zamrad_dev.event_slot_pitchers
  ADD CONSTRAINT FK_bh50952majcqdoc5qdkxmlnxt
FOREIGN KEY (profile_id)
REFERENCES zamrad_dev.event_slot (id);

ALTER TABLE zamrad_dev.post_image
  ADD CONSTRAINT FK_af6whs3lmpc7q6p1xnyv9kpux
FOREIGN KEY (post_id)
REFERENCES zamrad_dev.post (id);

ALTER TABLE zamrad_dev.review
  ADD CONSTRAINT FK_5bqt2a266wqtsf7lf31flr7ce
FOREIGN KEY (profile_id)
REFERENCES zamrad_dev.profiles (id);

ALTER TABLE zamrad_dev.showcase_image
  ADD CONSTRAINT FK_axlxbxbujmiut7vihpgwqcwer
FOREIGN KEY (showcase_id)
REFERENCES zamrad_dev.profile_showcase (id);

ALTER TABLE zamrad_dev.stripe_customer
  ADD CONSTRAINT FK_3ddw82j3ccr23ikeys6wshqhl
FOREIGN KEY (profile)
REFERENCES zamrad_dev.profiles (id);
