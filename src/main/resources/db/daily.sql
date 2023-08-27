drop database if exists daily;
create database daily;

use daily;

# 用户基本信息表
create table daily_user
(
    `id`          int(11)      not null auto_increment comment 'id',
    `email`       varchar(32) unique comment '用户创建时的邮箱',
    `username`    varchar(10)  not null default '' comment '用户名',
    `password`    varchar(40)  not null default '' comment '用户加密后的密码',
    `salt`        varchar(10)  not null default 'daily' comment '用户加密的盐值',
    `introduce`   varchar(255) not null default '' comment '用户的自我介绍',
    `image`       varchar(255)          default '' comment '用户头像',
    `sex`         tinyint(1)            default '1' comment '用户性别 1:男, 0:女',
    `status`      tinyint(1)            default 1 comment '用户状态  1:正常 , 0:禁用',
    `is_admin`    tinyint(1)            default 0 comment '是否是管理员 1:是  0. 不是',
    `create_time` DATETIME              default null comment '账号创建时间',
    `update_time` DATETIME              default null comment '账号修改时间',
    `is_delete`   tinyint(1)            default 0 comment '逻辑删除',
    primary key (`id`)
) comment '用户账号的基本信息';


/*
 用户粉丝关系表
 */
# create table daily_user_concern
# (
#     `id`           int(11) not null auto_increment comment 'id',
#     `user_id`      int(11) not null comment '用户id',
#     `concern`      int(11) not null comment '用户粉丝id',
#     `concern_name` varchar(20) default null comment '用户粉丝的名称',
#     `create_time`  DATETIME    default null comment '被粉丝关注时间',
#     `is_delete`    tinyint(1)  default 0 comment '逻辑删除',
#     primary key (`id`)
# ) comment '用户的自我介绍, 日记本';

/*
 用户关注关系表
 */
# create table daily_user_follow
# (
#     `id`          int(11) not null auto_increment comment 'id',
#     `user_id`     int(11) not null comment '用户id',
#     `follow`      int(11) not null comment '用户关注的人的id',
#     `follow_name` varchar(20) default '' comment '用户关注的人的名称',
#     `create_time` DATETIME    default null comment '关注时间',
#     `is_delete`   tinyint(1)  default 0 comment '逻辑删除',
#     primary key (`id`)
# ) comment '用户的自我介绍,日记本';


/*
 日记本
 */
# create table daily_book
# (
#     `id`          int(11)     not null auto_increment comment 'id',
#     `user_id`     int(11)     not null comment '用户id, 一个用户可以有多个日记本',
#     `book_title`  varchar(10) not null default '' comment '日记本名称',
#     `create_time` DATETIME             default null comment '日记本创建时间',
#     `update_time` DATETIME             default null comment '日记本修改时间',
#     `is_delete`   tinyint(1)           default 0 comment '逻辑删除',
#     primary key (`id`),
#     unique key (`user_id`)
# ) comment '用户和日记本(book)的对应关系';


/*
 用户单篇日记
 */
drop table if exists daily_info;
create table daily_info
(
    `id`          int(11)  auto_increment  not null comment '日记id',
    `user_id`     int(11)     not null comment '用户id',
    `daily_title` varchar(20) not null default '' comment '日记标题',
    `book_title`  varchar(10) not null default '' comment '日记本和日记的对应关系, book-所属的日记本',
    `content`     text COLLATE utf8mb4_unicode_ci comment '日记文本',
    `is_public`   tinyint(1)  default 1 comment '是否公开 1:公开  0私有',
    `image`       varchar(255) COLLATE utf8mb4_unicode_ci comment '文章图片url',
    `views`       int(10)              default '0' comment '用户日记被观看量',
    `likes`       int(10)              default '0' comment '全部日记被点赞量',
    `create_time` datetime                 default null comment '日记创建时间',
    `update_time` datetime                 default null comment '日记修改时间',
    `is_delete`   tinyint(1)           default 0 comment '逻辑删除',
    primary key (`id`),
    index (`user_id`)
) comment '一篇日记的基本信息';



/*
 评论表
 查询评论表
 根据日记id插叙所有评论
 然后根据每个评论的paren_id查询到自己所属的父评论
 封装为map返回 key是父评论 value是所有子评论列表, 这里就选择两级评论即可, 不要嵌套太多
 */
# CREATE TABLE `daily_content_comment`
# (
#     `id`               int(11) NOT NULL AUTO_INCREMENT comment 'id',
#     `parent_id`        int(11)      DEFAULT NULL comment '父评论的id',
#     `user_id`          int(11)      DEFAULT NULL comment '评论者用户id',
#     `user_name`        varchar(255) DEFAULT NULL comment '评论的用户名',
#     `comment_details`  longtext COLLATE utf8mb4_unicode_ci comment '评论内容',
#     `content_flag`     int(11)      DEFAULT NULL comment '文章标识 1:作者  0:普通用户',
#     `daily_id`         int(11)      DEFAULT NULL comment '日记id, 用户评论的是哪个日记',
#     # 回复相关
#     `reply_comment_id` int(11)      DEFAULT NULL comment '回复评论的 (评论的id)',
#     `reply_user_id`    int(11)      DEFAULT NULL comment '回复评论的 (用户id)',
#     `reply_user_name`  varchar(20)  DEFAULT NULL comment '回复评论的用户名',
#
#     `create_time`      datetime     DEFAULT null comment '创建日期',
#     `is_delete`        tinyint(1)   default 0 comment '逻辑删除',
#     PRIMARY KEY (`id`),
#     unique key(`daily_id`)
# ) ENGINE = InnoDB
#   AUTO_INCREMENT = 1
#   DEFAULT CHARSET = utf8mb4 comment '评论表,前半部分是评论相关, 后半部分是回复相关';
