package fr.paris.lutece.plugins.identityexport.rs.error;

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.error.ErrorResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.identitystore.web.exception.ResourceNotFoundException;
import fr.paris.lutece.plugins.rest.service.mapper.GenericUncaughtExceptionMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper designed to intercept uncaught {@link ResourceNotFoundException}.<br/>
 */
@Provider
public class UncaughtResourceNotFoundExceptionMapper extends GenericUncaughtExceptionMapper<ResourceNotFoundException, ResponseDto>
{

    public static final String ERROR_RESOURCE_NOT_FOUND = "The requested resource was not found";

    @Override
    protected Response.Status getStatus(final ResourceNotFoundException e)
    {
        if ( e.getResponse( ) != null )
        {
            return Response.Status.fromStatusCode( e.getResponse( ).getStatus( ).getHttpCode( ) );
        }
        return Response.Status.NOT_FOUND;
    }

    @Override
    protected ResponseDto getBody( final ResourceNotFoundException e )
    {
        if ( e.getResponse( ) != null )
        {
            return e.getResponse( );
        }
        final ErrorResponse response = new ErrorResponse( );
        response.setStatus(ResponseStatusFactory.notFound().setMessage(ERROR_RESOURCE_NOT_FOUND + " :: " + e.getMessage())
                                                .setMessageKey(Constants.PROPERTY_REST_ERROR_RESOURCE_NOT_FOUND));
        return response;
    }

    @Override
    protected String getType( )
    {
        return MediaType.APPLICATION_JSON;
    }
}
