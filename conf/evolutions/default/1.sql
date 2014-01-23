# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table checkin (
  user_id                   varchar(255) not null,
  loc_id                    varchar(255) not null,
  hour                      smallint)
;

create table location (
  loc_id                    varchar(255) not null,
  latitude                  double,
  longitude                 double,
  constraint pk_location primary key (loc_id))
;

create table user (
  user_id                   varchar(255) not null,
  name                      varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  constraint pk_user primary key (user_id))
;

create sequence checkin_seq;

create sequence location_seq;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists checkin;

drop table if exists location;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists checkin_seq;

drop sequence if exists location_seq;

drop sequence if exists user_seq;

