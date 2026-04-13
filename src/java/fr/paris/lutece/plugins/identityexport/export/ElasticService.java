package fr.paris.lutece.plugins.identityexport.export;

import java.util.List;
import java.util.StringJoiner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.libraryelastic.util.ElasticConnexion;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

public class ElasticService {

	private static ObjectMapper _mapper = (new ObjectMapper( )).configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
	private static ElasticConnexion _elasticConnex = new ElasticConnexion( AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_NAME), AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_PASSWORD ) );


	/**
	 * selectElasticField
	 *
	 * @param lstFields
	 * @param lstCertifLevel
	 * @param isMonParis
	 * @param strPitId
	 * @return
	 */
	public static String selectElasticField( List<String> lstFields, List<String> lstCertifLevel, boolean isMonParis, String strPitId )
	{

		StringJoiner joinerFields = new StringJoiner(",");
		for ( String fieldRequest : lstFields )
		{
			joinerFields.add("\"attributes." + fieldRequest + "\"");
		}

		StringJoiner joinerCertifCodes = new StringJoiner(",");
		for ( String fieldCertifs : lstCertifLevel )
		{
			joinerCertifCodes.add("\"" + fieldCertifs + "\"");
		}

		String searchRequest = "{\"size\": 10000 "
						+ " ,\"_source\": {\n"
	    				+ "            \"includes\": [ \"customerId\", \"connectionId\"," + joinerFields.toString( ) + " ]\n"
	    				+ "}, \"query\" : {\n"
	    				+ "        \"bool\": {\n"
	    				+ "            \"must\": [\n"
	    				+ "               {\n"
	    				+ "                    \"terms\": { \"attributes.family_name.certifierCode\": [ " + joinerCertifCodes.toString( ) + " ] }\n"
	    				+ "               },\n"
	    				+ "               {\n"
	    				+ "                   \"match\": { \"monParisActive\" : "+ isMonParis + " }\n"
	    				+ "               }\n"
	    				+ "            ]\n"
	    				+ "        }\n"
	    				+ "    },"
	    				+ "\"pit\": {\n"
	    				+ "    \"id\":  \"" + strPitId + "\", \n"
	    				+ "    \"keep_alive\": \"1m\"\n"
	    				+ "  },"
	    				+ "  \"sort\": [ \n"
	    				+ "  {\"_shard_doc\": {\"order\": \"asc\"}}\n"
	    				+ "  ],"
	    				+ "  \"track_total_hits\": true "
	    				+ "  }"
	    				;

		try
		{
			AppLogService.debug("Request elastic : " + searchRequest);
			return _elasticConnex.POST(AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_PROVIDER_URL ) + "/_search" , searchRequest);

		}
		catch (HttpAccessException e)
		{
			AppLogService.error(e.getMessage(), e);
		}

		return null;
	}


	/**
	 * selectElasticFieldSearchAfter
	 *
	 * @param strIdSort
	 * @param lstFields
	 * @param lstCertifLevel
	 * @param isMonParis
	 * @param strPitId
	 * @return
	 */
	public static String selectElasticFieldSearchAfter( String[] strIdSort, List<String> lstFields, List<String> lstCertifLevel, boolean isMonParis, String strPitId )
	{
		StringJoiner joinerFields = new StringJoiner(",");
		for ( String fieldRequest : lstFields )
		{
			joinerFields.add("\"attributes." + fieldRequest + "\"");
		}

		StringJoiner joinerCertifCodes = new StringJoiner(",");
		for ( String fieldCertifs : lstCertifLevel )
		{
			joinerCertifCodes.add("\"" + fieldCertifs + "\"");
		}

		try {

			String searchRequest = "{\"size\": 10000,"
					+ " \"_source\": {\n"
    				+ "            \"includes\": [ \"customerId\", \"connectionId\"," + joinerFields.toString( ) + " ]\n"
    				+ "}, \"query\" : {\n"
    				+ "        \"bool\": {\n"
    				+ "            \"must\": [\n"
    				+ "               {\n"
    				+ "                    \"terms\": { \"attributes.family_name.certifierCode\": [ " + joinerCertifCodes.toString( ) + " ] }\n"
    				+ "               },\n"
    				+ "               {\n"
    				+ "                   \"match\": { \"monParisActive\" : "+ isMonParis + " }\n"
    				+ "               }\n"
    				+ "            ]\n"
    				+ "        }\n"
    				+ "    },"
					+ "\"pit\": {\n"
					+ "    \"id\":  \"" + strPitId + "\", \n"
					+ "    \"keep_alive\": \"1m\"\n"
					+ "  },"
					+ " \"search_after\": [\n"
					+ " " + strIdSort[0] + "\n"
					+ "  ],\n"
					+ "\"sort\": [\n"
					+ "  {\"_shard_doc\": {\"order\": \"asc\"}}\n"
					+ "  ],"
					+ "  \"track_total_hits\": false "
					+ "}";

			AppLogService.debug("Request elastic : " + searchRequest);
			return _elasticConnex.POST(AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_PROVIDER_URL ) + "/_search" , searchRequest);

		} catch (HttpAccessException e) {
			AppLogService.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * closeElasticPit
	 *
	 * ElasticConnexion.DELETE does not support a request body, and the ES
	 * close PIT API requires one.  The PIT is created with keep_alive=1m
	 * so it will be released automatically after one minute of inactivity.
	 *
	 * @param strPitId the PIT id to close
	 */
	public static void closeElasticPit( String strPitId )
	{
		try
		{
			_elasticConnex.DELETE( AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_PROVIDER_URL ) + "/_pit" );
		}
		catch (HttpAccessException e)
		{
			AppLogService.debug( "PIT will expire automatically (keep_alive=1m): " + e.getMessage() );
		}
	}

	/**
	 * getElasticPitId
	 *
	 * @return the PIT
	 */
	public static String getElasticPitId(  )
	{
		try 
		{
			String strIdPitJSON = _elasticConnex.POST(AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_PROVIDER_URL ) + "/" + AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_INDICE ) + "/_pit?keep_alive=1m" , "");

			JsonNode node;

			node = _mapper.readTree(strIdPitJSON);
			if (node.has("id")) 
			{
				// return the PIT
				return node.get("id").asText( );
			}
		} 
		catch (JsonMappingException e) 
		{
			AppLogService.error( e.getMessage(), e);
		} 
		catch ( JsonProcessingException e ) 
		{
			AppLogService.error( e.getMessage(), e);
		}
		catch (HttpAccessException e) 
		{
			AppLogService.error( e.getMessage(), e);
		}

		return null;
	}
	
}

