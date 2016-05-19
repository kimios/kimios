-- noinspection SqlNoDataSourceInspectionForFile
ALTER USER postgres with password 'kimios';
CREATE USER kimios with password 'kimios';
CREATE DATABASE kimios with owner kimios;
