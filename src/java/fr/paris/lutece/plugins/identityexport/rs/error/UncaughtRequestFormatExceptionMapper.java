package fr.paris.lutece.plugins.identityexport.rs.error;


import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.error.ErrorResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.identitystore.web.exception.RequestFormatException;
import fr.paris.lutece.plugins.rest.service.mapper.GenericUncaughtExceptionMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper designed to intercept uncaught {@link RequestFormatException}.<br/>
 */
@Provider
public class UncaughtRequestFormatExceptionMapper extends GenericUncaughtExceptionMapper<RequestFormatException, ResponseDto>
{

    public static final String ERROR_REQUEST_BAD_FORMAT = "The sent request is not properly formatted";

    @Override
    protected Response.Status getStatus(final RequestFormatException e)
    {
        if ( e.getResponse( ) != null )
        {
            return Response.Status.fromStatusCode( e.getResponse( ).getStatus( ).getHttpCode( ) );
        }
        return Response.Status.BAD_REQUEST;
    }

    @Override
    protected ResponseDto getBody( final RequestFormatException e )
    {
        if ( e.getResponse( ) != null )
        {
            return e.getResponse( );
        }
        final ErrorResponse response = new ErrorResponse( );
        response.setStatus(ResponseStatusFactory.badRequest().setMessage(ERROR_REQUEST_BAD_FORMAT + " :: " + e.getMessage())
                                                .setMessageKey(Constants.PROPERTY_REST_ERROR_REQUEST_BAD_FORMAT));
        return response;
    }

    @Override
    protected String getType( )
    {
        return MediaType.APPLICATION_JSON;
    }
}
