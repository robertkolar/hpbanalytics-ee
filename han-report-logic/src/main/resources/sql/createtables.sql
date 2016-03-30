CREATE TABLE report.sequence
(
  seq_name character varying(255) NOT NULL,
  seq_count bigint,
  CONSTRAINT pk_sequence PRIMARY KEY (seq_name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE report.sequence
  OWNER TO hpbanalytics;

-- ******************
CREATE TABLE report.report
(
  id integer NOT NULL,
  origin character varying(255),
  reportname character varying(255),
  stk boolean,
  fut boolean,
  opt boolean,
  fx boolean,
  CONSTRAINT pk_report PRIMARY KEY (id)
)
WITH (
OIDS=FALSE
);
ALTER TABLE report.report
OWNER TO hpbanalytics;

-- ****************
CREATE TABLE report.execution
(
  id bigint NOT NULL,
  receiveddate timestamp without time zone,
  report_id integer,
  comment character varying(255),
  origin character varying(255),
  referenceid character varying(255),
  action character varying(255),
  quantity integer,
  symbol character varying(255),
  underlying character varying(255),
  currency character varying(255),
  sectype character varying(255),
  filldate timestamp without time zone,
  fillprice numeric(15,5),
  CONSTRAINT pk_execution PRIMARY KEY (id),
  CONSTRAINT fk_execution_report FOREIGN KEY (report_id)
  REFERENCES report.report (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE report.execution
OWNER TO hpbanalytics;

CREATE INDEX fki_execution_report
ON report.execution
USING btree
(report_id);

-- ****************
CREATE TABLE report.trade
(
  id bigint NOT NULL,
  type character varying(255),
  symbol character varying(255),
  underlying character varying(255),
  currency character varying(255),
  sectype character varying(255),
  cumulativequantity integer,
  status character varying(255),
  openposition integer,
  avgopenprice numeric(15,5),
  opendate timestamp without time zone,
  avgcloseprice numeric(15,5),
  closedate timestamp without time zone,
  profitloss numeric(15,5),
  report_id integer,
  CONSTRAINT pk_trade PRIMARY KEY (id),
  CONSTRAINT fk_trade_report FOREIGN KEY (report_id)
  REFERENCES report.report (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE report.trade
OWNER TO hpbanalytics;

CREATE INDEX fki_trade_report
ON report.trade
USING btree
(report_id);

-- ****************
CREATE TABLE report.splitexecution
(
  id bigint NOT NULL,
  splitquantity integer,
  currentposition integer,
  filldate timestamp without time zone,
  execution_id bigint,
  trade_id bigint,
  CONSTRAINT pk_splitexecution PRIMARY KEY (id),
  CONSTRAINT fk_splitexecution_execution FOREIGN KEY (execution_id)
  REFERENCES report.execution (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_splitexecution_trade FOREIGN KEY (trade_id)
  REFERENCES report.trade (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE report.splitexecution
OWNER TO hpbanalytics;

CREATE INDEX fki_splitexecution_execution
ON report.splitexecution
USING btree
(execution_id);

CREATE INDEX fki_splitexecution_trade
ON report.splitexecution
USING btree
(trade_id);