DROP TABLE IF EXISTS identityexport_profile ;
CREATE TABLE identityexport_profile (
    id_profile int AUTO_INCREMENT,
    name varchar(255) default '' NOT NULL,
    certifier_code varchar(255) default '' NOT NULL,
    file_name varchar(255) default '',
    is_monparis SMALLINT,
    is_auto_extract SMALLINT,
    password varchar(255) default '',
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
PRIMARY KEY (id_profile)
);