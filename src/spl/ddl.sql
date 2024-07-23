create database springdatajpa;
use springdatajpa;

create table user (
  email varchar(255) primary key,
  name varchar(255),
  create_date datetime
);