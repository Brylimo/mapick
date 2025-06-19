-- COMMUNITY
CREATE TABLE community (
    community_id INT AUTO_INCREMENT PRIMARY KEY,
    community_name VARCHAR(100) NOT NULL,
    registered_by INT NOT NULL,
    `desc` TEXT,
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    mod_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (registered_by) REFERENCES member(mno) ON DELETE CASCADE
);

-- COMMUNITY_MEMBER
CREATE TABLE community_member (
    community_member_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    community_id INT NOT NULL,
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    mod_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(mno) ON DELETE CASCADE,
    FOREIGN KEY (community_id) REFERENCES community(community_id) ON DELETE CASCADE
);

-- CATEGORY
CREATE TABLE category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    mod_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- COMMUNITY_CATEGORY
CREATE TABLE community_category (
    community_category_id INT AUTO_INCREMENT PRIMARY KEY,
    community_id INT NOT NULL,
    category_id INT NOT NULL,
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    mod_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (community_id) REFERENCES community(community_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE
);

-- TAG
CREATE TABLE tag (
    tag_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    mod_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- COMMUNITY_TAG
CREATE TABLE community_tag (
    community_tag_id INT AUTO_INCREMENT PRIMARY KEY,
    tag_id INT NOT NULL,
    community_id INT NOT NULL,
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    mod_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tag_id) REFERENCES tag(tag_id) ON DELETE CASCADE,
    FOREIGN KEY (community_id) REFERENCES community(community_id) ON DELETE CASCADE
);

-- COMMUNITY_LIKE
create table if not exists ssafytrip.community_like
(
    community_like_id   int auto_increment
    primary key,
    member_id int                                    not null,
    community_id  int                                not null,
    reg_dt    datetime default CURRENT_TIMESTAMP null,
    mod_dt    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint community_like_ibfk_1
    foreign key (member_id) references ssafytrip.member (MNO)
    on delete cascade,
    constraint community_like_ibfk_2
    foreign key (community_id) references ssafytrip.community (community_id)
    on delete cascade
);

-- LIKE
CREATE TABLE `like` (
    like_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    board_id BIGINT NOT NULL,
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    mod_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(mno) ON DELETE CASCADE,
    FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE
);

-- COMMENT
CREATE TABLE comment (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    board_id BIGINT NOT NULL,
    member_id INT NOT NULL,
    content TEXT NOT NULL,
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    mod_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member(mno) ON DELETE CASCADE
);

-- SUB_COMMENT
CREATE TABLE sub_comment (
    sub_comment_id INT AUTO_INCREMENT PRIMARY KEY,
    comment_id INT NOT NULL,
    member_id INT NOT NULL,
    content TEXT NOT NULL,
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    mod_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (comment_id) REFERENCES comment(comment_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member(mno) ON DELETE CASCADE
);

-- COMMUNITY_ATTRACTION
CREATE TABLE community_attraction (
    community_attraction_id INT AUTO_INCREMENT PRIMARY KEY,
    community_id INT NOT NULL,
    attraction_id INT NOT NULL,
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    mod_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (community_id) REFERENCES community(community_id) ON DELETE CASCADE,
    FOREIGN KEY (attraction_id) REFERENCES attractions(no) ON DELETE CASCADE
);

-- COMMUNITY_ATTRACTION_TAG
CREATE TABLE community_attraction_tag (
    community_attraction_tag_id INT AUTO_INCREMENT PRIMARY KEY,
    community_attraction_id INT NOT NULL,
    tag_id INT NOT NULL,
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    mod_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (community_attraction_id) REFERENCES community_attraction(community_attraction_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(tag_id) ON DELETE CASCADE
);

-- COMMUNITY_ATTRACTION_LIKE
create table community_attraction_like (
   community_attraction_like_id int auto_increment primary key,
   community_attraction_id int not null,
   member_id int not null,
   reg_dt datetime default CURRENT_TIMESTAMP,
   mod_dt datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,

   constraint fk_community_attraction_like_attraction
       foreign key (community_attraction_id) references community_attraction (community_attraction_id)
           on delete cascade,

   constraint fk_community_attraction_like_member
       foreign key (member_id) references member (mno)
           on delete cascade
);

-- 인덱스 추가
create index idx_community_attraction_id
    on community_attraction_like (community_attraction_id);

create index idx_member_id
    on community_attraction_like (member_id);

ALTER TABLE board
ADD COLUMN community_id INT NOT NULL AFTER board_id,
ADD CONSTRAINT fk_board_community
    FOREIGN KEY (community_id)
    REFERENCES community(community_id)
    ON DELETE CASCADE;

CREATE TABLE community_join_request (
    join_request_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    community_id INT NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    reason VARCHAR(500),
    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (member_id) REFERENCES member(MNO) ON DELETE CASCADE,
    FOREIGN KEY (community_id) REFERENCES community(community_id) ON DELETE CASCADE
);

