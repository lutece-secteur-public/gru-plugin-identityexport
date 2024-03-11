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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.identityexport.business.ExportAttribute;
import fr.paris.lutece.plugins.identityexport.business.ExportAttributeHome;
import fr.paris.lutece.plugins.identityexport.business.ProfileHome;
import fr.paris.lutece.plugins.identityexport.export.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeKeyDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AuthorType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.RequestAuthor;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.referentiel.AttributeSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.referentiel.LevelDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.referentiel.LevelSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.service.ReferentialService;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Extraction features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageExtractions.jsp", controllerPath = "jsp/admin/plugins/identityexport/", right = "IDENTITYEXPORT_MANAGEMENT" )
public class ExtractionJspBean extends AbstractManageExtractionJspBean <Integer, ExportAttribute>
{
    // Templates
    private static final String TEMPLATE_MANAGE_EXTRACTIONS = "/admin/plugins/identityexport/manage_extractions.html";
    private static final String TEMPLATE_CREATE_EXTRACTION = "/admin/plugins/identityexport/create_extraction.html";
    private static final String TEMPLATE_MODIFY_EXTRACTION = "/admin/plugins/identityexport/modify_extraction.html";

    // Parameters
    private static final String PARAMETER_ID_EXTRACTION = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_EXTRACTIONS = "identityexport.manage_extractions.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_EXTRACTION = "identityexport.modify_extraction.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_EXTRACTION = "identityexport.create_extraction.pageTitle";

    // Markers
    private static final String MARK_EXTRACTION_LIST = "extraction_list";
    private static final String MARK_EXTRACTION = "extraction";

    private static final String JSP_MANAGE_EXTRACTIONS = "jsp/admin/plugins/identityexport/ManageExtractions.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_EXTRACTION = "identityexport.message.confirmRemoveExtraction";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "identityexport.model.entity.extraction.attribute.";

    // Views
    private static final String VIEW_MANAGE_EXTRACTIONS = "manageExtractions";
    private static final String VIEW_CREATE_EXTRACTION = "createExtraction";
    private static final String VIEW_MODIFY_EXTRACTION = "modifyExtraction";

    // Actions
    private static final String ACTION_CREATE_EXTRACTION = "createExtraction";
    private static final String ACTION_MODIFY_EXTRACTION = "modifyExtraction";
    private static final String ACTION_REMOVE_EXTRACTION = "removeExtraction";
    private static final String ACTION_CONFIRM_REMOVE_EXTRACTION = "confirmRemoveExtraction";
    private static final String ACTION_EXTRACTION_DATA = "extractDataCSV";

    // Infos
    private static final String INFO_EXTRACTION_CREATED = "identityexport.info.extraction.created";
    private static final String INFO_EXTRACTION_UPDATED = "identityexport.info.extraction.updated";
    private static final String INFO_EXTRACTION_REMOVED = "identityexport.info.extraction.removed";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    // Session variable to store working values
    private ExportAttribute _extraction;
    private List<Integer> _listIdExtractions;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_EXTRACTIONS, defaultView = true )
    public String getManageExtractions( HttpServletRequest request )
    {
        _extraction = null;
        
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null || _listIdExtractions.isEmpty( ) )
        {
        	_listIdExtractions = ExportAttributeHome.getIdExportAttributeList(  );
        }
        
        ReferenceList profilsReferenceList = ProfileHome.getProfilsReferenceList();
        
        Map<String, Object> model = getPaginatedListModel( request, MARK_EXTRACTION_LIST, _listIdExtractions, JSP_MANAGE_EXTRACTIONS );
        model.put("lstProfils", profilsReferenceList.toMap( ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_EXTRACTIONS, TEMPLATE_MANAGE_EXTRACTIONS, model );
    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<ExportAttribute> getItemsFromIds( List<Integer> listIds ) 
	{
		List<ExportAttribute> listExtraction = ExportAttributeHome.getExportAttributeListByIds( listIds );
		
		// keep original order
        return listExtraction.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getId())))
                 .collect(Collectors.toList());
	}
    
    /**
    * reset the _listIdExtractions list
    */
    public void resetListId( )
    {
    	_listIdExtractions = new ArrayList<>( );
    }

    /**
     * Returns the form to create a extraction
     *
     * @param request The Http request
     * @return the html code of the extraction form
     */
    @View( VIEW_CREATE_EXTRACTION )
    public String getCreateExtraction( HttpServletRequest request )
    {
        _extraction = ( _extraction != null ) ? _extraction : new ExportAttribute(  );
        
        ReferentialService ref = SpringContextService.getBean( "referential.identityService" );
        ReferenceList lstAttributes = new ReferenceList();
        ReferenceList lstCertifLevel = new ReferenceList(); 
        ReferenceList lstProfils = ProfileHome.getProfilsReferenceList( );
    	try {
    		
    		RequestAuthor author = new RequestAuthor( );
            author.setType( AuthorType.admin );
            author.setName( "TEST" );
            AttributeSearchResponse attributeKeyList = ref.getAttributeKeyList( AppPropertiesService.getProperty( Constants.PROPERTY_CODE_CLIENT ), author);
            List<AttributeKeyDto> attributeKeys = attributeKeyList.getAttributeKeys();
            
            for (AttributeKeyDto attr : attributeKeys)
			{
				System.out.println("Attributes : " + attr.getKeyName()  + ", name : " +  attr.getName( ) + " weigh : " + attr.getKeyWeight( ) );
				lstAttributes.addItem( attr.getKeyName(), attr.getName( ));
				
			}
            
			LevelSearchResponse levelList = ref.getLevelList( AppPropertiesService.getProperty( Constants.PROPERTY_CODE_CLIENT ), author);
			List<LevelDto> levels = levelList.getLevels();
			for (LevelDto level : levels)
			{
				System.out.println("level : " + level.getLevel() + ", name : " + level.getName() + ", " + level.getDescription( ));
				lstCertifLevel.addItem( level.getLevel(), level.getName() );
			}
		} catch (IdentityStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Map<String, Object> model = getModel(  );
        model.put( MARK_EXTRACTION, _extraction );
        model.put( "lstAttributes" , lstAttributes);
        model.put( "lstCertif" , lstCertifLevel);
        model.put( "lstProfils", lstProfils);
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_EXTRACTION ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_EXTRACTION, TEMPLATE_CREATE_EXTRACTION, model );
    }

    /**
     * Process the data capture form of a new extraction
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_EXTRACTION )
    public String doCreateExtraction( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _extraction, request, getLocale( ) );
        

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_EXTRACTION ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _extraction, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_EXTRACTION );
        }

        ExportAttributeHome.create( _extraction );
        addInfo( INFO_EXTRACTION_CREATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_EXTRACTIONS );
    }

    /**
     * Manages the removal form of a extraction whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_EXTRACTION )
    public String getConfirmRemoveExtraction( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_EXTRACTION ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_EXTRACTION ) );
        url.addParameter( PARAMETER_ID_EXTRACTION, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_EXTRACTION, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a extraction
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage extractions
     */
    @Action( ACTION_REMOVE_EXTRACTION )
    public String doRemoveExtraction( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_EXTRACTION ) );
        
        
        ExportAttributeHome.remove( nId );
        addInfo( INFO_EXTRACTION_REMOVED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_EXTRACTIONS );
    }

    /**
     * Returns the form to update info about a extraction
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_EXTRACTION )
    public String getModifyExtraction( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_EXTRACTION ) );

        if ( _extraction == null || ( _extraction.getId(  ) != nId ) )
        {
            Optional<ExportAttribute> optExtraction = ExportAttributeHome.findByPrimaryKey( nId );
            _extraction = optExtraction.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }

        ReferentialService ref = SpringContextService.getBean( "referential.identityService" );
        ReferenceList lstAttributes = new ReferenceList();
        ReferenceList lstCertifLevel = new ReferenceList(); 
        ReferenceList lstProfils = ProfileHome.getProfilsReferenceList( );
    	try {
    		
    		RequestAuthor author = new RequestAuthor( );
            author.setType( AuthorType.admin );
            author.setName( "TEST" );
            AttributeSearchResponse attributeKeyList = ref.getAttributeKeyList( AppPropertiesService.getProperty( Constants.PROPERTY_CODE_CLIENT ), author);
            List<AttributeKeyDto> attributeKeys = attributeKeyList.getAttributeKeys();
            
            for (AttributeKeyDto attr : attributeKeys)
			{
				lstAttributes.addItem( attr.getKeyName(), attr.getName( ));
				
			}
            
			LevelSearchResponse levelList = ref.getLevelList( AppPropertiesService.getProperty( Constants.PROPERTY_CODE_CLIENT ), author);
			List<LevelDto> levels = levelList.getLevels();
			for (LevelDto level : levels)
			{
				lstCertifLevel.addItem( level.getLevel(), level.getName() );
			}
		} catch (IdentityStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Map<String, Object> model = getModel(  );
        model.put( MARK_EXTRACTION, _extraction );
        model.put( "lstAttributes" , lstAttributes);
        model.put( "lstCertif" , lstCertifLevel);
        model.put( "lstProfils", lstProfils);
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_EXTRACTION ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_EXTRACTION, TEMPLATE_MODIFY_EXTRACTION, model );
    }

    /**
     * Process the change form of a extraction
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_EXTRACTION )
    public String doModifyExtraction( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _extraction, request, getLocale( ) );
		
		
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_EXTRACTION ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _extraction, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_EXTRACTION, PARAMETER_ID_EXTRACTION, _extraction.getId( ) );
        }

        ExportAttributeHome.update( _extraction );
        addInfo( INFO_EXTRACTION_UPDATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_EXTRACTIONS );
    }
   
}
