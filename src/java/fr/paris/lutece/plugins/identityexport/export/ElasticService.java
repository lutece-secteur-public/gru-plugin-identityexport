package fr.paris.lutece.plugins.identityexport.export;

import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

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
	 * @param strIdPit
	 * @return
	 */
	public static String selectElasticField( List<String> lstFields, List<String> lstCertifLevel, boolean isMonParis, String strIdPit )
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
	    				+ "    \"id\":  \"" + strIdPit + "\", \n"
	    				+ "    \"keep_alive\": \"1m\"\n"
	    				+ "  },"
	    				+ "  \"sort\": [ \n"
	    				//+ "    {\"creationDate\": {\"order\": \"asc\"}}\n"
	    				+ "    {\"_shard_doc\": \"desc\"}\n"
	    				+ "  ] "
	    				+ "  }"
	    				;

		try 
		{
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
	 * @param strIdPit
	 * @return
	 */
	public static String selectElasticFieldSearchAfter( String[] strIdSort, String strIdPit )
	{
		try {

			String searchRequest = "{\"size\": 10000,"
					+ "\"pit\": {\n"
					+ "    \"id\":  \"" + strIdPit + "\", \n"
					+ "    \"keep_alive\": \"1m\"\n"
					+ "  }," 
					+ "\"sort\": [\n"
					//+ "    {\"creationDate\": \"asc\"}\n"
					+ "    {\"_shard_doc\": \"desc\"}\n"
					+ "  ],"
					+ " \"search_after\": [\n"
					//+ " \"" + strIdSort[0] + "\"," + strIdSort[1] + "\n"
					+ " \"" + strIdSort[0] + "\"\n"
					+ "  ],\n"
					+ "  \"track_total_hits\": false "
					+ "}";


			return _elasticConnex.POST(AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_PROVIDER_URL ) + "/_search" , searchRequest);

		} catch (HttpAccessException e) {
			AppLogService.error(e.getMessage(), e);
		}

		return null;
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

