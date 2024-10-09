package fr.paris.lutece.plugins.identityexport.rs;

import fr.paris.lutece.plugins.identityexport.export.Constants;
import fr.paris.lutece.plugins.identityexport.rs.request.ScheduleExportRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.exporting.ExportModelScheduleRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.exporting.ExportModelScheduleResponse;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.plugins.rest.service.RestConstants;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(RestConstants.BASE_PATH + Constants.API_PATH )
public class ExportRest {

    public ExportRest() {

    }

    @Path(Constants.SCHEDULE_PATH)
    @POST
    @Produces( MediaType.APPLICATION_JSON )
    public Response getCityListByDate(
            final ExportModelScheduleRequest exportModelScheduleRequest,
            @HeaderParam( fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants.PARAM_CLIENT_CODE ) final String clientCode,
            @HeaderParam( fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants.PARAM_AUTHOR_NAME ) final String authorName,
            @HeaderParam( fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants.PARAM_AUTHOR_TYPE ) final String authorType,
            @HeaderParam( fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants.PARAM_APPLICATION_CODE ) @DefaultValue( "" ) final String strHeaderAppCode )
            throws IdentityStoreException {
        final ScheduleExportRequest request = new ScheduleExportRequest(clientCode, authorName, authorType, exportModelScheduleRequest);
        final ExportModelScheduleResponse response = (ExportModelScheduleResponse) request.doRequest();
        return Response.status( response.getStatus( ).getHttpCode( ) ).entity( response ).type( MediaType.APPLICATION_JSON_TYPE ).build( );
    }

}
