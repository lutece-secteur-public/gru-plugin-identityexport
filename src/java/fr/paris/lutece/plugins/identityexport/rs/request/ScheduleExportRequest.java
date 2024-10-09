package fr.paris.lutece.plugins.identityexport.rs.request;

import fr.paris.lutece.plugins.identityexport.business.ExportRequest;
import fr.paris.lutece.plugins.identityexport.business.ExtractRequestHome;
import fr.paris.lutece.plugins.identityexport.business.Profile;
import fr.paris.lutece.plugins.identityexport.business.ProfileHome;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.AbstractIdentityStoreRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.ExportRequestValidator;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.exporting.ExportModelScheduleRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.exporting.ExportModelScheduleResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;

public class ScheduleExportRequest extends AbstractIdentityStoreRequest {

    private final ExportModelScheduleRequest request;
    private Profile exportModel;

    public ScheduleExportRequest(final String strClientCode, final String authorName, final String authorType, final ExportModelScheduleRequest request)
            throws IdentityStoreException {
        super(strClientCode, authorName, authorType);
        this.request = request;
    }

    @Override
    protected void validateSpecificRequest() throws IdentityStoreException {
        ExportRequestValidator.getInstance().validateScheduleExportRequest(request);
        exportModel = ProfileHome.findByPrimaryKey(request.getExportModelId()).orElseThrow(() -> new IdentityStoreException("Export model not found"));
        if (ExtractRequestHome.findByPrimaryKey(request.getExportModelId()).isPresent()) {
            throw new IdentityStoreException("Extract already in progress on this export model");
        }
    }

    @Override
    protected ExportModelScheduleResponse doSpecificRequest() throws IdentityStoreException {
        final ExportRequest extract = new ExportRequest();
        extract.setIdProfil(exportModel.getId());
        extract.setRecipientEmail(request.getEmail());
        ExtractRequestHome.create(extract);

        final ExportModelScheduleResponse response = new ExportModelScheduleResponse();
        response.setPwd(exportModel.getPassword());
        response.setStatus(ResponseStatusFactory.success().setMessageKey(Constants.PROPERTY_REST_INFO_SUCCESSFUL_OPERATION));

        return response;
    }
}
