---Kimios - Document Management System Software
---Copyright (C) 2012-2013  DevLib'
---
---This program is free software: you can redistribute it and/or modify
---it under the terms of the GNU Affero General Public License as
---published by the Free Software Foundation, either version 2 of the
---License, or (at your option) any later version.
---
---This program is distributed in the hope that it will be useful,
---but WITHOUT ANY WARRANTY; without even the implied warranty of
---MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
---GNU Affero General Public License for more details.
---You should have received a copy of the GNU Affero General Public License
---along with this program.  If not, see <http://www.gnu.org/licenses/>.
---
--
-- PostgreSQL database dump
--

-- Started on 2010-11-05 11:36:52

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 431 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: -
--


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;


CREATE TABLE authenticated_services
(
  service_id character varying(255) NOT NULL,
  service_key character varying(255) NOT NULL,
  CONSTRAINT auth_services_pk PRIMARY KEY (service_id )
);

--
-- TOC entry 1618 (class 1259 OID 72397)
-- Dependencies: 6
-- Name: authentication_params; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authentication_params (
    authentication_source_name character varying(100) NOT NULL,
    param_name character varying(255) NOT NULL,
    param_value character varying(255)
);


--
-- TOC entry 1619 (class 1259 OID 72403)
-- Dependencies: 6
-- Name: authentication_source; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE authentication_source (
    source_name character varying(50) NOT NULL,
    java_class character varying(255)
);


--
-- TOC entry 1620 (class 1259 OID 72406)
-- Dependencies: 6
-- Name: bookmarks; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE bookmarks (
    bk_owner character varying(50) NOT NULL,
    bk_owner_source character varying(50) NOT NULL,
    dm_entity_id bigint NOT NULL,
    dm_entity_type bigint NOT NULL
);


--
-- TOC entry 1621 (class 1259 OID 72409)
-- Dependencies: 6
-- Name: checkout; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE checkout (
    document_id bigint NOT NULL,
    username character varying(50) NOT NULL,
    user_source character varying,
    checkout_date timestamp without time zone
);


--
-- TOC entry 1624 (class 1259 OID 72427)
-- Dependencies: 6
-- Name: dt_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE dt_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1625 (class 1259 OID 72429)
-- Dependencies: 1952 6
-- Name: data_transaction; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE data_transaction (
    id bigint DEFAULT nextval('dt_id_seq'::regclass) NOT NULL,
    username character varying(50) NOT NULL,
    user_source character varying(50) NOT NULL,
    last_activity_date timestamp without time zone NOT NULL,
    document_version_id bigint NOT NULL,
    data_size bigint NOT NULL,
    is_compressed boolean NOT NULL,
    hash_md5 character varying(255) NOT NULL,
    hash_sha character varying(255) NOT NULL,
    transfer_mode integer NOT NULL,
    file_path character varying(255),
    has_been_chk_out boolean
);


--
-- TOC entry 1626 (class 1259 OID 72436)
-- Dependencies: 6
-- Name: dm_entity; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE dm_entity (
    dm_entity_id bigint NOT NULL,
    dm_entity_type integer NOT NULL,
    dm_entity_path text NOT NULL,
    dm_entity_owner character varying(200),
    dm_entity_owner_source character varying(200),
    creation_date timestamp without time zone,
    update_date timestamp without time zone,
    dm_entity_name character varying(255)
);


--
-- TOC entry 1627 (class 1259 OID 72442)
-- Dependencies: 6
-- Name: dm_entity_acl; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE dm_entity_acl (
    dm_entity_id bigint NOT NULL,
    dm_entity_type integer NOT NULL,
    rule_hash character varying(500) NOT NULL
);


--
-- TOC entry 1628 (class 1259 OID 72448)
-- Dependencies: 6
-- Name: dm_entity_attributes; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE dm_entity_attributes (
    dm_entity_id bigint NOT NULL,
    attribute_name character varying(50) NOT NULL,
    attribute_value character varying(500),
    is_indexed boolean
);


--
-- TOC entry 1629 (class 1259 OID 72454)
-- Dependencies: 1626 6
-- Name: dm_entity_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE dm_entity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 2109 (class 0 OID 0)
-- Dependencies: 1629
-- Name: dm_entity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE dm_entity_id_seq OWNED BY dm_entity.dm_entity_id;


--
-- TOC entry 1630 (class 1259 OID 72456)
-- Dependencies: 6
-- Name: dm_security_rules; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE dm_security_rules (
    security_entity_type integer NOT NULL,
    security_entity_id character varying(500) NOT NULL,
    security_entity_source character varying(500) NOT NULL,
    rights smallint NOT NULL,
    rule_hash character varying(255) NOT NULL
);


--
-- TOC entry 1631 (class 1259 OID 72462)
-- Dependencies: 6
-- Name: doc_comment_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE doc_comment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1632 (class 1259 OID 72464)
-- Dependencies: 6
-- Name: doc_type_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE doc_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1633 (class 1259 OID 72466)
-- Dependencies: 6
-- Name: doc_version_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE doc_version_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1634 (class 1259 OID 72468)
-- Dependencies: 6
-- Name: document; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE document (
    id bigint NOT NULL,
    folder_id bigint,
    mime_type character varying(500),
    extension character varying(50)
);


--
-- TOC entry 1635 (class 1259 OID 72474)
-- Dependencies: 1954 6
-- Name: document_comment; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE document_comment (
    id bigint DEFAULT nextval('doc_comment_id_seq'::regclass) NOT NULL,
    document_version_id bigint,
    author_name character varying(50),
    author_source character varying(50),
    comment_content text,
    comment_date timestamp without time zone
);


--
-- TOC entry 1636 (class 1259 OID 72481)
-- Dependencies: 1955 6
-- Name: document_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE document_type (
    id bigint DEFAULT nextval('doc_type_id_seq'::regclass) NOT NULL,
    type_name character varying(50),
    document_type_id bigint
);


--
-- TOC entry 1637 (class 1259 OID 72485)
-- Dependencies: 1956 6
-- Name: document_version; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE document_version (
    id bigint DEFAULT nextval('doc_version_id_seq'::regclass) NOT NULL,
    author character varying(50),
    author_source character varying(50),
    creation_date timestamp without time zone,
    modification_date timestamp without time zone,
    document_id bigint,
    storage_path character varying(255),
    version_length bigint,
    document_type_id bigint,
    hash_md5 character varying(255),
    hash_sha1 character varying(255)
);


--
-- TOC entry 1638 (class 1259 OID 72492)
-- Dependencies: 6
-- Name: document_workflow_status; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE document_workflow_status (
    document_id bigint NOT NULL,
    workflow_status_id bigint NOT NULL,
    status_date timestamp without time zone,
    security_entity_name character varying(50),
    security_entity_source character varying(50)
);


--
-- TOC entry 1639 (class 1259 OID 72495)
-- Dependencies: 6
-- Name: document_workflow_status_request; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE document_workflow_status_request (
    username character varying(50) NOT NULL,
    user_source character varying(50) NOT NULL,
    document_id bigint NOT NULL,
    workflow_status_id bigint NOT NULL,
    request_date timestamp without time zone NOT NULL,
    request_status integer NOT NULL,
    request_comment text,
    validator_user_name character varying(50),
    validator_user_source character varying(50),
    validation_date timestamp without time zone
);


--
-- TOC entry 1640 (class 1259 OID 72501)
-- Dependencies: 6
-- Name: wkf_status_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE wkf_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1641 (class 1259 OID 72503)
-- Dependencies: 1957 6
-- Name: workflow_status; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE workflow_status (
    id bigint DEFAULT nextval('wkf_status_id_seq'::regclass) NOT NULL,
    status_name character varying(50),
    workflow_id bigint,
    successor_id bigint
);


--
-- TOC entry 1642 (class 1259 OID 72507)
-- Dependencies: 1760 6
-- Name: document_pojo; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW document_pojo AS
    SELECT doc.id, doc.dm_entity_name, doc.dm_entity_owner, doc.dm_entity_owner_source, doc.creation_date, doc.update_date, doc.folder_id, doc.mime_type, doc.extension, doc.path AS document_path, dv.id AS version_id, dv.type_name, dv.version_length AS file_length, dv.creation_date AS version_creation_date, dv.modification_date AS version_update_date, dv.document_type_id, doc.locked, doc.lock_by, doc.lock_source, doc.lock_date, req.outofworkflow, stat.status_name, stat.last_wfs FROM ((((SELECT d.id, dm.dm_entity_name, dm.dm_entity_owner, dm.dm_entity_owner_source, dm.creation_date, dm.update_date, d.folder_id, d.mime_type, d.extension, CASE WHEN (chk.username IS NOT NULL) THEN true ELSE false END AS locked, chk.username AS lock_by, chk.user_source AS lock_source, chk.checkout_date AS lock_date, dm.dm_entity_path AS path FROM ((document d LEFT JOIN checkout chk ON ((d.id = chk.document_id))) LEFT JOIN dm_entity dm ON (((d.id = dm.dm_entity_id) AND (dm.dm_entity_type = 3))))) doc JOIN (SELECT b.id, b.author, b.author_source, b.creation_date, b.modification_date, b.document_id, b.storage_path, b.version_length, b.document_type_id, b.hash_md5, b.hash_sha1, dt.type_name FROM (((SELECT document_version.document_id, max(document_version.creation_date) AS creation_date FROM document_version GROUP BY document_version.document_id) a JOIN document_version b ON (((a.creation_date = b.creation_date) AND (a.document_id = b.document_id)))) LEFT JOIN document_type dt ON ((b.document_type_id = dt.id)))) dv ON ((dv.document_id = doc.id))) LEFT JOIN (SELECT dws.document_id, stu.id AS last_wfs, stu.status_name FROM (((SELECT document_workflow_status.document_id, max(document_workflow_status.status_date) AS status_date FROM document_workflow_status GROUP BY document_workflow_status.document_id) c JOIN document_workflow_status dws ON (((c.document_id = dws.document_id) AND (c.status_date = dws.status_date)))) JOIN workflow_status stu ON ((dws.workflow_status_id = stu.id)))) stat ON ((stat.document_id = doc.id))) LEFT JOIN (SELECT r2.document_id, CASE WHEN ((st1.successor_id IS NOT NULL) OR (r2.request_status = ANY (ARRAY[1, 3]))) THEN false ELSE true END AS outofworkflow FROM (((SELECT document_workflow_status_request.document_id, max(document_workflow_status_request.request_date) AS request_date FROM document_workflow_status_request GROUP BY document_workflow_status_request.document_id) r1 JOIN document_workflow_status_request r2 ON (((r1.document_id = r2.document_id) AND (r1.request_date = r2.request_date)))) JOIN workflow_status st1 ON ((r2.workflow_status_id = st1.id)))) req ON ((req.document_id = doc.id)));


--
-- TOC entry 1643 (class 1259 OID 72512)
-- Dependencies: 6
-- Name: entity_log; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE entity_log (
    id bigint NOT NULL,
    log_time timestamp without time zone,
    username character varying(50),
    user_source character varying(50),
    dm_entity_id bigint,
    dm_entity_type integer,
    action_parameter bigint,
    action integer NOT NULL
);


--
-- TOC entry 1644 (class 1259 OID 72518)
-- Dependencies: 6
-- Name: entity_log_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE entity_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1645 (class 1259 OID 72520)
-- Dependencies: 6
-- Name: enumeration_value; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE enumeration_value (
    enumeration_id bigint NOT NULL,
    enumeration_value character varying(255) NOT NULL
);


--
-- TOC entry 1646 (class 1259 OID 72523)
-- Dependencies: 6
-- Name: folder; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE folder (
    id bigint NOT NULL,
    parent_type integer,
    parent_id bigint,
    folder_name character varying(50)
);


--
-- TOC entry 1647 (class 1259 OID 72526)
-- Dependencies: 6
-- Name: log_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1648 (class 1259 OID 72528)
-- Dependencies: 1958 6
-- Name: generic_log; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE generic_log (
    id bigint DEFAULT nextval('log_id_seq'::regclass) NOT NULL,
    log_time timestamp without time zone,
    username character varying(50),
    user_source character varying(50),
    dm_entity_id bigint,
    dm_entity_type integer,
    action integer NOT NULL,
    action_parameters bigint
);


--
-- TOC entry 1649 (class 1259 OID 72535)
-- Dependencies: 6
-- Name: groups; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE groups (
    gid character varying(50) NOT NULL,
    group_name character varying(50),
    authentication_source character varying(50) NOT NULL
);


--
-- TOC entry 1650 (class 1259 OID 72538)
-- Dependencies: 6
-- Name: meta_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE meta_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1651 (class 1259 OID 72540)
-- Dependencies: 1959 6
-- Name: meta; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE meta (
    meta_name character varying(50) NOT NULL,
    document_type_id bigint NOT NULL,
    meta_feed_id bigint,
    id bigint DEFAULT nextval('meta_id_seq'::regclass) NOT NULL,
    meta_type integer
);


--
-- TOC entry 1652 (class 1259 OID 72544)
-- Dependencies: 6
-- Name: meta_boolean_value; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE meta_boolean_value (
    document_version_id bigint NOT NULL,
    meta_id bigint NOT NULL,
    meta_boolean_value boolean
);


--
-- TOC entry 1653 (class 1259 OID 72547)
-- Dependencies: 6
-- Name: meta_date_value; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE meta_date_value (
    document_version_id bigint NOT NULL,
    meta_id bigint NOT NULL,
    meta_date_value date
);


--
-- TOC entry 1654 (class 1259 OID 72550)
-- Dependencies: 6
-- Name: meta_feed_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE meta_feed_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1655 (class 1259 OID 72552)
-- Dependencies: 1960 6
-- Name: meta_feed; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE meta_feed (
    id bigint DEFAULT nextval('meta_feed_id_seq'::regclass) NOT NULL,
    meta_feed_name character varying(50),
    java_class character varying(255)
);


--
-- TOC entry 1656 (class 1259 OID 72556)
-- Dependencies: 6
-- Name: meta_number_value; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE meta_number_value (
    document_version_id bigint NOT NULL,
    meta_id bigint NOT NULL,
    meta_number_value double precision
);


--
-- TOC entry 1657 (class 1259 OID 72559)
-- Dependencies: 6
-- Name: meta_string_value; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE meta_string_value (
    document_version_id bigint NOT NULL,
    meta_id bigint NOT NULL,
    meta_string_value character varying(500)
);


--
-- TOC entry 1658 (class 1259 OID 72565)
-- Dependencies: 6
-- Name: related_documents; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE related_documents (
    document_id bigint NOT NULL,
    related_document_id bigint NOT NULL
);


CREATE SEQUENCE repository_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


CREATE TABLE repositories
(
  repository_id bigint NOT NULL DEFAULT nextval('repository_id_seq'::regclass),
  repository_name character varying(200) NOT NULL,
  repository_path text NOT NULL,
  repository_default boolean NOT NULL DEFAULT false,
  CONSTRAINT repository_id_pk PRIMARY KEY (repository_id),
  CONSTRAINT repository_path_unique_key UNIQUE (repository_path),
  CONSTRAINT repository_name_unique_key UNIQUE (repository_name)
);


--
-- TOC entry 1659 (class 1259 OID 72568)
-- Dependencies: 6
-- Name: roles; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE roles (
    role_value integer NOT NULL,
    username character varying(50) NOT NULL,
    user_source character varying(50) NOT NULL
);


--
-- TOC entry 1660 (class 1259 OID 72571)
-- Dependencies: 6
-- Name: rule_event; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE rule_event (
    rule_id bigint NOT NULL,
    event_name integer NOT NULL,
    event_status integer NOT NULL
);


--
-- TOC entry 1673 (class 1259 OID 73294)
-- Dependencies: 6
-- Name: rule_exclude_paths; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE rule_exclude_paths (
    rule_id bigint NOT NULL,
    excl_path text NOT NULL
);


--
-- TOC entry 1661 (class 1259 OID 72574)
-- Dependencies: 6
-- Name: rules; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE rules (
    id bigint NOT NULL,
    java_class character varying(500) NOT NULL,
    rule_name character varying(500) NOT NULL,
    dm_path text NOT NULL,
    rule_recursive boolean
);


--
-- TOC entry 1662 (class 1259 OID 72580)
-- Dependencies: 6 1661
-- Name: rule_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE rule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 2110 (class 0 OID 0)
-- Dependencies: 1662
-- Name: rule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE rule_id_seq OWNED BY rules.id;


--
-- TOC entry 1663 (class 1259 OID 72582)
-- Dependencies: 6
-- Name: rule_param; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE rule_param (
    rule_id bigint NOT NULL,
    param_name character varying(500) NOT NULL,
    param_value bytea NOT NULL
);


--
-- TOC entry 1664 (class 1259 OID 72588)
-- Dependencies: 6
-- Name: symbolic_link; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE symbolic_link (
    dm_entity_id bigint NOT NULL,
    parent_id bigint NOT NULL,
    parent_type integer NOT NULL,
    id bigint NOT NULL,
    dm_entity_type integer
);


--
-- TOC entry 1665 (class 1259 OID 72591)
-- Dependencies: 6
-- Name: user_attributes; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE user_attributes (
    user_id character varying(50) NOT NULL,
    authentication_source character varying(50) NOT NULL,
    attribute_name character varying(255) NOT NULL,
    attribute_value character varying(500) NOT NULL
);


--
-- TOC entry 1666 (class 1259 OID 72597)
-- Dependencies: 6
-- Name: user_group; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE user_group (
    user_id character varying(50) NOT NULL,
    gid character varying(50) NOT NULL,
    authentication_source character varying(50) NOT NULL
);


--
-- TOC entry 1667 (class 1259 OID 72600)
-- Dependencies: 6
-- Name: user_session; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE user_session (
    id character varying(50) NOT NULL,
    user_id character varying(50),
    user_source character varying(50),
    last_use timestamp with time zone
);


--
-- TOC entry 1668 (class 1259 OID 72603)
-- Dependencies: 6
-- Name: users; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE users (
    user_id character varying(50) NOT NULL,
    user_password character varying(50),
    user_fullname character varying(50),
    last_login timestamp without time zone,
    mail character varying(50),
    authentication_source character varying(50) NOT NULL
);


--
-- TOC entry 1669 (class 1259 OID 72606)
-- Dependencies: 6
-- Name: wkf_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE wkf_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1670 (class 1259 OID 72608)
-- Dependencies: 1962 6
-- Name: workflow; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE workflow (
    id bigint DEFAULT nextval('wkf_id_seq'::regclass) NOT NULL,
    workflow_name character varying(50),
    workflow_description text
);


--
-- TOC entry 1671 (class 1259 OID 72615)
-- Dependencies: 6
-- Name: workflow_status_manager; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE workflow_status_manager (
    security_entity_name character varying(50) NOT NULL,
    security_entity_source character varying(50) NOT NULL,
    security_entity_type integer NOT NULL,
    workflow_status_id bigint NOT NULL
);


--
-- TOC entry 1672 (class 1259 OID 72618)
-- Dependencies: 6
-- Name: workspace; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE workspace (
    id bigint NOT NULL
);


--
-- TOC entry 1953 (class 2604 OID 72621)
-- Dependencies: 1629 1626
-- Name: dm_entity_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE dm_entity ALTER COLUMN dm_entity_id SET DEFAULT nextval('dm_entity_id_seq'::regclass);


--
-- TOC entry 1961 (class 2604 OID 72622)
-- Dependencies: 1662 1661
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE rules ALTER COLUMN id SET DEFAULT nextval('rule_id_seq'::regclass);


--
-- TOC entry 1982 (class 2606 OID 72624)
-- Dependencies: 1627 1627 1627
-- Name: dm_entity_acl_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY dm_entity_acl
    ADD CONSTRAINT dm_entity_acl_pk PRIMARY KEY (dm_entity_id, rule_hash);


--
-- TOC entry 1978 (class 2606 OID 72626)
-- Dependencies: 1626 1626
-- Name: dm_entity_uid_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY dm_entity
    ADD CONSTRAINT dm_entity_uid_pk PRIMARY KEY (dm_entity_id);


--
-- TOC entry 1984 (class 2606 OID 72628)
-- Dependencies: 1628 1628 1628
-- Name: entity_attr_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY dm_entity_attributes
    ADD CONSTRAINT entity_attr_pk PRIMARY KEY (dm_entity_id, attribute_name);


--
-- TOC entry 2020 (class 2606 OID 72630)
-- Dependencies: 1655 1655
-- Name: meta_feed_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY meta_feed
    ADD CONSTRAINT meta_feed_pkey PRIMARY KEY (id);


--
-- TOC entry 1964 (class 2606 OID 72632)
-- Dependencies: 1618 1618 1618
-- Name: kimios_authentication_params_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authentication_params
    ADD CONSTRAINT kimios_authentication_params_pkey PRIMARY KEY (authentication_source_name, param_name);


--
-- TOC entry 1966 (class 2606 OID 72634)
-- Dependencies: 1619 1619
-- Name: kimios_authentication_source_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY authentication_source
    ADD CONSTRAINT kimios_authentication_source_pkey PRIMARY KEY (source_name);


--
-- TOC entry 1968 (class 2606 OID 72636)
-- Dependencies: 1620 1620 1620 1620 1620
-- Name: kimios_bookmark_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY bookmarks
    ADD CONSTRAINT kimios_bookmark_pk PRIMARY KEY (bk_owner, bk_owner_source, dm_entity_id, dm_entity_type);


--
-- TOC entry 1970 (class 2606 OID 72638)
-- Dependencies: 1621 1621
-- Name: kimios_checkout_idx; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY checkout
    ADD CONSTRAINT kimios_checkout_idx PRIMARY KEY (document_id);



--
-- TOC entry 1976 (class 2606 OID 72644)
-- Dependencies: 1625 1625
-- Name: kimios_data_transaction_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY data_transaction
    ADD CONSTRAINT kimios_data_transaction_pk PRIMARY KEY (id);


--
-- TOC entry 1980 (class 2606 OID 72646)
-- Dependencies: 1626 1626
-- Name: kimios_dm_entity_unique_path; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY dm_entity
    ADD CONSTRAINT kimios_dm_entity_unique_path UNIQUE (dm_entity_path);


--
-- TOC entry 1986 (class 2606 OID 72648)
-- Dependencies: 1630 1630
-- Name: kimios_dm_security_rules_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY dm_security_rules
    ADD CONSTRAINT kimios_dm_security_rules_pkey PRIMARY KEY (rule_hash);


--
-- TOC entry 1988 (class 2606 OID 72650)
-- Dependencies: 1630 1630 1630 1630 1630
-- Name: kimios_dm_security_rules_security_entity_type_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY dm_security_rules
    ADD CONSTRAINT kimios_dm_security_rules_security_entity_type_key UNIQUE (security_entity_type, security_entity_id, security_entity_source, rights);


--
-- TOC entry 1992 (class 2606 OID 72652)
-- Dependencies: 1635 1635
-- Name: kimios_document_comment_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY document_comment
    ADD CONSTRAINT kimios_document_comment_pkey PRIMARY KEY (id);


--
-- TOC entry 1990 (class 2606 OID 72654)
-- Dependencies: 1634 1634
-- Name: kimios_document_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY document
    ADD CONSTRAINT kimios_document_pkey PRIMARY KEY (id);


--
-- TOC entry 1994 (class 2606 OID 72656)
-- Dependencies: 1636 1636
-- Name: kimios_document_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY document_type
    ADD CONSTRAINT kimios_document_type_pkey PRIMARY KEY (id);


--
-- TOC entry 1996 (class 2606 OID 72658)
-- Dependencies: 1637 1637
-- Name: kimios_document_version_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY document_version
    ADD CONSTRAINT kimios_document_version_pkey PRIMARY KEY (id);


--
-- TOC entry 1998 (class 2606 OID 72660)
-- Dependencies: 1638 1638 1638
-- Name: kimios_document_workflow_status_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY document_workflow_status
    ADD CONSTRAINT kimios_document_workflow_status_pk PRIMARY KEY (document_id, workflow_status_id);


--
-- TOC entry 2000 (class 2606 OID 72662)
-- Dependencies: 1639 1639 1639 1639 1639 1639 1639
-- Name: kimios_document_workflow_status_request_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY document_workflow_status_request
    ADD CONSTRAINT kimios_document_workflow_status_request_pkey PRIMARY KEY (username, user_source, document_id, workflow_status_id, request_date, request_status);


--
-- TOC entry 2006 (class 2606 OID 72664)
-- Dependencies: 1645 1645 1645
-- Name: kimios_enumeration_value_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY enumeration_value
    ADD CONSTRAINT kimios_enumeration_value_pk PRIMARY KEY (enumeration_id, enumeration_value);


--
-- TOC entry 2008 (class 2606 OID 72666)
-- Dependencies: 1646 1646
-- Name: kimios_folder_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY folder
    ADD CONSTRAINT kimios_folder_pkey PRIMARY KEY (id);


--
-- TOC entry 2012 (class 2606 OID 72668)
-- Dependencies: 1649 1649 1649
-- Name: kimios_group_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT kimios_group_pk PRIMARY KEY (gid, authentication_source);


--
-- TOC entry 2010 (class 2606 OID 72670)
-- Dependencies: 1648 1648
-- Name: kimios_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY generic_log
    ADD CONSTRAINT kimios_log_pkey PRIMARY KEY (id);


--
-- TOC entry 2016 (class 2606 OID 72672)
-- Dependencies: 1652 1652 1652
-- Name: kimios_meta_boolean_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY meta_boolean_value
    ADD CONSTRAINT kimios_meta_boolean_value_pkey PRIMARY KEY (document_version_id, meta_id);


--
-- TOC entry 2018 (class 2606 OID 72674)
-- Dependencies: 1653 1653 1653
-- Name: kimios_meta_date_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY meta_date_value
    ADD CONSTRAINT kimios_meta_date_value_pkey PRIMARY KEY (document_version_id, meta_id);


--
-- TOC entry 2022 (class 2606 OID 72676)
-- Dependencies: 1656 1656 1656
-- Name: kimios_meta_number_value_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY meta_number_value
    ADD CONSTRAINT kimios_meta_number_value_pkey PRIMARY KEY (document_version_id, meta_id);


--
-- TOC entry 2014 (class 2606 OID 72678)
-- Dependencies: 1651 1651
-- Name: kimios_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY meta
    ADD CONSTRAINT kimios_meta_pkey PRIMARY KEY (id);


--
-- TOC entry 2024 (class 2606 OID 72680)
-- Dependencies: 1657 1657 1657
-- Name: kimios_meta_string_value_document_uid_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY meta_string_value
    ADD CONSTRAINT kimios_meta_string_value_document_uid_pkey PRIMARY KEY (document_version_id, meta_id);


--
-- TOC entry 2026 (class 2606 OID 72682)
-- Dependencies: 1658 1658 1658
-- Name: kimios_related_documetns_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY related_documents
    ADD CONSTRAINT kimios_related_documetns_pk PRIMARY KEY (document_id, related_document_id);


--
-- TOC entry 2028 (class 2606 OID 72684)
-- Dependencies: 1659 1659 1659 1659
-- Name: kimios_role_idx; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT kimios_role_idx PRIMARY KEY (user_source, username, role_value);


--
-- TOC entry 2030 (class 2606 OID 72686)
-- Dependencies: 1660 1660 1660 1660
-- Name: kimios_rule_event_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY rule_event
    ADD CONSTRAINT kimios_rule_event_pk PRIMARY KEY (rule_id, event_name, event_status);


--
-- TOC entry 2032 (class 2606 OID 72688)
-- Dependencies: 1661 1661
-- Name: kimios_rule_id; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY rules
    ADD CONSTRAINT kimios_rule_id PRIMARY KEY (id);


--
-- TOC entry 2034 (class 2606 OID 72690)
-- Dependencies: 1663 1663 1663
-- Name: kimios_rule_param_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY rule_param
    ADD CONSTRAINT kimios_rule_param_pk PRIMARY KEY (rule_id, param_name);


--
-- TOC entry 2040 (class 2606 OID 72692)
-- Dependencies: 1667 1667
-- Name: kimios_session_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY user_session
    ADD CONSTRAINT kimios_session_pkey PRIMARY KEY (id);


--
-- TOC entry 2038 (class 2606 OID 72694)
-- Dependencies: 1666 1666 1666 1666
-- Name: kimios_user_group_idx; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY user_group
    ADD CONSTRAINT kimios_user_group_idx PRIMARY KEY (gid, user_id, authentication_source);


--
-- TOC entry 2042 (class 2606 OID 72696)
-- Dependencies: 1668 1668 1668
-- Name: kimios_user_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT kimios_user_pk PRIMARY KEY (user_id, authentication_source);


--
-- TOC entry 2044 (class 2606 OID 72698)
-- Dependencies: 1670 1670
-- Name: kimios_workflow_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY workflow
    ADD CONSTRAINT kimios_workflow_pkey PRIMARY KEY (id);


--
-- TOC entry 2046 (class 2606 OID 72700)
-- Dependencies: 1671 1671 1671 1671 1671
-- Name: kimios_workflow_status_manager_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY workflow_status_manager
    ADD CONSTRAINT kimios_workflow_status_manager_pkey PRIMARY KEY (security_entity_name, security_entity_type, workflow_status_id, security_entity_source);


--
-- TOC entry 2002 (class 2606 OID 72702)
-- Dependencies: 1641 1641
-- Name: kimios_workflow_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY workflow_status
    ADD CONSTRAINT kimios_workflow_status_pkey PRIMARY KEY (id);


--
-- TOC entry 2004 (class 2606 OID 72704)
-- Dependencies: 1643 1643
-- Name: kimios_workspace_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY entity_log
    ADD CONSTRAINT kimios_workspace_log_pkey PRIMARY KEY (id);


--
-- TOC entry 2048 (class 2606 OID 72706)
-- Dependencies: 1672 1672
-- Name: kimios_workspace_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY workspace
    ADD CONSTRAINT kimios_workspace_pkey PRIMARY KEY (id);


--
-- TOC entry 2050 (class 2606 OID 73301)
-- Dependencies: 1673 1673 1673
-- Name: rule_excl_path; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY rule_exclude_paths
    ADD CONSTRAINT rule_excl_path PRIMARY KEY (rule_id, excl_path);


--
-- TOC entry 2036 (class 2606 OID 72708)
-- Dependencies: 1665 1665 1665 1665
-- Name: user_attributes_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY user_attributes
    ADD CONSTRAINT user_attributes_pk PRIMARY KEY (user_id, authentication_source, attribute_name);


--
-- TOC entry 2057 (class 2606 OID 72709)
-- Dependencies: 1630 1985 1627
-- Name: dm_entity_acl_hash_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY dm_entity_acl
    ADD CONSTRAINT dm_entity_acl_hash_fk FOREIGN KEY (rule_hash) REFERENCES dm_security_rules(rule_hash) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2058 (class 2606 OID 72714)
-- Dependencies: 1979 1626 1627
-- Name: dm_entity_acl_path_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY dm_entity_acl
    ADD CONSTRAINT dm_entity_acl_dm_entity_id_fk FOREIGN KEY (dm_entity_id) REFERENCES dm_entity(dm_entity_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2065 (class 2606 OID 72719)
-- Dependencies: 1637 1636 1993
-- Name: dt_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document_version
    ADD CONSTRAINT dt_id_fk FOREIGN KEY (document_type_id) REFERENCES document_type(id);


--
-- TOC entry 2059 (class 2606 OID 72724)
-- Dependencies: 1977 1628 1626
-- Name: ent_attr_entity_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY dm_entity_attributes
    ADD CONSTRAINT ent_attr_entity_fk FOREIGN KEY (dm_entity_id) REFERENCES dm_entity(dm_entity_id) ON DELETE CASCADE;


--
-- TOC entry 2081 (class 2606 OID 72729)
-- Dependencies: 1651 1993 1636
-- Name: meta_dt_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meta
    ADD CONSTRAINT meta_dt_id_fk FOREIGN KEY (document_type_id) REFERENCES document_type(id) ON DELETE CASCADE;


--
-- TOC entry 2078 (class 2606 OID 72734)
-- Dependencies: 1977 1626 1646
-- Name: q_dm_entity_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY folder
    ADD CONSTRAINT q_dm_entity_id FOREIGN KEY (id) REFERENCES dm_entity(dm_entity_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2102 (class 2606 OID 72739)
-- Dependencies: 1672 1977 1626
-- Name: q_dm_entity_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY workspace
    ADD CONSTRAINT q_dm_entity_id FOREIGN KEY (id) REFERENCES dm_entity(dm_entity_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2060 (class 2606 OID 72744)
-- Dependencies: 1626 1977 1634
-- Name: q_dm_entity_uid; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document
    ADD CONSTRAINT q_dm_entity_uid FOREIGN KEY (id) REFERENCES dm_entity(dm_entity_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2076 (class 2606 OID 72749)
-- Dependencies: 1965 1619 1643
-- Name: kimios__workspace_log_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY entity_log
    ADD CONSTRAINT kimios__workspace_log_fk FOREIGN KEY (user_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2051 (class 2606 OID 72754)
-- Dependencies: 1619 1965 1618
-- Name: kimios_authentication_params_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY authentication_params
    ADD CONSTRAINT kimios_authentication_params_fkey FOREIGN KEY (authentication_source_name) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2052 (class 2606 OID 72759)
-- Dependencies: 1634 1621 1989
-- Name: kimios_checkout_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY checkout
    ADD CONSTRAINT kimios_checkout_fk FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE;


--
-- TOC entry 2053 (class 2606 OID 72764)
-- Dependencies: 1619 1965 1621
-- Name: kimios_checkout_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY checkout
    ADD CONSTRAINT kimios_checkout_fk1 FOREIGN KEY (user_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2055 (class 2606 OID 72774)
-- Dependencies: 1965 1619 1625
-- Name: kimios_data_transaction_authentication_source_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY data_transaction
    ADD CONSTRAINT kimios_data_transaction_authentication_source_fk FOREIGN KEY (user_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2056 (class 2606 OID 72779)
-- Dependencies: 1995 1625 1637
-- Name: kimios_data_transaction_document_version_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY data_transaction
    ADD CONSTRAINT kimios_data_transaction_document_version_fk FOREIGN KEY (document_version_id) REFERENCES document_version(id) ON DELETE CASCADE;


--
-- TOC entry 2062 (class 2606 OID 72784)
-- Dependencies: 1965 1635 1619
-- Name: kimios_document_comment_author_source_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document_comment
    ADD CONSTRAINT kimios_document_comment_author_source_fkey FOREIGN KEY (author_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2063 (class 2606 OID 72789)
-- Dependencies: 1995 1637 1635
-- Name: kimios_document_comment_document_version_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document_comment
    ADD CONSTRAINT kimios_document_comment_document_version_fk FOREIGN KEY (document_version_id) REFERENCES document_version(id) ON DELETE CASCADE;


--
-- TOC entry 2061 (class 2606 OID 72794)
-- Dependencies: 1646 1634 2007
-- Name: kimios_document_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document
    ADD CONSTRAINT kimios_document_fk FOREIGN KEY (folder_id) REFERENCES folder(id) ON DELETE CASCADE;


--
-- TOC entry 2064 (class 2606 OID 72799)
-- Dependencies: 1636 1636 1993
-- Name: kimios_document_type_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document_type
    ADD CONSTRAINT kimios_document_type_fk FOREIGN KEY (document_type_id) REFERENCES document_type(id) ON DELETE CASCADE;


--
-- TOC entry 2066 (class 2606 OID 72804)
-- Dependencies: 1637 1634 1989
-- Name: kimios_document_version_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document_version
    ADD CONSTRAINT kimios_document_version_fk FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE SET NULL;


--
-- TOC entry 2067 (class 2606 OID 72809)
-- Dependencies: 1965 1637 1619
-- Name: kimios_document_version_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document_version
    ADD CONSTRAINT kimios_document_version_fk1 FOREIGN KEY (author_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2070 (class 2606 OID 72814)
-- Dependencies: 1634 1639 1989
-- Name: kimios_document_workflow_request_document_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document_workflow_status_request
    ADD CONSTRAINT kimios_document_workflow_request_document_uid_fk FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE;


--
-- TOC entry 2071 (class 2606 OID 72819)
-- Dependencies: 1641 1639 2001
-- Name: kimios_document_workflow_status_reque_workflow_status_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document_workflow_status_request
    ADD CONSTRAINT kimios_document_workflow_status_reque_workflow_status_uid_fkey FOREIGN KEY (workflow_status_id) REFERENCES workflow_status(id) ON DELETE CASCADE;


--
-- TOC entry 2072 (class 2606 OID 72824)
-- Dependencies: 1619 1639 1965
-- Name: kimios_document_workflow_status_request_fk5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document_workflow_status_request
    ADD CONSTRAINT kimios_document_workflow_status_request_fk5 FOREIGN KEY (validator_user_source) REFERENCES authentication_source(source_name);


--
-- TOC entry 2073 (class 2606 OID 72829)
-- Dependencies: 1619 1639 1965
-- Name: kimios_document_workflow_status_request_user_source_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document_workflow_status_request
    ADD CONSTRAINT kimios_document_workflow_status_request_user_source_fkey FOREIGN KEY (user_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2068 (class 2606 OID 72834)
-- Dependencies: 1641 1638 2001
-- Name: kimios_document_workflow_status_workflow_status_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document_workflow_status
    ADD CONSTRAINT kimios_document_workflow_status_workflow_status_uid_fkey FOREIGN KEY (workflow_status_id) REFERENCES workflow_status(id) ON DELETE CASCADE;


--
-- TOC entry 2069 (class 2606 OID 72839)
-- Dependencies: 1634 1638 1989
-- Name: kimios_document_worklow_status_document_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY document_workflow_status
    ADD CONSTRAINT kimios_document_worklow_status_document_fk FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE;


--
-- TOC entry 2077 (class 2606 OID 72844)
-- Dependencies: 1655 1645 2019
-- Name: kimios_enumeration_value_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY enumeration_value
    ADD CONSTRAINT kimios_enumeration_value_fk FOREIGN KEY (enumeration_id) REFERENCES meta_feed(id) ON DELETE CASCADE;


--
-- TOC entry 2080 (class 2606 OID 72849)
-- Dependencies: 1649 1619 1965
-- Name: kimios_group_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT kimios_group_fk FOREIGN KEY (authentication_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2079 (class 2606 OID 72854)
-- Dependencies: 1648 1619 1965
-- Name: kimios_log_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY generic_log
    ADD CONSTRAINT kimios_log_fk FOREIGN KEY (user_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2083 (class 2606 OID 72859)
-- Dependencies: 2013 1652 1651
-- Name: kimios_meta_boolean_value_meta_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meta_boolean_value
    ADD CONSTRAINT kimios_meta_boolean_value_meta_uid_fkey FOREIGN KEY (meta_id) REFERENCES meta(id) ON DELETE CASCADE;


--
-- TOC entry 2085 (class 2606 OID 72864)
-- Dependencies: 1651 1653 2013
-- Name: kimios_meta_date_value_meta_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meta_date_value
    ADD CONSTRAINT kimios_meta_date_value_meta_uid_fkey FOREIGN KEY (meta_id) REFERENCES meta(id) ON DELETE CASCADE;


--
-- TOC entry 2082 (class 2606 OID 72869)
-- Dependencies: 1655 1651 2019
-- Name: kimios_meta_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meta
    ADD CONSTRAINT kimios_meta_fk1 FOREIGN KEY (meta_feed_id) REFERENCES meta_feed(id) ON DELETE SET NULL;


--
-- TOC entry 2087 (class 2606 OID 72874)
-- Dependencies: 1651 1656 2013
-- Name: kimios_meta_number_value_meta_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meta_number_value
    ADD CONSTRAINT kimios_meta_number_value_meta_uid_fkey FOREIGN KEY (meta_id) REFERENCES meta(id) ON DELETE CASCADE;


--
-- TOC entry 2089 (class 2606 OID 72879)
-- Dependencies: 1651 1657 2013
-- Name: kimios_meta_string_value_meta_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meta_string_value
    ADD CONSTRAINT kimios_meta_string_value_meta_uid_fkey FOREIGN KEY (meta_id) REFERENCES meta(id) ON DELETE CASCADE;


--
-- TOC entry 2084 (class 2606 OID 72884)
-- Dependencies: 1637 1652 1995
-- Name: kimios_metabool_doc_version_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meta_boolean_value
    ADD CONSTRAINT kimios_metabool_doc_version_fk FOREIGN KEY (document_version_id) REFERENCES document_version(id) ON DELETE CASCADE;


--
-- TOC entry 2086 (class 2606 OID 72889)
-- Dependencies: 1637 1653 1995
-- Name: kimios_metadate_doc_version_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meta_date_value
    ADD CONSTRAINT kimios_metadate_doc_version_fk FOREIGN KEY (document_version_id) REFERENCES document_version(id) ON DELETE CASCADE;


--
-- TOC entry 2088 (class 2606 OID 72894)
-- Dependencies: 1656 1995 1637
-- Name: kimios_metanum_doc_version_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meta_number_value
    ADD CONSTRAINT kimios_metanum_doc_version_fk FOREIGN KEY (document_version_id) REFERENCES document_version(id) ON DELETE CASCADE;


--
-- TOC entry 2090 (class 2606 OID 72899)
-- Dependencies: 1995 1637 1657
-- Name: kimios_metastring_doc_version_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY meta_string_value
    ADD CONSTRAINT kimios_metastring_doc_version_fk FOREIGN KEY (document_version_id) REFERENCES document_version(id) ON DELETE CASCADE;


--
-- TOC entry 2091 (class 2606 OID 72904)
-- Dependencies: 1989 1658 1634
-- Name: kimios_rel_doc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY related_documents
    ADD CONSTRAINT kimios_rel_doc FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE;


--
-- TOC entry 2092 (class 2606 OID 72909)
-- Dependencies: 1989 1634 1658
-- Name: kimios_rel_doc2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY related_documents
    ADD CONSTRAINT kimios_rel_doc2 FOREIGN KEY (related_document_id) REFERENCES document(id) ON DELETE CASCADE;


--
-- TOC entry 2093 (class 2606 OID 72914)
-- Dependencies: 1965 1619 1659
-- Name: kimios_role_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT kimios_role_fk FOREIGN KEY (user_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2094 (class 2606 OID 72919)
-- Dependencies: 1660 1661 2031
-- Name: kimios_rule_event_rule_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rule_event
    ADD CONSTRAINT kimios_rule_event_rule_id_fk FOREIGN KEY (rule_id) REFERENCES rules(id) ON DELETE CASCADE;


--
-- TOC entry 2095 (class 2606 OID 72924)
-- Dependencies: 1663 1661 2031
-- Name: kimios_rule_param_rule_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rule_param
    ADD CONSTRAINT kimios_rule_param_rule_fk FOREIGN KEY (rule_id) REFERENCES rules(id) ON DELETE CASCADE;


--
-- TOC entry 2098 (class 2606 OID 72929)
-- Dependencies: 1667 1965 1619
-- Name: kimios_session_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_session
    ADD CONSTRAINT kimios_session_fk FOREIGN KEY (user_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2099 (class 2606 OID 72934)
-- Dependencies: 1619 1668 1965
-- Name: kimios_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT kimios_user_fk FOREIGN KEY (authentication_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2097 (class 2606 OID 72939)
-- Dependencies: 1619 1965 1666
-- Name: kimios_user_group_fk2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_group
    ADD CONSTRAINT kimios_user_group_fk2 FOREIGN KEY (authentication_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2074 (class 2606 OID 72944)
-- Dependencies: 1641 2043 1670
-- Name: kimios_workflow_status_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY workflow_status
    ADD CONSTRAINT kimios_workflow_status_fk FOREIGN KEY (workflow_id) REFERENCES workflow(id) ON DELETE CASCADE;


--
-- TOC entry 2075 (class 2606 OID 72949)
-- Dependencies: 1641 2001 1641
-- Name: kimios_workflow_status_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY workflow_status
    ADD CONSTRAINT kimios_workflow_status_fk1 FOREIGN KEY (successor_id) REFERENCES workflow_status(id) ON DELETE SET NULL;


--
-- TOC entry 2100 (class 2606 OID 72954)
-- Dependencies: 1619 1671 1965
-- Name: kimios_workflow_status_manager_fkey1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY workflow_status_manager
    ADD CONSTRAINT kimios_workflow_status_manager_fkey1 FOREIGN KEY (security_entity_source) REFERENCES authentication_source(source_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 2101 (class 2606 OID 72959)
-- Dependencies: 2001 1641 1671
-- Name: kimios_workflow_status_manager_workflow_fkey2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY workflow_status_manager
    ADD CONSTRAINT kimios_workflow_status_manager_workflow_fkey2 FOREIGN KEY (workflow_status_id) REFERENCES workflow_status(id) ON DELETE CASCADE;


--
-- TOC entry 2103 (class 2606 OID 73302)
-- Dependencies: 2031 1673 1661
-- Name: rules_excl_path; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY rule_exclude_paths
    ADD CONSTRAINT rules_excl_path FOREIGN KEY (rule_id) REFERENCES rules(id) ON DELETE CASCADE;


--
-- TOC entry 2096 (class 2606 OID 72964)
-- Dependencies: 2041 1665 1665 1668 1668
-- Name: user_attributes_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_attributes
    ADD CONSTRAINT user_attributes_user_fk FOREIGN KEY (user_id, authentication_source) REFERENCES users(user_id, authentication_source) ON DELETE CASCADE;


--
-- TOC entry 2108 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2010-11-05 11:36:52

--
-- PostgreSQL database dump complete
--

INSERT INTO authentication_source (source_name, java_class) values ('kimios', 'org.kimios.kernel.user.impl.HAuthenticationSource');
INSERT INTO users (user_id, user_password, user_fullname, mail, authentication_source) values ('admin', md5('kimios'), 'kimios Supervisor', 'admin@kimiosdms.com', 'kimios');
INSERT INTO roles (role_value, username, user_source) values ('1', 'admin', 'kimios');
INSERT INTO roles (role_value, username, user_source) values ('2', 'admin', 'kimios');
INSERT INTO roles (role_value, username, user_source) values ('3', 'admin', 'kimios');
INSERT INTO roles (role_value, username, user_source) values ('5', 'admin', 'kimios');





