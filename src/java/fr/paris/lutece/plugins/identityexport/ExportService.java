package fr.paris.lutece.plugins.identityexport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.identityexport.business.ElasticsearchResponseJSON;
import fr.paris.lutece.plugins.identityexport.business.ElasticsearchResponseJSON.Hit;
import fr.paris.lutece.plugins.identityexport.business.ExportAttribute;
import fr.paris.lutece.plugins.identityexport.business.ExportAttributeHome;
import fr.paris.lutece.plugins.identityexport.business.Profile;
import fr.paris.lutece.plugins.identityexport.business.ProfileHome;
import fr.paris.lutece.plugins.identityexport.export.Constants;
import fr.paris.lutece.plugins.identityexport.export.ElasticService;
import fr.paris.lutece.plugins.identityexport.export.ProfileGenerator;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AuthorType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.RequestAuthor;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.referentiel.AttributeCertificationProcessusDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.referentiel.ProcessusSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.service.ReferentialService;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class ExportService {

	private static ObjectMapper _mapper = (new ObjectMapper( )).configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
	private static IFileStoreServiceProvider _fileStoreService = FileService.getInstance().getFileStoreServiceProvider( Constants.LOCAL_FILESYSTEM_DIRECTORY );

	/**
	 * Process Export
	 * 
	 * @param nIdProfil
	 * @return message
	 * @throws IOException 
	 */
	public static String generateExport( int nIdProfile ) throws IOException
	{
		List<String> lstFields = new ArrayList<>( );
		List<String> lstFieldsGuidCuid = new ArrayList<>();
		List<String> lstCertifCodes = new ArrayList<>();
		lstFieldsGuidCuid.add( Constants.CUID_ATTRIBUTE_KEY );
		lstFieldsGuidCuid.add( Constants.GUID_ATTRIBUTE_KEY );

		Optional<Profile> optProfile = ProfileHome.findByPrimaryKey( nIdProfile );

		List<ExportAttribute> lstAttributesByIdProfil = ExportAttributeHome.getExportAttributeListByIdProfil( nIdProfile );
		for ( ExportAttribute attr : lstAttributesByIdProfil )
		{
			lstFields.add( attr.getKey( ) );
		}

		if ( lstFields == null || lstFields.isEmpty( ) || optProfile.isEmpty( ) )
		{
			return "nothing to export";
		}

		ProfileGenerator genProfile = new ProfileGenerator ( optProfile.get( ) );

		lstCertifCodes = getLstCertifCode( genProfile.getCertification( ) );

		String strIdPit = ElasticService.getElasticPitId( );

		// get first result set of ELS
		String resultElastic = ElasticService.selectElasticField( lstFields, lstCertifCodes, optProfile.get().isMonParis( ), strIdPit  );

		if ( resultElastic.isEmpty( ) )
		{
			return "nothing to export";
		}

		// write headers
		genProfile.addContent( getHeaders( lstFieldsGuidCuid, lstFields ) ); 

		ElasticsearchResponseJSON response = _mapper.readValue(resultElastic, ElasticsearchResponseJSON.class);

		List<Hit> lstHits = response.getHits( ).getHits( );

		String strSortId ="";
		String[] strSortIdTab = new String[] {};
		StringBuilder strContent = new StringBuilder( );
		for ( Hit hit : lstHits)
		{
			// get one line
			strContent.append( getLineFromHit( hit, lstFields ) );

			AppLogService.debug("shard : " + hit.getSort( )[0] );

			strSortId = hit.getSort( )[0];
			strSortIdTab = hit.getSort( );
		}

		// write first set of lines in file
		genProfile.addContent( strContent.toString( ) );

		// fetch results
		//while ( strSortId != null && !strSortId.isEmpty() && strIdPit != null && !strIdPit.isEmpty())
		while ( strSortId != null && !strSortId.isEmpty() )
		{
			String resultElasticScroll = ElasticService.selectElasticFieldSearchAfter(strSortIdTab, strIdPit, lstFields, lstCertifCodes, optProfile.get().isMonParis( ));

			if ( resultElasticScroll.isEmpty( ) )
			{
				strSortId = StringUtils.EMPTY;
				continue;
			}

			ElasticsearchResponseJSON responseElasticSearch = _mapper.readValue(resultElasticScroll, ElasticsearchResponseJSON.class);
			//strIdPit = (String) responseElasticSearch.getPit_id();

			if ( !responseElasticSearch.getHits( ).getHits( ).isEmpty( ) )
			{
				strContent = new StringBuilder( );
				for ( Hit hit : responseElasticSearch.getHits( ).getHits( ) )
				{
					strContent.append( getLineFromHit( hit, lstFields ) );

					AppLogService.debug(" shard : " + hit.getSort( )[0]);

					strSortId = hit.getSort( )[0];
					strSortIdTab = hit.getSort( );
				}

				// write lines
				genProfile.addContent( strContent.toString( ) );
			}
			else
			{
				strSortId = StringUtils.EMPTY;
			}
		}

		// finalize and  zip
		File zipFile = genProfile.finalizeAndGenerateZipFile( );

		// store result in FileService
		_fileStoreService.storeFile( zipFile );
		AppLogService.debug( "fichier cree : " + genProfile.getName( ) );


		lstFields = new ArrayList<>();


		return "Export of id profile : " + nIdProfile + " done." ;

	}

	/**
	 * prepare headers
	 * 
	 * @param lstFields
	 * @return
	 */
	private static String getHeaders(List<String> lstFieldsGuidCuid, List<String> lstFields) 
	{
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

		return joinerHeaders.toString( ) + System.lineSeparator();
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
		.append( System.lineSeparator( ) );

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
