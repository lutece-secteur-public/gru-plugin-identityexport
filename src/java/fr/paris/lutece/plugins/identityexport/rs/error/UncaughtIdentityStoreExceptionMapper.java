package fr.paris.lutece.plugins.identityexport.rs.error;

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.error.ErrorResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.plugins.rest.service.mapper.GenericUncaughtExceptionMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper designed to intercept uncaught {@link IdentityStoreException}.<br/>
 */
@Provider
public class UncaughtIdentityStoreExceptionMapper extends GenericUncaughtExceptionMapper<IdentityStoreException, ErrorResponse>
{
    @Override
    protected Response.Status getStatus(final IdentityStoreException exception)
    {
        return Response.Status.INTERNAL_SERVER_ERROR;
    }

    @Override
    protected ErrorResponse getBody( final IdentityStoreException e )
    {
        final ErrorResponse response = new ErrorResponse( );
        response.setStatus(ResponseStatusFactory.internalServerError().setMessage(ERROR_DURING_TREATMENT + " :: " + e.getMessage())
                                                .setMessageKey(Constants.PROPERTY_REST_ERROR_DURING_TREATMENT));
        return response;
    }

    @Override
    protected String getType( )
    {
        return MediaType.APPLICATION_JSON;
    }
}
