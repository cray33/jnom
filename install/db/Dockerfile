FROM postgis/postgis:15-3.3

RUN apt-get update \
      && apt-get install -y --no-install-recommends osm2pgsql \
      && rm -rf /var/lib/apt/lists/*

ARG OSM_FILE=*.pbf
RUN mkdir -p /osm_pbf
COPY ${OSM_FILE} /osm_pbf/osm.pbf
COPY schema.sql /docker-entrypoint-initdb.d/schema.sql