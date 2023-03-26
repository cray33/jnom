CREATE EXTENSION hstore;

CREATE SCHEMA jnom AUTHORIZATION jnom;

CREATE TABLE jnom.administrative_boundary (
    osm_id int8 NOT NULL,
    "name" varchar NOT NULL,
    tags hstore NULL,
    admin_level int2 NOT NULL,
    parent_osm_id int8 NULL,
    lat float8 NULL,
    lon float8 NULL,
    way geometry NULL,
    CONSTRAINT pk_administrative_boundary PRIMARY KEY (osm_id)
);

CREATE TABLE jnom.city (
    osm_id int8 NOT NULL,
    "name" varchar NOT NULL,
    tags hstore NULL,
    population int4 NULL,
    adm_boundary_osm_id int8 NULL,
    lat float8 NULL,
    lon float8 NULL,
    way_area float4 NULL,
    way geometry NULL,
    CONSTRAINT pk_city PRIMARY KEY (osm_id)
);

ALTER TABLE jnom.city ADD CONSTRAINT adm_boundary_fk FOREIGN KEY (adm_boundary_osm_id) REFERENCES jnom.administrative_boundary(osm_id);

CREATE TABLE jnom.street (
    id uuid NOT NULL,
	osm_id int8 NOT NULL,
	"name" varchar NOT NULL,
	adm_boundary_osm_id int8 NOT NULL,
	city_osm_id int8 NOT NULL,
	lat float8 NULL,
	lon float8 NULL,
	way_length float8 NULL,
	way geometry NULL,
	CONSTRAINT street_pk PRIMARY KEY (id),
	CONSTRAINT street_un UNIQUE (name, city_osm_id, adm_boundary_osm_id)
);

-- jnom.street foreign keys

ALTER TABLE jnom.street ADD CONSTRAINT street_adm_boundary_fk FOREIGN KEY (adm_boundary_osm_id) REFERENCES jnom.administrative_boundary(osm_id);
ALTER TABLE jnom.street ADD CONSTRAINT street_city_fk FOREIGN KEY (city_osm_id) REFERENCES jnom.city(osm_id);


CREATE TABLE jnom.house (
	osm_id int8 NOT NULL,
	tags hstore NULL,
	house_number varchar NOT NULL,
	city_osm_id int8 NOT NULL,
	adm_boundary_osm_id int8 NOT NULL,
	street_id uuid NOT NULL,
	lat float8 NOT NULL,
	lon float8 NOT NULL,
	CONSTRAINT house_pk PRIMARY KEY (osm_id)
);


-- jnom.house foreign keys

ALTER TABLE jnom.house ADD CONSTRAINT adm_boundary_fk FOREIGN KEY (adm_boundary_osm_id) REFERENCES jnom.administrative_boundary(osm_id);
ALTER TABLE jnom.house ADD CONSTRAINT city_fk FOREIGN KEY (city_osm_id) REFERENCES jnom.city(osm_id);
ALTER TABLE jnom.house ADD CONSTRAINT street_fk FOREIGN KEY (street_id) REFERENCES jnom.street(id);

-- --------- osm ---------------------------------
CREATE SCHEMA osm AUTHORIZATION jnom;

-- DROP TABLE public.planet_osm_line;

CREATE TABLE osm.planet_osm_line (
	osm_id int8 NULL,
	"access" text NULL,
	"addr:housename" text NULL,
	"addr:housenumber" text NULL,
	"addr:interpolation" text NULL,
	admin_level text NULL,
	aerialway text NULL,
	aeroway text NULL,
	amenity text NULL,
	area text NULL,
	barrier text NULL,
	bicycle text NULL,
	brand text NULL,
	bridge text NULL,
	boundary text NULL,
	building text NULL,
	construction text NULL,
	covered text NULL,
	culvert text NULL,
	cutting text NULL,
	denomination text NULL,
	disused text NULL,
	embankment text NULL,
	foot text NULL,
	"generator:source" text NULL,
	harbour text NULL,
	highway text NULL,
	historic text NULL,
	horse text NULL,
	intermittent text NULL,
	junction text NULL,
	landuse text NULL,
	layer text NULL,
	leisure text NULL,
	"lock" text NULL,
	man_made text NULL,
	military text NULL,
	motorcar text NULL,
	"name" text NULL,
	"natural" text NULL,
	office text NULL,
	oneway text NULL,
	"operator" text NULL,
	place text NULL,
	population text NULL,
	power text NULL,
	power_source text NULL,
	public_transport text NULL,
	railway text NULL,
	"ref" text NULL,
	religion text NULL,
	route text NULL,
	service text NULL,
	shop text NULL,
	sport text NULL,
	surface text NULL,
	toll text NULL,
	tourism text NULL,
	"tower:type" text NULL,
	tracktype text NULL,
	tunnel text NULL,
	water text NULL,
	waterway text NULL,
	wetland text NULL,
	width text NULL,
	wood text NULL,
	z_order int4 NULL,
	way_area float4 NULL,
	tags hstore NULL,
	way geometry(LINESTRING, 3857) NULL
);
CREATE INDEX planet_osm_line_way_idx ON osm.planet_osm_line USING gist (way) WITH (fillfactor='100');

-- DROP TABLE public.planet_osm_polygon;

CREATE UNLOGGED TABLE osm.planet_osm_polygon (
	osm_id int8 NULL,
	"access" text NULL,
	"addr:housename" text NULL,
	"addr:housenumber" text NULL,
	"addr:interpolation" text NULL,
	admin_level text NULL,
	aerialway text NULL,
	aeroway text NULL,
	amenity text NULL,
	area text NULL,
	barrier text NULL,
	bicycle text NULL,
	brand text NULL,
	bridge text NULL,
	boundary text NULL,
	building text NULL,
	construction text NULL,
	covered text NULL,
	culvert text NULL,
	cutting text NULL,
	denomination text NULL,
	disused text NULL,
	embankment text NULL,
	foot text NULL,
	"generator:source" text NULL,
	harbour text NULL,
	highway text NULL,
	historic text NULL,
	horse text NULL,
	intermittent text NULL,
	junction text NULL,
	landuse text NULL,
	layer text NULL,
	leisure text NULL,
	"lock" text NULL,
	man_made text NULL,
	military text NULL,
	motorcar text NULL,
	"name" text NULL,
	"natural" text NULL,
	office text NULL,
	oneway text NULL,
	"operator" text NULL,
	place text NULL,
	population text NULL,
	power text NULL,
	power_source text NULL,
	public_transport text NULL,
	railway text NULL,
	"ref" text NULL,
	religion text NULL,
	route text NULL,
	service text NULL,
	shop text NULL,
	sport text NULL,
	surface text NULL,
	toll text NULL,
	tourism text NULL,
	"tower:type" text NULL,
	tracktype text NULL,
	tunnel text NULL,
	water text NULL,
	waterway text NULL,
	wetland text NULL,
	width text NULL,
	wood text NULL,
	z_order int4 NULL,
	way_area float4 NULL,
	way geometry NULL
)
WITH (
	autovacuum_enabled=off
);