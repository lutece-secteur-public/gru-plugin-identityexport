
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'IDENTITYEXPORT_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('IDENTITYEXPORT_MANAGEMENT','identityexport.adminFeature.ManageExtraction.name',1,'jsp/admin/plugins/identityexport/ManageProfils.jsp','identityexport.adminFeature.ManageExtraction.description',0,'identityexport',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'IDENTITYEXPORT_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('IDENTITYEXPORT_MANAGEMENT',1);

