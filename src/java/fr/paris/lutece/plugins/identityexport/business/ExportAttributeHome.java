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


 package fr.paris.lutece.plugins.identityexport.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;


import java.util.List;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for Extraction objects
 */
public final class ExportAttributeHome
{
    // Static variable pointed at the DAO instance
    private static IExtractionDAO _dao = SpringContextService.getBean( "identityexport.extractionDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "identityexport" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ExportAttributeHome(  )
    {
    }

    /**
     * Create an instance of the extraction class
     * @param extraction The instance of the Extraction which contains the informations to store
     * @return The  instance of extraction which has been created with its primary key.
     */
    public static ExportAttribute create( ExportAttribute extraction )
    {
        _dao.insert( extraction, _plugin );

        return extraction;
    }

    /**
     * Update of the extraction which is specified in parameter
     * @param extraction The instance of the Extraction which contains the data to store
     * @return The instance of the  extraction which has been updated
     */
    public static ExportAttribute update( ExportAttribute extraction )
    {
        _dao.store( extraction, _plugin );

        return extraction;
    }

    /**
     * Remove the extraction whose identifier is specified in parameter
     * @param nKey The extraction Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a extraction whose identifier is specified in parameter
     * @param nKey The extraction primary key
     * @return an instance of Extraction
     */
    public static Optional<ExportAttribute> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the extraction objects and returns them as a list
     * @return the list which contains the data of all the extraction objects
     */
    public static List<ExportAttribute> getExportAttributeList( )
    {
        return _dao.selectExtractionsList( _plugin );
    }
    
    /**
     * Load the id of all the extraction objects and returns them as a list
     * @return the list which contains the id of all the extraction objects
     */
    public static List<Integer> getIdExportAttributeList( )
    {
        return _dao.selectIdExtractionsList( _plugin );
    }
    
    /**
     * Load the data of all the extraction objects and returns them as a referenceList
     * @return the referenceList which contains the data of all the extraction objects
     */
    public static ReferenceList getExportAttributeReferenceList( )
    {
        return _dao.selectExtractionsReferenceList( _plugin );
    }
    
	
    /**
     * Load the data of all the avant objects and returns them as a list
     * @param listIds liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<ExportAttribute> getExportAttributeListByIds( List<Integer> listIds )
    {
        return _dao.selectExtractionsListByIds( _plugin, listIds );
    }
    
    /**
     * Load the data of all the extraction objects and returns them as a list
     * @return the list which contains the data of all the extraction objects
     */
    public static List<ExportAttribute> getExportAttributeListByIdProfil( int nIdProfil )
    {
        return _dao.selectExtractionsListByIdProfil( _plugin, nIdProfil );
    }
    
    /**
     * Remove the extraction whose identifier is specified in parameter
     * @param nKey The extraction Id
     */
    public static void removeFromProfil( int nKey )
    {
        _dao.deleteFromProfil( nKey, _plugin );
    }

}

