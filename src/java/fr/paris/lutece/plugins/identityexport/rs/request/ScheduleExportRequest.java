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
import fr.paris.lutece.plugins.identitystore.web.exception.ClientAuthorizationException;
import fr.paris.lutece.plugins.identitystore.web.exception.DuplicatesConsistencyException;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.plugins.identitystore.web.exception.RequestContentFormattingException;
import fr.paris.lutece.plugins.identitystore.web.exception.RequestFormatException;
import fr.paris.lutece.plugins.identitystore.web.exception.ResourceConsistencyException;
import fr.paris.lutece.plugins.identitystore.web.exception.ResourceNotFoundException;

public class ScheduleExportRequest extends AbstractIdentityStoreRequest {

    private final ExportModelScheduleRequest request;
    private Profile exportModel;

    public ScheduleExportRequest(final String strClientCode, final String authorName, final String authorType, final ExportModelScheduleRequest request)
            throws IdentityStoreException {
        super(strClientCode, authorName, authorType);
        this.request = request;
    }

    @Override
    protected void fetchResources() throws ResourceNotFoundException {
        if (request != null && request.getExportModelId() != null) {
            exportModel = ProfileHome.findByPrimaryKey(request.getExportModelId()).orElseThrow(
                    () -> new ResourceNotFoundException("Export model not found", Constants.PROPERTY_REST_ERROR_RESOURCE_NOT_FOUND));
        }
    }

    @Override
    protected void validateRequestFormat() throws RequestFormatException {
        ExportRequestValidator.getInstance().validateScheduleExportRequest(request);
    }

    @Override
    protected void validateClientAuthorization() throws ClientAuthorizationException {
        // do nothing
    }

    @Override
    protected void validateResourcesConsistency() throws ResourceConsistencyException {
        if (ExtractRequestHome.findByPrimaryKey(request.getExportModelId()).isPresent()) {
            throw new ResourceConsistencyException("Extract already in progress on this export model", Constants.PROPERTY_REST_ERROR_SCHEDULE_EXPORT_ALREADY_IN_PROGRESS);
        }
    }

    @Override
    protected void formatRequestContent() throws RequestContentFormattingException {
        // do nothing
    }

    @Override
    protected void checkDuplicatesConsistency() throws DuplicatesConsistencyException {
        // do nothing
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
