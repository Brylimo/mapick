USE SSAFYTRIP;

create table contenttypes
(
    content_type_id   int  not null comment '콘텐츠타입번호'
        primary key,
    content_type_name varchar(45) null comment '콘텐츠타입이름'
)
    comment '콘텐츠타입정보테이블';

create table member
(
    MNO      int auto_increment
        primary key,
    NAME     varchar(45) not null,
    EMAIL    varchar(45) not null,
    PASSWORD varchar(225) not null,
    ROLE     varchar(45) null
);

create table board
(
    board_id   bigint auto_increment
        primary key,
    title      varchar(255)                        not null,
    content    text                                not null,
    author     int                                 not null,
    created_at timestamp default CURRENT_TIMESTAMP null,
    updated_at timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint board_ibfk_1
        foreign key (author) references member (MNO)
            on delete cascade
);

create index author
    on board (author);

create table notice
(
    notice_id  bigint auto_increment
        primary key,
    title      varchar(255)                        not null,
    content    text                                not null,
    author     int                                 not null,
    created_at timestamp default CURRENT_TIMESTAMP null,
    updated_at timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint notice_ibfk_1
        foreign key (author) references member (MNO)
            on delete cascade
);

create index author
    on notice (author);

create table plan
(
    PID         int auto_increment
        primary key,
    MNO         int      not null,
    NAME        VARCHAR(100) NOT NULL,
    EXP         text     null,
    CREATEDDATE datetime not null,
    UPDATEDDATE datetime null,
    constraint plan_ibfk_1
        foreign key (MNO) references member (MNO) ON DELETE CASCADE
);

create index MNO
    on plan (MNO);

create table sidos
(
    no        int auto_increment comment '시도번호'
        primary key,
    sido_code int         not null comment '시도코드',
    sido_name varchar(20) null comment '시도이름',
    constraint sido_code_UNIQUE
        unique (sido_code)
)
    comment '시도정보테이블';

create table guguns
(
    no         int auto_increment comment '구군번호'
        primary key,
    sido_code  int         not null comment '시도코드',
    gugun_code int         not null comment '구군코드',
    gugun_name varchar(20) null comment '구군이름',
    constraint sido_gugun_unique
        unique (sido_code, gugun_code),
    constraint guguns_sido_to_sidos_code_fk
        foreign key (sido_code) references sidos (sido_code)
)
    comment '구군정보테이블';

create table attractions
(
    no              int auto_increment comment '명소코드'
        primary key,
    content_id      int             null comment '콘텐츠번호',
    title           varchar(500)    null comment '명소이름',
    content_type_id int             null comment '콘텐츠타입',
    area_code       int             null comment '시도코드',
    si_gun_gu_code  int             null comment '구군코드',
    first_image1    varchar(100)    null comment '이미지경로1',
    first_image2    varchar(100)    null comment '이미지경로2',
    map_level       int             null comment '줌레벨',
    latitude        decimal(20, 17) null comment '위도',
    longitude       decimal(20, 17) null comment '경도',
    tel             varchar(20)     null comment '전화번호',
    addr1           varchar(100)    null comment '주소1',
    addr2           varchar(100)    null comment '주소2',
    homepage        varchar(1000)   null comment '홈페이지',
    overview        varchar(10000)  null comment '설명',
    constraint attractions_area_to_sidos_code_fk
        foreign key (area_code) references sidos (sido_code),
    constraint attractions_sigungu_to_guguns_gugun_fk
        foreign key (area_code, si_gun_gu_code) references guguns (sido_code, gugun_code),
    constraint attractions_typeid_to_types_typeid_fk
        foreign key (content_type_id) references contenttypes (content_type_id)
)
    comment '명소정보테이블';

create index attractions_sido_to_sidos_code_fk_idx
    on attractions (area_code);

create index attractions_sigungu_to_guguns_gugun_fk_idx
    on attractions (si_gun_gu_code);

create index attractions_typeid_to_types_typeid_fk_idx
    on attractions (content_type_id);

create index gugun_code_idx
    on guguns (gugun_code);

create index guguns_sido_to_sidos_code_fk_idx
    on guguns (sido_code);

create table hotplace
(
    HPID         int auto_increment
        primary key,
    MNO          int      not null,
    CREATEDDATE  datetime not null,
    ATTRACTIONID int      not null,
    constraint hotplace_ibfk_1
        foreign key (MNO) references member (MNO) ON DELETE CASCADE,
    constraint hotplace_ibfk_2
        foreign key (ATTRACTIONID) references attractions (no) ON DELETE CASCADE
);

create index ATTRACTIONID
    on hotplace (ATTRACTIONID);

create index MNO
    on hotplace (MNO);

create table plan_attraction
(
    PPID         int auto_increment
        primary key,
    IDX          int not null,
    PLANID       int not null,
    ATTRACTIONID int not null,
    TIME        TIME DEFAULT NULL,
    EXP          VARCHAR(100),
    constraint plan_attraction_ibfk_1
        foreign key (PLANID) references plan (PID) ON DELETE CASCADE,
    constraint plan_attraction_ibfk_2
        foreign key (ATTRACTIONID) references attractions (no) ON DELETE CASCADE
);

create index ATTRACTIONID
    on plan_attraction (ATTRACTIONID);

create index PLANID
    on plan_attraction (PLANID);

CREATE TABLE plan_expense (
  expid INT AUTO_INCREMENT PRIMARY KEY,
  plan_id INT NOT NULL,
  content VARCHAR(100) NOT NULL,
  price INT NOT NULL,
  reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (plan_id) REFERENCES plan(pid) ON DELETE CASCADE
);
ALTER TABLE plan_expense ADD COLUMN receipt_path VARCHAR(512);

CREATE TABLE reserved_board (
    reserved_board_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    community_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author INT NOT NULL,
    reserved_time DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reserved_board_community
        FOREIGN KEY (community_id)
        REFERENCES community(community_id)
        ON DELETE CASCADE
);

