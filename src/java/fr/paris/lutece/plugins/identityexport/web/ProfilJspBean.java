/*
 * Copyright (c) 2002-2024, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
 	
 
package fr.paris.lutece.plugins.identityexport.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import fr.paris.lutece.plugins.identityexport.business.ElasticsearchResponseJSON;
import fr.paris.lutece.plugins.identityexport.business.ElasticsearchResponseJSON.Hit;
import fr.paris.lutece.plugins.identityexport.business.ExportAttribute;
import fr.paris.lutece.plugins.identityexport.business.ExportAttributeHome;
import fr.paris.lutece.plugins.identityexport.business.ExportRequest;
import fr.paris.lutece.plugins.identityexport.business.ExtractRequestHome;
import fr.paris.lutece.plugins.identityexport.business.Profile;
import fr.paris.lutece.plugins.identityexport.business.ProfileHome;
import fr.paris.lutece.plugins.identityexport.export.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AuthorType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.RequestAuthor;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.referentiel.AttributeCertificationLevelDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.referentiel.AttributeCertificationProcessusDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.referentiel.ProcessusSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.service.ReferentialService;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Profil features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageProfils.jsp", controllerPath = "jsp/admin/plugins/identityexport/", right = "IDENTITYEXPORT_MANAGEMENT" )
public class ProfilJspBean extends AbstractManageExtractionJspBean <Integer, Profile>
{
    // Templates
    private static final String TEMPLATE_MANAGE_PROFILS = "/admin/plugins/identityexport/manage_profils.html";
    private static final String TEMPLATE_CREATE_PROFIL = "/admin/plugins/identityexport/create_profil.html";
    private static final String TEMPLATE_MODIFY_PROFIL = "/admin/plugins/identityexport/modify_profil.html";

    // Parameters
    private static final String PARAMETER_ID_PROFIL = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_PROFILS = "identityexport.manage_profils.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_PROFIL = "identityexport.modify_profil.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_PROFIL = "identityexport.create_profil.pageTitle";

    // Markers
    private static final String MARK_PROFIL_LIST = "profil_list";
    private static final String MARK_PROFIL = "profil";
    private static final String MARK_LST_PROFIL_DAEMON = "lstIdProfil";
    private static final String MARK_FILE_LINK_URL = "urlFile";

    private static final String JSP_MANAGE_PROFILS = "jsp/admin/plugins/identityexport/ManageProfils.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_PROFIL = "identityexport.message.confirmRemoveProfil";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "identityexport.model.entity.profil.attribute.";

    // Views
    private static final String VIEW_MANAGE_PROFILS = "manageProfils";
    private static final String VIEW_CREATE_PROFIL = "createProfil";
    private static final String VIEW_MODIFY_PROFIL = "modifyProfil";

    // Actions
    private static final String ACTION_CREATE_PROFIL = "createProfil";
    private static final String ACTION_MODIFY_PROFIL = "modifyProfil";
    private static final String ACTION_REMOVE_PROFIL = "removeProfil";
    private static final String ACTION_CONFIRM_REMOVE_PROFIL = "confirmRemoveProfil";
    private static final String ACTION_EXTRACTION_PROFIL_DATA = "extractDataCSV";

    // Infos
    private static final String INFO_PROFIL_CREATED = "identityexport.info.profil.created";
    private static final String INFO_PROFIL_UPDATED = "identityexport.info.profil.updated";
    private static final String INFO_PROFIL_REMOVED = "identityexport.info.profil.removed";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    // Session variable to store working values
    private Profile _profil;
    private List<Integer> _listIdProfils;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_PROFILS, defaultView = true )
    public String getManageProfils( HttpServletRequest request )
    {
        _profil = null;
        
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null || _listIdProfils.isEmpty( ) )
        {
        	_listIdProfils = ProfileHome.getIdProfilsList(  );
        }
       
        IFileStoreServiceProvider fileStoreService = FileService.getInstance().getFileStoreServiceProvider("localFileSystemDirectoryFileService");
        List<Profile> lstProfils = ProfileHome.getProfilsList( );
        Map<String, String> mapLinkBO = new HashMap<String, String>();
        for ( Profile pro : lstProfils )
        {
        	mapLinkBO.put( String.valueOf( pro.getId() ), fileStoreService.getFileDownloadUrlBO( pro.getFileName() + ".zip" ) );
        }
        
        
        Map<String, Object> model = getPaginatedListModel( request, MARK_PROFIL_LIST, _listIdProfils, JSP_MANAGE_PROFILS );
        model.put( MARK_LST_PROFIL_DAEMON, ExtractRequestHome.getIdExportRequestList() );
        model.put( MARK_FILE_LINK_URL, mapLinkBO );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_PROFILS, TEMPLATE_MANAGE_PROFILS, model );
    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<Profile> getItemsFromIds( List<Integer> listIds ) 
	{
		List<Profile> listProfil = ProfileHome.getProfilsListByIds( listIds );
		
		// keep original order
        return listProfil.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getId())))
                 .collect(Collectors.toList());
	}
    
    /**
    * reset the _listIdProfils list
    */
    public void resetListId( )
    {
    	_listIdProfils = new ArrayList<>( );
    }

    /**
     * Returns the form to create a profil
     *
     * @param request The Http request
     * @return the html code of the profil form
     */
    @View( VIEW_CREATE_PROFIL )
    public String getCreateProfil( HttpServletRequest request )
    {
        _profil = ( _profil != null ) ? _profil : new Profile(  );
        
        ReferentialService ref = SpringContextService.getBean( "referential.identityService" );
        ReferenceList lstCertifLevel = new ReferenceList(); 
    	try {
    		
    		RequestAuthor author = new RequestAuthor( );
            author.setType( AuthorType.admin );
            author.setName( "TEST" );
			
			ProcessusSearchResponse processList = ref.getProcessList( AppPropertiesService.getProperty( Constants.PROPERTY_CODE_CLIENT ), author);
			List<AttributeCertificationProcessusDto> processusCertif = processList.getProcessus();
			for ( AttributeCertificationProcessusDto attrCertif : processusCertif )
			{
				System.out.println("Processus : " + attrCertif.getCode( ) + ", label : " + attrCertif.getLabel( ) );
				lstCertifLevel.addItem( attrCertif.getCode(), attrCertif.getLabel() );
				for ( AttributeCertificationLevelDto attrLevel : attrCertif.getAttributeCertificationLevels() )
				{
					System.out.println("Attr du certif : " + attrLevel.getAttributeKey() + " , level" + attrLevel.getLevel().getName() + "," + attrLevel.getLevel().getLevel() );
				}
			}
			
			
		} catch (IdentityStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Map<String, Object> model = getModel(  );
        model.put( MARK_PROFIL, _profil );
        model.put( "lstCertif" , lstCertifLevel);
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_PROFIL ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_PROFIL, TEMPLATE_CREATE_PROFIL, model );
    }

    /**
     * Process the data capture form of a new profil
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_PROFIL )
    public String doCreateProfil( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _profil, request, getLocale( ) );
        

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_PROFIL ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _profil, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_PROFIL );
        }

        ProfileHome.create( _profil );
        addInfo( INFO_PROFIL_CREATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_PROFILS );
    }

    /**
     * Manages the removal form of a profil whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_PROFIL )
    public String getConfirmRemoveProfil( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROFIL ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_PROFIL ) );
        url.addParameter( PARAMETER_ID_PROFIL, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_PROFIL, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a profil
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage profils
     */
    @Action( ACTION_REMOVE_PROFIL )
    public String doRemoveProfil( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROFIL ) );
        
        
        ProfileHome.remove( nId );
        ExportAttributeHome.removeFromProfil( nId );
        addInfo( INFO_PROFIL_REMOVED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_PROFILS );
    }

    /**
     * Returns the form to update info about a profil
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_PROFIL )
    public String getModifyProfil( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROFIL ) );

        if ( _profil == null || ( _profil.getId(  ) != nId ) )
        {
            Optional<Profile> optProfil = ProfileHome.findByPrimaryKey( nId );
            _profil = optProfil.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }

        ReferentialService ref = SpringContextService.getBean( "referential.identityService" );
        ReferenceList lstCertifLevel = new ReferenceList(); 
    	try {
    		
    		RequestAuthor author = new RequestAuthor( );
            author.setType( AuthorType.admin );
            author.setName( "TEST" );
			
			ProcessusSearchResponse processList = ref.getProcessList( AppPropertiesService.getProperty( Constants.PROPERTY_CODE_CLIENT ), author);
			List<AttributeCertificationProcessusDto> processusCertif = processList.getProcessus();
			for ( AttributeCertificationProcessusDto attrCertif : processusCertif )
			{
				System.out.println("Processus : " + attrCertif.getCode( ) + ", label : " + attrCertif.getLabel( ) );
				lstCertifLevel.addItem( attrCertif.getCode(), attrCertif.getLabel() );
			}
			
		} catch (IdentityStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Map<String, Object> model = getModel(  );
        model.put( MARK_PROFIL, _profil );
        model.put( "lstCertif" , lstCertifLevel);
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_PROFIL ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_PROFIL, TEMPLATE_MODIFY_PROFIL, model );
    }

    /**
     * Process the change form of a profil
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_PROFIL )
    public String doModifyProfil( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _profil, request, getLocale( ) );
		
		
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_PROFIL ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _profil, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_PROFIL, PARAMETER_ID_PROFIL, _profil.getId( ) );
        }

        ProfileHome.update( _profil );
        addInfo( INFO_PROFIL_UPDATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_PROFILS );
    }
    
    
    @Action( ACTION_EXTRACTION_PROFIL_DATA )
    public String doExportFile( HttpServletRequest request )
    {
    	int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROFIL ) );

        Optional<ExportRequest> extractStore = ExtractRequestHome.findByPrimaryKey( nId );
        if ( !extractStore.isEmpty() )
        {
        	addError( "Il y a déjà un extract en cours sur ce profil" );
        	return redirectView( request, VIEW_MANAGE_PROFILS);
        }
    	
    	
    	Optional<Profile> optProfil = ProfileHome.findByPrimaryKey( nId );
        Profile profilExtract = optProfil.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
    	
    	
    	ExportRequest extract = new ExportRequest();
    	extract.setIdProfil( profilExtract.getId( ) );
    	ExtractRequestHome.create( extract );
    	
    	//ExtractDaemon exDaemon = new ExtractDaemon( lstFieldsExtract, profilExtract.getCertification( ) );
    	//exDaemon.run();
    	
    	//ExtractDaemon.setCertifLevel( profilExtract.getCertification( ) );
    	//ExtractDaemon.setFields( lstFieldsExtract );
    	
    	/*
    	String resultElastic = ElasticService.selectElasticField( lstFieldsExtract, profilExtract.getCertification( ) );
    	
    	if ( !resultElastic.isEmpty( ) )
    	{
	    	ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
	        try {
				ElasticsearchResponseJSON response = objectMapper.readValue(resultElastic, ElasticsearchResponseJSON.class);
				String strScrollID = response.get_scroll_id( );
				
				System.out.println("SCROLLID : " + response.get_scroll_id() );
				//System.out.println("Attributes : " + response.getHits().getHits().get(0).get_source().getAttributes().get(0) );
				List<Hit> lstHits = response.getHits().getHits();
				//System.out.println("Attributes : " + lstHits.get(0).get_source().getAttributes() );
				
				StringBuilder strAllContent = new StringBuilder();
				for ( Hit hit : lstHits)
				{
					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			        String json = ow.writeValueAsString( hit.get_source().getAttributes() ); ;
			        
			        Map<String,Object> result = objectMapper.readValue(json, HashMap.class);
			        //Object resultLogin = result.get("login");
			        
			        StringJoiner joinerFieldValues = new StringJoiner( AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_SEPARATOR )  );
			        StringJoiner joinerCertifValue = new StringJoiner( AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_SEPARATOR ) );
			        if (result != null)
			        {
				        for ( String fieldRequest : lstFieldsExtract )
					    {	
				        	Map<String,String> resultLogin = (Map<String,String>) result.get( fieldRequest );
				        	if ( resultLogin != null )
				        	{
					        	joinerFieldValues.add( resultLogin.get("value") );
					        	joinerCertifValue.add( resultLogin.get("certifierCode") );
				        	}
					    }
			        }
			        strAllContent.append( joinerFieldValues.toString( ) )
			        .append( AppPropertiesService.getProperty( Constants.PROPERTY_ELASTIC_ACCOUNT_SEPARATOR ) )
			        .append( joinerCertifValue.toString( ) )
			        .append( System.getProperty("line.separator") );
				}
				
				while ( strScrollID != null && !strScrollID.isEmpty( ) )
				{
					String resultElasticScroll = ElasticService.selectElasticFieldScroll( strScrollID );
					
					if ( !resultElasticScroll.isEmpty( ) )
			    	{
						ObjectMapper objectMapperScroll = new ObjectMapper();
						objectMapperScroll.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
						ElasticsearchResponseJSON responseScroll = objectMapper.readValue(resultElasticScroll, ElasticsearchResponseJSON.class);
						if ( !responseScroll.getHits().getHits().isEmpty( ) )
						{
							strScrollID = responseScroll.get_scroll_id( );
							String strContentScroll = getResultElasticScroll(resultElasticScroll, lstFieldsExtract, strScrollID);
					    	strAllContent.append(strContentScroll);
						}
						else
							strScrollID = StringUtils.EMPTY;
						
				    }
					else {
						strScrollID = StringUtils.EMPTY;
					}
				}
				
		        
		        StringJoiner joinerHeaders = new StringJoiner(",");
				 
			    for ( String fieldRequest : lstFieldsExtract )
			    {	
			    	joinerHeaders.add(fieldRequest);
			    }
			    String joinedHeadersString = joinerHeaders.toString();
		        
		        
		        
		        
		        TemporaryCSV tmp = new TemporaryCSV( );
		        tmp.setHeaders( joinedHeadersString );
		        //tmp.setContent( joinerFieldValues.toString( ) );
		        tmp.setContent( strAllContent.toString( ) );
		    	
		    	TemporaryFileGeneratorService.getInstance( ).generateFile(tmp, getUser());
		    	
		    	//ExportCSV.exportCSV( joinedHeadersString, strAllContent.toString( ) );
		    	
		    	
		    	
	    	
	        } catch (JsonProcessingException e) {
				AppLogService.error( e.getMessage(  ), e );
			}
    	}
    	*/
    	
    	return redirectView( request, VIEW_MANAGE_PROFILS);
    	
    }
}
