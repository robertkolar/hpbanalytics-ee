CREATE TABLE iblogger.sequence
(
  seq_name character varying(255) NOT NULL,
  seq_count bigint,
  CONSTRAINT pk_sequence PRIMARY KEY (seq_name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE iblogger.sequence
  OWNER TO hpbanalytics;

-- ****************
CREATE TABLE iblogger.ibaccount
(
  accountid character varying(255) NOT NULL,
  host character varying(255),
  port integer,
  listen boolean,
  allowupd boolean,
  ibtoc2 boolean,
  analytics boolean,
  stk boolean,
  fut boolean,
  opt boolean,
  fx boolean,
  permittedclients character varying(255),
  permittedaccounts character varying(255),
  CONSTRAINT pk_ibaccount PRIMARY KEY (accountid)
)
WITH (
OIDS=FALSE
);
ALTER TABLE iblogger.ibaccount
OWNER TO hpbanalytics;

-- ****************
CREATE TABLE iblogger.iborder
(
  id bigint NOT NULL,
  permid integer,
  orderid integer,
  clientid integer,
  ibaccount_accountid character varying(255),
  action character varying(255),
  quantity integer,
  underlying character varying(255),
  currency character varying(255),
  symbol character varying(255),
  sectype character varying(255),
  ordertype character varying(255),
  submitdate timestamp without time zone,
  orderprice double precision,
  tif character varying(255),
  parentid integer,
  ocagroup character varying(255),
  statusdate timestamp without time zone,
  fillprice double precision,
  status character varying(255),
  CONSTRAINT pk_iborder PRIMARY KEY (id),
  CONSTRAINT fk_iborder_ibaccount FOREIGN KEY (ibaccount_accountid)
  REFERENCES iblogger.ibaccount (accountid) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE iblogger.iborder
OWNER TO hpbanalytics;

CREATE INDEX fki_iborder_ibaccount
ON iblogger.iborder
USING btree
(ibaccount_accountid COLLATE pg_catalog."default");

-- ****************
CREATE TABLE iblogger.iborderevent
(
  id bigint NOT NULL,
  eventdate timestamp without time zone,
  status character varying(255),
  updateprice double precision,
  fillprice double precision,
  iborder_id bigint,
  CONSTRAINT pk_iborderevent PRIMARY KEY (id),
  CONSTRAINT fk_iborderevent_iborder FOREIGN KEY (iborder_id)
  REFERENCES iblogger.iborder (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE iblogger.iborderevent
OWNER TO hpbanalytics;

CREATE INDEX fki_iborderevent_iborder
ON iblogger.iborderevent
USING btree
(iborder_id);