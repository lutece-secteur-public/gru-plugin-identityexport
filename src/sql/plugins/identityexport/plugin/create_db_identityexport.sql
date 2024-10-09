DROP TABLE IF EXISTS identityexport_profile ;
CREATE TABLE identityexport_profile (
    id_profile int AUTO_INCREMENT,
    name varchar(255) default '' NOT NULL,
    certifier_code varchar(255) default '' NOT NULL,
    file_name varchar(255) default '',
    is_monparis SMALLINT,
    is_auto_extract SMALLINT,
    auto_extract_interval int,
    password varchar(255) default '',
    last_extract_date timestamp,
PRIMARY KEY (id_profile)
);

DROP TABLE IF EXISTS identityexport_profile_attributes ;
CREATE TABLE identityexport_profile_attributes (
	id_profile_attributes int AUTO_INCREMENT,
    id_profile int ,
    attribute_key varchar(255) NOT NULL,
PRIMARY KEY (id_profile_attributes)
);

DROP TABLE IF EXISTS identityexport_daemon_stack ;
DROP TABLE IF EXISTS identityexport_daemon_stack;
CREATE TABLE identityexport_daemon_stack (
    id_profile int,
    date_create timestamp,
    recipient_email varchar(255),
PRIMARY KEY (id_profile)
);