package fr.paris.lutece.plugins.identityexport.rbac;

import fr.paris.lutece.plugins.identityexport.business.Profile;
import fr.paris.lutece.plugins.identityexport.business.ProfileHome;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class AccessExportProfileResourceIdService extends ResourceIdService {

    private static final String PLUGIN_NAME = "identityexport";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "identityexport.rbac.access.export.profile.label";
    private static final String PROPERTY_LABEL_READ = "identityexport.rbac.access.export.profile.permission.read";
    private static final String PROPERTY_LABEL_WRITE = "identityexport.rbac.access.export.profile.permission.write";
    private static final String PROPERTY_LABEL_CREATE = "identityexport.rbac.access.export.profile.permission.create";

    @Override
    public void register() {
        final ResourceType rt = new ResourceType( );
        rt.setResourceIdServiceClass( this.getClass().getName() );
        rt.setPluginName( PLUGIN_NAME );
        rt.setResourceTypeKey( AccessExportProfileResource.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        final Permission permRead = new Permission( );
        permRead.setPermissionKey( AccessExportProfileResource.PERMISSION_READ );
        permRead.setPermissionTitleKey( PROPERTY_LABEL_READ );
        rt.registerPermission( permRead );

        final Permission permWrite = new Permission( );
        permWrite.setPermissionKey( AccessExportProfileResource.PERMISSION_WRITE );
        permWrite.setPermissionTitleKey( PROPERTY_LABEL_WRITE );
        rt.registerPermission( permWrite );

        final Permission permCreate = new Permission( );
        permCreate.setPermissionKey( AccessExportProfileResource.PERMISSION_CREATE );
        permCreate.setPermissionTitleKey( PROPERTY_LABEL_CREATE );
        rt.registerPermission( permCreate );

        ResourceTypeManager.registerResourceType(rt);
    }

    @Override
    public ReferenceList getResourceIdList(final Locale locale) {
        return ProfileHome.getProfilsReferenceList();
    }

    @Override
    public String getTitle(final String strId, final Locale locale) {
        final Profile resource = ProfileHome.findByPrimaryKey(Integer.parseInt(strId)).orElse(null);
        return resource == null ? StringUtils.EMPTY : resource.getName();
    }
}
