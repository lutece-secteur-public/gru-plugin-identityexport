package fr.paris.lutece.plugins.identityexport.rs;

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseDto;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface IRestService {
    default Response buildJsonResponse(final ResponseDto entity)
    {
        return Response.status( entity.getStatus( ).getHttpCode( ) ).entity( entity ).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
