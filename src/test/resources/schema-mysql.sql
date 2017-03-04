
    create table zamrad_dev.artist_genres (
        profile_id BINARY(16) not null,
        genre_id BINARY(16) not null,
        primary key (profile_id, genre_id)
    );

    create table zamrad_dev.event (
        id BINARY(16) not null,
        address varchar(255),
        cancellation_policy varchar(255),
        date tinyblob,
        description varchar(255),
        payment_type varchar(255),
        photo_url varchar(255),
        status varchar(255),
        title varchar(255),
        type varchar(255),
        venue_capacity bigint,
        venue_type varchar(255),
        event_additional_details_id BINARY(16),
        profile_id BINARY(16),
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

    create table zamrad_dev.profile_showcase (
        id BINARY(16) not null,
        primary key (id)
    );

    create table zamrad_dev.profiles (
        id BINARY(16) not null,
        address varchar(255),
        alias varchar(255),
        average_rating double precision,
        bio varchar(255),
        email varchar(255),
        facebook_id bigint,
        first_name varchar(255),
        gender varchar(255),
        max_travel_distance integer,
        minimum_fee decimal(19,2),
        mobile_number varchar(255),
        photo_url varchar(255),
        profile_type varchar(255),
        second_name varchar(255),
        type varchar(255),
        years_experience integer,
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

    create table zamrad_dev.showcase_image (
        id BINARY(16) not null,
        image_url varchar(255),
        showcase_id BINARY(16),
        primary key (id)
    );

    create table zamrad_dev.stripe_charge (
        id BINARY(16) not null,
        amount decimal(19,2),
        captured varchar(255),
        charge_id varchar(255),
        customer_id varchar(255),
        primary key (id)
    );

    create table zamrad_dev.stripe_customer (
        id BINARY(16) not null,
        customer_token varchar(255),
        email varchar(255),
        primary key (id)
    );

    alter table zamrad_dev.artist_genres 
        add constraint FK_my5sftj3xpdqc2ea17qbnjsad 
        foreign key (genre_id) 
        references zamrad_dev.music_genre (id);

    alter table zamrad_dev.artist_genres 
        add constraint FK_7lkoyqkv94m3t4o7vkjfcvtay 
        foreign key (profile_id) 
        references zamrad_dev.profiles (id);

    alter table zamrad_dev.event 
        add constraint FK_ej0hn6j64ju38qand90p6q499 
        foreign key (event_additional_details_id) 
        references zamrad_dev.event_additional_details (id);

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

    alter table zamrad_dev.review 
        add constraint FK_5bqt2a266wqtsf7lf31flr7ce 
        foreign key (profile_id) 
        references zamrad_dev.profiles (id);

    alter table zamrad_dev.showcase_image 
        add constraint FK_axlxbxbujmiut7vihpgwqcwer 
        foreign key (showcase_id) 
        references zamrad_dev.profile_showcase (id);
