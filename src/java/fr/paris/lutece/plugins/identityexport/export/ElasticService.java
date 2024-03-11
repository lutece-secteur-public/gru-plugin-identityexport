package fr.paris.lutece.plugins.identityexport.export;

import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.libraryelastic.util.ElasticConnexion;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

public class ElasticService {



	public static String selectElasticField( List<String> lstFields, List<String> lstCertifLevel, boolean strMonParis, String strIdPit )
	{
		StringJoiner joiner = new StringJoiner(",");

		for ( String fieldRequest : lstFields )
		{	
			joiner.add("\"attributes." + fieldRequest + "\"");
		}
		String joinedString = joiner.toString();

		StringJoiner joinerCertifCodes = new StringJoiner(",");

		for ( String fieldCertifs : lstCertifLevel )
		{	
			joinerCertifCodes.add("\"" + fieldCertifs + "\"");
		}
		String strJoinerCertifCodes = joinerCertifCodes.toString();

		//Elastic elastic = new Elastic( AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_PROVIDER_URL ), AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_NAME), AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_PASSWORD ) );
		ElasticConnexion elasticConnex = new ElasticConnexion( AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_NAME), AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_PASSWORD ) );
		String search = "";
		try {

			/*
	    		String request = "{\"size\": 10000 "
	    				+ " ,\"_source\": {\n"

	    				+ "            \"includes\": [ " + joinedString + " ]\n"
	    				+ "},  \"query\": {\n"
	    				+ "    \"match\": { \"attributes.family_name.certifierCode\" : \""+ strCertifLevel + "\" }\n"
	    				+ "  } }";
			 */

			String request = "{\"size\": 10000 "
					+ " ,\"_source\": {\n"

	    				+ "            \"includes\": [ \"customerId\", \"connectionId\"," + joinedString + " ]\n"
	    				+ "}, \"query\" : {\n"
	    				+ "        \"bool\": {\n"
	    				+ "            \"must\": [\n"
	    				+ "               {\n"
	    				+ "                    \"terms\": { \"attributes.family_name.certifierCode\": [ " + strJoinerCertifCodes + " ] }\n"
	    				+ "               },\n"
	    				+ "               {\n"
	    				+ "                   \"match\": { \"monParisActive\" : "+ strMonParis + " }\n"
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
	    				+ "  }";

			//search = elastic.search(AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_INDEX ), request);
			//search = elasticConnex.POST(AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_PROVIDER_URL ) + "/" + AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_INDICE ) + "/_search?scroll=1m" , request);
			search = elasticConnex.POST(AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_PROVIDER_URL ) + "/_search" , request);

		
		} catch (HttpAccessException e) {
			AppLogService.error(e.getMessage(), e);
		}


		return search;
	}


	public static String selectElasticFieldScroll( String strScrollId )
	{


		//Elastic elastic = new Elastic( AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_PROVIDER_URL ), AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_NAME), AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_PASSWORD ) );
		ElasticConnexion elasticConnex = new ElasticConnexion( AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_NAME), AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_PASSWORD ) );
		String search = "";
		try {
			/*
	    		String request = "{\"size\": 3"
	    				+ "  }";
			 */

			String request = "{\"scroll\": \"1m\" "
					+ " ,\"scroll_id\": \""+ strScrollId + "\" }" ;

			//search = elastic.search(AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_INDEX ), request);
			search = elasticConnex.POST(AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_PROVIDER_URL ) + "/_search/scroll" , request);
			//} catch (ElasticClientException e1) {
			//	AppLogService.error( e1.getMessage(  ), e1 );
		} catch (HttpAccessException e) {
			AppLogService.error(e.getMessage(), e);
		}
		if(!StringUtils.isEmpty(search))
		{
			return search;
		}
		return search;
	}
	
	public static String selectElasticFieldSearchAfter( String[] strIdSort, String strIdPit )
	{


		//Elastic elastic = new Elastic( AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_PROVIDER_URL ), AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_NAME), AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_PASSWORD ) );
		ElasticConnexion elasticConnex = new ElasticConnexion( AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_NAME), AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_PASSWORD ) );
		String search = "";
		try {
			/*
	    		String request = "{\"size\": 3"
	    				+ "  }";
			 */

			String request = "{\"size\": 10000,"
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

			//search = elastic.search(AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_INDEX ), request);
			search = elasticConnex.POST(AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_PROVIDER_URL ) + "/_search" , request);
			//} catch (ElasticClientException e1) {
			//	AppLogService.error( e1.getMessage(  ), e1 );
		} catch (HttpAccessException e) {
			AppLogService.error(e.getMessage(), e);
		}
		if(!StringUtils.isEmpty(search))
		{
			return search;
		}
		return search;
	}
	
	public static String getElasticIdPit(  )
	{


		ElasticConnexion elasticConnex = new ElasticConnexion( AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_NAME), AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_PASSWORD ) );
		String search = "";
		try {

			String request = "" ;

			search = elasticConnex.POST(AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_PROVIDER_URL ) + "/" + AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_INDICE ) + "/_pit?keep_alive=1m" , request);

		} catch (HttpAccessException e) {
			AppLogService.error(e.getMessage(), e);
		}
		if(!StringUtils.isEmpty(search))
		{
			return search;
		}
		return search;
	}
}

