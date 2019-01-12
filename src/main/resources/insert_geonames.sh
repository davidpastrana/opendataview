#!/bin/bash

DIR="/Users/david/Desktop/thesis"
DBHOST="127.0.0.1"
DBPORT="5432"
DBUSER="postgres"

export PGPASSWORD='postgres';

psql -U $DBUSER -h $DBHOST -p $DBPORT -c "CREATE DATABASE geonames WITH TEMPLATE = template0 ENCODING = 'UTF8';"

echo "--------- Removing existing tables..."
psql -U $DBUSER -h $DBHOST -p $DBPORT geonames <<EOT
DROP TABLE geoname;
DROP TABLE postalcodes;
EOT
echo "DONE"

echo "--------- Creating tables... ---------"
psql -U $DBUSER -h $DBHOST -p $DBPORT geonames <<EOT

CREATE TABLE geoname (
geonameid      INT primary key,
name           VARCHAR(200),
asciiname      VARCHAR(200),
alternatenames VARCHAR(10000),
latitude       NUMERIC(8,6),
longitude      NUMERIC(9,6),
fclass         CHAR(1),
fcode          VARCHAR(10),
country        VARCHAR(2),
cc2            VARCHAR(200),
admin1         VARCHAR(20),
admin2         VARCHAR(80),
admin3         VARCHAR(20),
admin4         VARCHAR(20),
population     BIGINT,
elevation      INT,
gtopo30        INT,
timezone       VARCHAR(40),
moddate        DATE
);

CREATE TABLE postalcodes (
country		   VARCHAR(2),
code 		   VARCHAR(20),
name		   VARCHAR(180),
admin1name	   VARCHAR(100),
admin1		   VARCHAR(20),
admin2name	   VARCHAR(100),
admin2         VARCHAR(20),
admin3name     VARCHAR(100),
admin3		   VARCHAR(20),
latitude 	   NUMERIC(8,6),
longitude	   NUMERIC(9,6),
accuracy       INT
);

EOT
echo "DONE"

echo "--------- Copying data into database, please wait..."
psql -e -U $DBUSER -h $DBHOST -p $DBPORT geonames <<EOT

copy geoname (geonameid,name,asciiname,alternatenames,latitude,longitude,fclass,fcode,country,cc2,admin1,admin2,admin3,admin4,population,elevation,gtopo30,timezone,moddate) from '${DIR}/geoname/allCountries.txt' null as '';
copy postalcodes (country,code,name,admin1name,admin1,admin2name,admin2,admin3name,admin3,latitude,longitude,accuracy) from '${DIR}/postalcodes/AT.txt' null as '';

EOT
echo "DONE"

echo "--------- Improving database tables performance for Austria..."
psql -e -U $DBUSER -h $DBHOST -p $DBPORT geonames <<EOT

ALTER TABLE geoname DROP COLUMN fclass, DROP COLUMN fcode, DROP COLUMN cc2, DROP COLUMN admin4, DROP COLUMN gtopo30, DROP COLUMN timezone, DROP COLUMN moddate;
UPDATE geoname SET asciiname=lower(asciiname);
CREATE INDEX geoname_coord_idx ON geoname (asciiname,latitude,longitude,population,elevation);

EOT
echo "DONE"
echo "--------- FINISHED, enjoy! :) ---------"
