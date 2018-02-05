# jnom

### This project is needed for obtaining a normalized database of addresses from the openstreetmap data.
OSM data must be in the .osm format.

## Database
You should have a postgresql database.
DB properties are located in the resources/application.properties file.
`spring.datasource.url=jdbc:postgresql://localhost:5433/jnom` <br>
`spring.datasource.username=jnom`<br>
`spring.datasource.password=123`

### Creation of the database
1. Create a user "jnom". For example <br>
`CREATE ROLE jnom LOGIN
  ENCRYPTED PASSWORD 'md5a39ad10977270b24fdfc54c5d3e90a8b'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;` <br>
  It will create a user "jnom" with password "jnom"
2. Create a database, for example "jnom". Pay attention to LC_COLLATE and LC_CTYPE. They should be set for your native language.<br>
  For example <br>
  `CREATE DATABASE jnom
  WITH OWNER = jnom
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'Russian_Russia.1251'
       LC_CTYPE = 'Russian_Russia.1251'
       CONNECTION LIMIT = -1;`
3. Run sql script for this database from resources/schema.sql

## OSM data
Download .bz2 file from this page: http://gis-lab.info/projects/osm_dump/. Unpack it

## Launch:
java -jar ...target/jnom-0.1.jar [0] [1]
* [0] can be:
   * load_pre_data - load a preliminary data
   * load_data - from preliminary to normal data
   * load_full - preliminary and normal data
   * start - just launch project, without data importing 
* [1] should be a path to the osm data. For example G:\Pavel\Java\jNom\RU-UD.osm
    If you want only to launch projectm without importing, you can do not set this parameter
    
### Examples:
* java -jar ...target/jnom-0.1.jar load_pre_data G:\Pavel\Java\jNom\RU-UD.osm
   * load a preliminary data
* java -jar ...target/jnom-0.1.jar load_data G:\Pavel\Java\jNom\RU-UD.osm
  * from preliminary to normal data
* java -jar ...target/jnom-0.1.jar load_full G:\Pavel\Java\jNom\RU-UD.osm
  * full load
* java -jar ...target/jnom-0.1.jar
  * just launch project, without data importing 
