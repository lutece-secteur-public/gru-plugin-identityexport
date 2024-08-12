package fr.paris.lutece.plugins.identityexport.rbac;

import fr.paris.lutece.portal.service.rbac.RBACResource;

public class AccessExportProfileResource implements RBACResource {

    // RBAC management
    public static final String RESOURCE_TYPE = "ACCESS_EXPORT_PROFILE";

    // Perimissions
    public static final String PERMISSION_READ = "READ";
    public static final String PERMISSION_WRITE = "WRITE";
    public static final String PERMISSION_CREATE = "CREATE";

    @Override
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    @Override
    public String getResourceId( )
    {
        return null;
    }
}
