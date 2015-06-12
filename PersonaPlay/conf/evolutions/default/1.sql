# --- First database schema

# --- !Ups

create table person (

  id                        bigint not null,
  nombre                    varchar(255),
  direccion                 varchar(255),
  constraint pk_person primary key (id))
;

create sequence person_seq start with 100;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists person;


SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists person_seq;


