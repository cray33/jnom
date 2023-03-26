# jnom

### This is a project for getting a normalized hierarchical database of postal addresses from [OSM](https://www.openstreetmap.org/) data
You can see an example of search page with result data here: http://jnom.fvds.ru:8080/searchbox.html?

This application has been written in the Java language.
It also uses [PostgreSQL](https://www.postgresql.org) database, [PostGIS](https://postgis.net) extension, [osm2pgsql](https://osm2pgsql.org) converter.

The process consist of two steps
1. Loading an OSM data to a database by osm2pgsql
2. Processing this data and converting it to a normalized database

There are two variants of executing this application.
1. By the [Docker](https://www.docker.com) ([Docker Desktop](https://www.docker.com/products/docker-desktop/) for window)
2. Or by manual installing all the environment

### Docker
1. You need to have Docker installed on your computer
2. Download OSM data of the region you needed. The bigger region - the more space it requires, the longer it processed.
      There are two services which allow you to download OSM data https://download.geofabrik.de and https://download.bbbike.org/osm/
      The format of the result file should be .pbf
3. Copy the "install" directory of the project to you computer
4. Put the downloaded .pbf file to the "install/db/"
5. If there is any other .pbf file - delete it
6. Execute the "docker-compose up -d" command in the console in the "install" directory
7. If everything is ok - open the "http://localhost:8080" url. You should see a page with input fields and a map
8. Congratulations, the service has been started. 
   Now, you should import OSM data to the database. 
   Execute command "docker-compose exec jnom-db osm2pgsql -c -s /osm_pbf/osm.pbf --database=jnom --username=jnom --middle-schema=osm --output-pgsql-schema=osm --hstore"
   is the console in the "install" directory. The process could take o lot of time.
9. Open the page "http://localhost:8080/rest/startImport". It will start the converting process 
   to normalized form. 
10. When it ends - then you would be able to search you addresses via form in the http://localhost:8080.
   The data will be in the "jnom" schema in the database. The login/password to access the database is jnom/jnom. The port is 5433

### Without Docker
1. Install the postgresql
2. Install the PostGIS extension
3. Install the osm2pgsql
4. Start the installed postgresql
5. Create a user "jnom" and create a database "jnom"
6. Execute the sql script, located in the "/install/db/schema.sql"
7. Copy a .pbf file to any directory, for example "/osm_pbf/osm.pbf"
8. Execute osm2pgsql by following command
   osm2pgsql -c -s <pbf_file> --database=jnom --username=jnom --middle-schema=osm --output-pgsql-schema=osm --hstore
9. You need to have installed java jre
10. Copy the jnom.jar file from "install/app/jnom.jar"
11. Start the application by following command: java -jar <jar_file> -Dspring.datasource.url=jdbc:postgresql://localhost:5433/jnom -Dspring.datasource.username=jnom -Dspring.datasource.password=jnom
    You can set your port, username or password to the database if you need
12. When app starts, go to the http://localhost:8080/rest/startImport page. It will start the converting process
13. hen it ends - then you would be able to search you addresses via form in the http://localhost:8080.
    The data will be in the "jnom" schema in the database.