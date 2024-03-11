package fr.paris.lutece.plugins.identityexport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.identityexport.business.ElasticsearchResponseJSON;
import fr.paris.lutece.plugins.identityexport.business.ElasticsearchResponseJSON.Hit;
import fr.paris.lutece.plugins.identityexport.business.ExportAttribute;
import fr.paris.lutece.plugins.identityexport.business.ExportAttributeHome;
import fr.paris.lutece.plugins.identityexport.business.Profile;
import fr.paris.lutece.plugins.identityexport.business.ProfileHome;
import fr.paris.lutece.plugins.identityexport.export.Constants;
import fr.paris.lutece.plugins.identityexport.export.ElasticService;
import fr.paris.lutece.plugins.identityexport.export.ExportFileService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AuthorType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.RequestAuthor;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.referentiel.AttributeCertificationProcessusDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.referentiel.ProcessusSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.service.ReferentialService;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class ExportService {

	private static ObjectMapper _mapper = (new ObjectMapper( )).configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );

	/**
	 * Process Export
	 * 
	 * @param nIdProfil
	 * @return message
	 */
	public static String generateExport( int nIdProfil )
	{
		List<String> lstFields = new ArrayList<>( );
		List<String> lstFieldsGuidCuid = new ArrayList<>();
		List<String> lstCertifCodes = new ArrayList<>();
		lstFieldsGuidCuid.add( Constants.CUID_ATTRIBUTE_KEY );
		lstFieldsGuidCuid.add( Constants.GUID_ATTRIBUTE_KEY );


		List<ExportAttribute> lstAttributesByIdProfil = ExportAttributeHome.getExportAttributeListByIdProfil( nIdProfil );
		for ( ExportAttribute attr : lstAttributesByIdProfil )
		{
			lstFields.add( attr.getKey( ) );
		}

		Optional<Profile> profil = ProfileHome.findByPrimaryKey( nIdProfil );

		if ( lstFields == null || lstFields.isEmpty( ) ) 
		{
			return "nothing to export";
		}


		lstCertifCodes = getLstCertifCode( profil.get().getCertification( ) );

		String strIdPitJSON = ElasticService.getElasticIdPit( );
		String strIdPit = "";
		String strSortId = "";
		String[] strSortIdTab = null;
		JsonNode node;
		try {
			node = new ObjectMapper().readTree(strIdPitJSON);
			if (node.has("id")) {
				strIdPit = node.get("id").asText( );
				}
		} catch (JsonMappingException e1) {
			AppLogService.error( e1.getMessage(  ), e1 );
		} catch (JsonProcessingException e1) {
			AppLogService.error( e1.getMessage(  ), e1 );
		}
		
		  
		
		String resultElastic = ElasticService.selectElasticField( lstFields, lstCertifCodes, profil.get().isMonParis( ), strIdPit  );

		if ( resultElastic.isEmpty( ) )
		{
			return "nothing to export";
		}

		try 
		{
			// preprare headers
			StringJoiner joinerHeaders = new StringJoiner( AppPropertiesService.getProperty( Constants.PROPERTY_SEPARATOR ) );

			for ( String fieldRequest : lstFieldsGuidCuid )
			{	
				joinerHeaders.add(fieldRequest);
			}
			for ( String fieldRequest : lstFields )
			{	
				joinerHeaders.add(fieldRequest);
			}
			
			// add certifiers headers
			for ( String fieldRequest : lstFields )
			{	
				joinerHeaders.add(fieldRequest+"_certifier");
			}
			String joinedHeadersString = joinerHeaders.toString();
			
			// write headers
			//ExportFileService.addContentToFile( null, joinerHeaders.toString( ) );
			String strTmpDirsLocation = System.getProperty("java.io.tmpdir");
			ExportFileService.newFileCSV( profil.get( ).getFileName( ), joinedHeadersString, strTmpDirsLocation);
			
			
			// get first result set of ELS
			ElasticsearchResponseJSON response = _mapper.readValue(resultElastic, ElasticsearchResponseJSON.class);
			String strScrollID = response.get_scroll_id( );

			//AppLogService.debug("SORT : " + response.get_scroll_id() );
			List<Hit> lstHits = response.getHits().getHits();

			StringBuilder strContent = new StringBuilder( );
			for ( Hit hit : lstHits)
			{
				strContent.append( getLineFromHit( hit, lstFields ) );
				//AppLogService.info("connectionID : " + hit.getSort( )[0] + " shard : " + hit.getSort( )[1] );
				//AppLogService.info("shard : " + hit.getSort( )[0] );
				strSortId = hit.getSort( )[0];
				strSortIdTab = hit.getSort( );
			}

			// write first set
			ExportFileService.addContentToFile( strContent.toString( ) );

			// fetch results
			//while ( strScrollID != null && !strScrollID.isEmpty( ) )
			while ( strSortId != null && !strSortId.isEmpty() && strIdPit != null && !strIdPit.isEmpty())
			{
				//String resultElasticScroll = ElasticService.selectElasticFieldScroll( strScrollID );
				String resultElasticScroll = ElasticService.selectElasticFieldSearchAfter(strSortIdTab, strIdPit);

				if ( resultElasticScroll.isEmpty( ) )
				{
					strSortId = StringUtils.EMPTY;
					continue;
				}

				ElasticsearchResponseJSON responseElasticSearch = _mapper.readValue(resultElasticScroll, ElasticsearchResponseJSON.class);
				strIdPit = (String) responseElasticSearch.getPit_id();
				
				
				if ( !responseElasticSearch.getHits( ).getHits( ).isEmpty( ) )
				{
					strScrollID = responseElasticSearch.get_scroll_id( );
					
					strContent = new StringBuilder( );
					for ( Hit hit : responseElasticSearch.getHits( ).getHits( ) )
					{
						strContent.append( getLineFromHit( hit, lstFields ) );
						//AppLogService.info(" shard : " + hit.getSort( )[0]);
						strSortId = hit.getSort( )[0];
						strSortIdTab = hit.getSort( );
					}
					
					// write lines
					ExportFileService.addContentToFile( strContent.toString( ) );
				}
				else
				{
					strSortId = StringUtils.EMPTY;
				}
			}


			// finalize file (close ?)
			ExportFileService.close( );
			
			// zip
			ExportFileService.zipFile( profil.get( ).getFileName( ), profil.get( ).getPassword( ), strTmpDirsLocation );
			
			//IFileStoreServiceProvider fileStoreService = FileService.getInstance( ).getFileStoreServiceProvider( );
			IFileStoreServiceProvider fileStoreService = FileService.getInstance().getFileStoreServiceProvider( Constants.LOCAL_FILESYSTEM_DIRECTORY );
			
			byte[] bytesFile;
			try {
				//bytesFile = Files.readAllBytes(Paths.get( AppPropertiesService.getProperty( Constants.PROPERTY_EXPORT_DIR_PATH ) + profil.get().getFileName() + ".zip" ));
				bytesFile = Files.readAllBytes(Paths.get( strTmpDirsLocation + "/" + profil.get().getFileName() + ".zip" ) );
			
				//File TEST_FILE = new File(AppPropertiesService.getProperty( Constants.PROPERTY_EXPORT_DIR_PATH ) + profil.get().getFileName() + ".zip" );
				File fileZIP = new File();
	
				PhysicalFile phyFile = new PhysicalFile();
				phyFile.setValue( bytesFile );
				fileZIP.setPhysicalFile( phyFile );
				fileZIP.setMimeType( "application/zip" );
				fileZIP.setTitle( profil.get().getFileName() + ".zip" );
				
				
	            fileStoreService.storeFile(fileZIP);
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
			AppLogService.debug( "fichier cree : " + profil.get().getName( ) );


		} catch (JsonProcessingException e) {
			AppLogService.error( e.getMessage(  ), e );
		}

		lstFields = new ArrayList<>();


		return "Export of id profile : " + nIdProfil + " done." ;

	}

	/**
	 * get string from hit
	 * @param hit
	 * @return the string
	 * @throws JsonProcessingException 
	 */
	private static String getLineFromHit(Hit hit, List<String> listAttributesKeys ) throws JsonProcessingException 
	{
		StringBuilder strContent = new StringBuilder( );

		String json = _mapper.writer().withDefaultPrettyPrinter( ).writeValueAsString( hit.get_source().getAttributes() );
		String cuid = hit.get_source().getCustomerId( );
		String guid = hit.get_source().getConnectionId();

		// voir si on peut rester en "objet" plutot que passer par du string
		Map<String,Object> result = _mapper.readValue(json, HashMap.class);

		if ( result == null )
		{
			return null;
		}

		StringJoiner joinerFieldValues = new StringJoiner( AppPropertiesService.getProperty( Constants.PROPERTY_SEPARATOR )  );
		StringJoiner joinerCertifValue = new StringJoiner( AppPropertiesService.getProperty( Constants.PROPERTY_SEPARATOR ) );

		joinerFieldValues.add( cuid );
		joinerFieldValues.add( guid );

		for ( String attr : listAttributesKeys )
		{	
			Map<String,String> resultField = (Map<String,String>) result.get( attr );


			if ( resultField != null )
			{
				// gender case
				if ( resultField.get("key").equals( "gender" ) )
				{
					if ( resultField.get("value").equals("1") ) {
						joinerFieldValues.add( "MME" );
					}
					else if ( resultField.get("value").equals("2") )
					{
						joinerFieldValues.add( "M" );
					}
					else
					{
						joinerFieldValues.add( "?" );
					}
				}
				else
				{
					// for all other attributes
					joinerFieldValues.add( resultField.get("value") );
				}

				joinerCertifValue.add( resultField.get("certifierCode") );
			}
			else
			{
				joinerFieldValues.add( StringUtils.EMPTY );
				joinerCertifValue.add( StringUtils.EMPTY );
			}
		}

		strContent.append( joinerFieldValues.toString( ) )
		.append( AppPropertiesService.getProperty( Constants.PROPERTY_SEPARATOR ) )
		.append( joinerCertifValue.toString( ) )
		.append( System.lineSeparator() );

		return strContent.toString( );
	}



	/**
	 * get all certifier codes that have the same level or above of a given certifier code
	 * 
	 * @param certifierCode
	 * @return the list
	 */
	private static List<String> getLstCertifCode ( String certifierCode )
	{
		List<String> lstCertifCodes = new ArrayList<>( );
		String strLvlCode = "";
		ReferentialService referentialService = SpringContextService.getBean( "referential.identityService" );
		try {

			RequestAuthor author = new RequestAuthor( );
			author.setType( AuthorType.application );
			author.setName( "Identity Export Daemon" );

			ProcessusSearchResponse processList = referentialService.getProcessList( AppPropertiesService.getProperty( Constants.PROPERTY_CODE_CLIENT ), author);


			List<AttributeCertificationProcessusDto> processusCertif = processList.getProcessus();
			for ( AttributeCertificationProcessusDto attrCertif : processusCertif )
			{
				if ( attrCertif.getCode( ).equals( certifierCode ) )
				{
					strLvlCode = attrCertif.getAttributeCertificationLevels().get(0).getLevel().getLevel();
				}
			}

			if ( !strLvlCode.isEmpty( ) )
			{
				for ( AttributeCertificationProcessusDto attrCertif : processusCertif )
				{
					if ( Integer.valueOf( attrCertif.getAttributeCertificationLevels().get(0).getLevel().getLevel() ) >= Integer.valueOf( strLvlCode ) )
					{
						lstCertifCodes.add( attrCertif.getCode( ) );
					}
				}
			}

		} catch (IdentityStoreException e) {
			AppLogService.error(e.getMessage(), e);
		}

		return lstCertifCodes;
	}

}
