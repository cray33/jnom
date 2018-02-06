CREATE EXTENSION hstore;

CREATE SCHEMA data;
ALTER SCHEMA data OWNER TO jnom;


CREATE SCHEMA predata;
ALTER SCHEMA predata OWNER TO jnom;

-- DROP TABLE predata.member;

CREATE TABLE predata.member
(
  relation_osm_id bigint NOT NULL,
  ref bigint NOT NULL,
  type character varying NOT NULL,
  role character varying NOT NULL,
  order_by smallint NOT NULL,
  CONSTRAINT pk_member PRIMARY KEY (relation_osm_id, ref)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE predata.member
  OWNER TO jnom;

-- Table: predata.member
-- DROP TABLE predata.node;

CREATE TABLE predata.node
(
  osm_id bigint NOT NULL,
  timestamp_ timestamp without time zone NOT NULL,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  jnom_type smallint,
  tags hstore,
  CONSTRAINT pk_node PRIMARY KEY (osm_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE predata.node
  OWNER TO jnom;


-- Table: predata.node
-- DROP TABLE predata.relation;

CREATE TABLE predata.relation
(
  osm_id bigint NOT NULL,
  timestamp_ timestamp without time zone NOT NULL,
  tags hstore,
  admin_level smallint,
  jnom_type smallint, -- 1 - administrative boundary...
  CONSTRAINT pk_relation PRIMARY KEY (osm_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE predata.relation
  OWNER TO jnom;
COMMENT ON COLUMN predata.relation.jnom_type IS '1 - administrative boundary, 2 - city, 3 - street, 4 - house';


-- Table: predata.way
-- DROP TABLE predata.way;

CREATE TABLE predata.way
(
  osm_id bigint NOT NULL,
  timestamp_ timestamp without time zone NOT NULL,
  tags hstore,
  jnom_type smallint,
  CONSTRAINT pk_way PRIMARY KEY (osm_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE predata.way
  OWNER TO jnom;


  -- Table: predata.way_x_node
-- DROP TABLE predata.way_x_node;

CREATE TABLE predata.way_x_node
(
  way_osm_id bigint NOT NULL,
  node_osm_id bigint NOT NULL,
  order_by integer NOT NULL,
  CONSTRAINT pk_way_x_node PRIMARY KEY (way_osm_id, node_osm_id, order_by)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE predata.way_x_node
  OWNER TO jnom;




-- Table: data.administrative_boundary
-- DROP TABLE data.administrative_boundary;

CREATE TABLE data.administrative_boundary
(
  osm_id bigint NOT NULL,
  name character varying NOT NULL,
  tags hstore,
  admin_level smallint NOT NULL,
  parent_osm_id bigint,
  lat double precision,
  lon double precision,
  CONSTRAINT pk_administrative_boundary PRIMARY KEY (osm_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE data.administrative_boundary
  OWNER TO jnom;


-- Table: data.city
-- DROP TABLE data.city;

CREATE TABLE data.city
(
  osm_id bigint NOT NULL,
  name character varying NOT NULL,
  tags hstore,
  population integer,
  adm_boundary_osm_id bigint,
  lat double precision,
  lon double precision,
  CONSTRAINT pk_city PRIMARY KEY (osm_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE data.city
  OWNER TO jnom;


-- Table: data.house
-- DROP TABLE data.house;

CREATE TABLE data.house
(
  osm_id bigint NOT NULL,
  tags hstore,
  house_number character varying,
  street_osm_id bigint,
  city_osm_id bigint,
  adm_boundary_osm_id bigint,
  lat double precision,
  lon double precision,
  CONSTRAINT pk_house PRIMARY KEY (osm_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE data.house
  OWNER TO jnom;


-- Table: data.street
-- DROP TABLE data.street;

CREATE TABLE data.street
(
  osm_id bigint NOT NULL,
  name character varying NOT NULL,
  tags hstore,
  city_osm_id bigint,
  adm_boundary_osm_id bigint,
  is_copy boolean,
  lat double precision,
  lon double precision,
  CONSTRAINT pk_street PRIMARY KEY (osm_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE data.street
  OWNER TO jnom;


-- Function: data.check_parent_has_child(bigint, bigint)
-- DROP FUNCTION data.check_parent_has_child(bigint, bigint);

CREATE OR REPLACE FUNCTION data.check_parent_has_child(
    adm_boundary_osm_id_child bigint,
    adm_boundary_osm_id_parent bigint)
  RETURNS boolean AS
$BODY$
DECLARE
	adm_boundary data.administrative_boundary;
	result boolean;
BEGIN

	SELECT * INTO adm_boundary FROM data.administrative_boundary WHERE osm_id = adm_boundary_osm_id_child;
	LOOP
		IF adm_boundary.osm_id = adm_boundary_osm_id_parent THEN
			RETURN true;
		END IF;
		IF adm_boundary.parent_osm_id is null THEN
			RETURN false;
		END IF;
		SELECT * INTO adm_boundary FROM data.administrative_boundary WHERE osm_id = adm_boundary.parent_osm_id;
	END LOOP;

	RETURN false;
END;
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION data.check_parent_has_child(bigint, bigint)
  OWNER TO jnom;


  -- Function: data.get_name_with_parents(bigint)
-- DROP FUNCTION data.get_name_with_parents(bigint);

CREATE OR REPLACE FUNCTION data.get_name_with_parents(relation_osm_id bigint)
  RETURNS character varying AS
$BODY$
DECLARE
  adm_boundary data.administrative_boundary;
  result character varying;
    BEGIN

	SELECT * INTO adm_boundary FROM data.administrative_boundary WHERE osm_id = relation_osm_id;
	result := adm_boundary.name;
        WHILE adm_boundary.parent_osm_id is not null
	  LOOP
	SELECT * INTO adm_boundary FROM data.administrative_boundary WHERE osm_id = adm_boundary.parent_osm_id;
	    result := result || ', ' || adm_boundary.name;
	  END LOOP;

    RETURN result;
    END;
  $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION data.get_name_with_parents(bigint)
  OWNER TO jnom;

