
    create table zamrad_dev.artist_genres (
        profile_id BINARY(16) not null,
        genre_id BINARY(16) not null,
        primary key (profile_id, genre_id)
    );

    create table zamrad_dev.event (
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
        event_additional_details_id BINARY(16),
        profile_id                  BINARY(16),
        primary key (id)
    );

    create table zamrad_dev.event_additional_details (
        id BINARY(16) not null,
        age_restriction char(1),
        dj_booth char(1),
        noise_restriction char(1),
        pa_system char(1),
        sound_engineer char(1),
        stage_depth bigint,
        stage_lighting char(1),
        stage_width bigint,
        venue_capacity bigint,
        wheelchair char(1),
        primary key (id)
    );

    create table zamrad_dev.event_slot (
        id BINARY(16) not null,
        artist_type varchar(255),
        end_time varchar(255),
        genre varchar(255),
        price decimal(19,2),
        start_time varchar(255),
        status varchar(255),
        event_id BINARY(16),
        primary key (id)
    );

    create table zamrad_dev.event_slot_pitchers (
        profile_id BINARY(16) not null,
        event_slot_id BINARY(16) not null,
        primary key (profile_id, event_slot_id)
    );

    create table zamrad_dev.music_genre (
        id BINARY(16) not null,
        name varchar(255),
        primary key (id)
    );

    CREATE TABLE zamrad_dev.profile_showcase (
        id BINARY(16) NOT NULL,
        PRIMARY KEY (id)
    );

    create table zamrad_dev.profiles (
        id                  BINARY(16) NOT NULL,
        address             VARCHAR(255),
        alias               VARCHAR(255),
        average_rating      DOUBLE PRECISION,
        bio                 VARCHAR(255),
        email               VARCHAR(255),
        facebook_id         BIGINT,
        first_name          VARCHAR(255),
        gender              VARCHAR(255),
        max_travel_distance integer,
        minimum_fee         DECIMAL(19, 2),
        mobile_number       VARCHAR(255),
        photo_url           VARCHAR(255),
        profile_type        VARCHAR(255),
        second_name         VARCHAR(255),
        type                VARCHAR(255),
        years_experience    INTEGER,
        stripe_customer     BINARY(16),
        primary key (id)
    );

    create table zamrad_dev.review (
        id BINARY(16) not null,
        content varchar(255),
        rating double precision,
        title varchar(255),
        profile_id BINARY(16),
        primary key (id)
    );

    CREATE TABLE zamrad_dev.showcase_image (
        id          BINARY(16) NOT NULL,
        image_url   VARCHAR(255),
        showcase_id BINARY(16),
        PRIMARY KEY (id)
    );

    CREATE TABLE zamrad_dev.stripe_customer (
        id             BINARY(16) NOT NULL,
        customer_token VARCHAR(255),
        email          VARCHAR(255),
        profile        BINARY(16),
        PRIMARY KEY (id)
    );

    alter table zamrad_dev.artist_genres 
        add constraint FK_my5sftj3xpdqc2ea17qbnjsad 
        foreign key (genre_id) 
        references zamrad_dev.music_genre (id);

    alter table zamrad_dev.artist_genres 
        add constraint FK_7lkoyqkv94m3t4o7vkjfcvtay 
        foreign key (profile_id) 
        references zamrad_dev.profiles (id);

    ALTER TABLE zamrad_dev.event
        ADD CONSTRAINT FK_ej0hn6j64ju38qand90p6q499
    FOREIGN KEY (event_additional_details_id)
    REFERENCES zamrad_dev.event_additional_details (id);

    alter table zamrad_dev.event 
        add constraint FK_h3l8glmo3itbtihgth000p0q7 
        foreign key (profile_id) 
        references zamrad_dev.profiles (id);

    alter table zamrad_dev.event_slot 
        add constraint FK_257tbtbe2xe1a4git0yqxe9h6 
        foreign key (event_id) 
        references zamrad_dev.event (id);

    alter table zamrad_dev.event_slot_pitchers 
        add constraint FK_rdghqgi6alx5dma63kfia3hil 
        foreign key (event_slot_id) 
        references zamrad_dev.profiles (id);

    alter table zamrad_dev.event_slot_pitchers 
        add constraint FK_bh50952majcqdoc5qdkxmlnxt 
        foreign key (profile_id) 
        references zamrad_dev.event_slot (id);

    ALTER TABLE zamrad_dev.profiles
        ADD CONSTRAINT FK_jy3brtigb3fhp0clhnq54ao8s
    FOREIGN KEY (stripe_customer)
    REFERENCES zamrad_dev.stripe_customer (id);

    alter table zamrad_dev.review 
        add constraint FK_5bqt2a266wqtsf7lf31flr7ce 
        foreign key (profile_id) 
        references zamrad_dev.profiles (id);

    ALTER TABLE zamrad_dev.showcase_image
        ADD CONSTRAINT FK_axlxbxbujmiut7vihpgwqcwer
    FOREIGN KEY (showcase_id)
    REFERENCES zamrad_dev.profile_showcase (id);

    ALTER TABLE zamrad_dev.stripe_customer
        ADD CONSTRAINT FK_3ddw82j3ccr23ikeys6wshqhl
    FOREIGN KEY (profile)
    REFERENCES zamrad_dev.profiles (id);
