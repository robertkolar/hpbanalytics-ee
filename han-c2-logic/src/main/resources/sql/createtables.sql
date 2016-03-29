CREATE TABLE c2.sequence
(
  seq_name character varying(255) NOT NULL,
  seq_count bigint,
  CONSTRAINT pk_sequence PRIMARY KEY (seq_name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE c2.sequence
  OWNER TO hpbanalytics;

-- ****************
CREATE TABLE c2.c2system
(
  systemid integer NOT NULL,
  systemname character varying(255),
  origin character varying(255),
  stk boolean,
  fut boolean,
  opt boolean,
  fx boolean,
  email character varying(255),
  password character varying(255),
  usessl boolean,
  CONSTRAINT pk_c2system PRIMARY KEY (systemid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE c2.c2system
  OWNER TO hpbanalytics;

-- ****************
CREATE TABLE c2.c2signal
(
  id bigint NOT NULL,
  origin character varying(255),
  referenceid character varying(255),
  c2system_systemid integer,
  c2signalid integer,
  action character varying(255),
  quant integer,
  reversalquant integer,
  reversalsignaltype character varying(255),
  reversalparent integer,
  symbol character varying(255),
  instrument character varying(255),
  limitprice double precision,
  stopprice double precision,
  duration character varying(255),
  ocagroup integer,
  pollstatus character varying(255),
  publishstatus character varying(255),
  createddate timestamp without time zone,
  polldate timestamp without time zone,
  publishstatusdate timestamp without time zone,
  tradeprice double precision,
  CONSTRAINT pk_c2signal PRIMARY KEY (id),
  CONSTRAINT fk_c2signal_c2system FOREIGN KEY (c2system_systemid)
      REFERENCES c2.c2system (systemid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE c2.c2signal
  OWNER TO hpbanalytics;

CREATE INDEX fki_c2signal_c2system
  ON c2.c2signal
  USING btree
  (c2system_systemid);

-- ****************
CREATE TABLE c2.inputrequest
(
  id bigint NOT NULL,
  receiveddate timestamp without time zone,
  status character varying(255),
  statusdate timestamp without time zone,
  ignorereason character varying(255),
  origin character varying(255),
  referenceid character varying(255),
  requesttype character varying(255),
  action character varying(255),
  quantity integer,
  symbol character varying(255),
  sectype character varying(255),
  ordertype character varying(255),
  orderprice double precision,
  tif character varying(255),
  ocagroup character varying(255),
  CONSTRAINT pk_inputrequest PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE c2.inputrequest
  OWNER TO hpbanalytics;

-- ****************
CREATE TABLE c2.pollevent
(
  id bigint NOT NULL,
  c2signal_id bigint,
  eventdate timestamp without time zone,
  status character varying(255),
  c2request character varying,
  c2response character varying,
  dateposted character varying(255),
  dateemailed character varying(255),
  datekilled character varying(255),
  dateexpired character varying(255),
  datetraded character varying(255),
  tradeprice double precision,
  CONSTRAINT pk_pollevent PRIMARY KEY (id),
  CONSTRAINT fk_pollevent_c2signal FOREIGN KEY (c2signal_id)
      REFERENCES c2.c2signal (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE c2.pollevent
  OWNER TO hpbanalytics;

CREATE INDEX fki_pollevent_c2signal
  ON c2.pollevent
  USING btree
  (c2signal_id);

-- ****************
CREATE TABLE c2.publishevent
(
  id bigint NOT NULL,
  c2signal_id bigint,
  eventdate timestamp without time zone,
  status character varying(255),
  c2request character varying,
  c2response character varying,
  CONSTRAINT pk_publishevent PRIMARY KEY (id),
  CONSTRAINT fk_publishevent_c2signal FOREIGN KEY (c2signal_id)
      REFERENCES c2.c2signal (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE c2.publishevent
  OWNER TO hpbanalytics;

CREATE INDEX fki_publishevent_c2signal
  ON c2.publishevent
  USING btree
  (c2signal_id);