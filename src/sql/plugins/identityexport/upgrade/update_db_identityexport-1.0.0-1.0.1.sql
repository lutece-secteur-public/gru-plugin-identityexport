alter table identityexport_profile add column is_export_cuid SMALLINT DEFAULT 1;
alter table identityexport_profile add column is_export_guid SMALLINT DEFAULT 1;

alter table identityexport_daemon_stack add column progress_token varchar(255);