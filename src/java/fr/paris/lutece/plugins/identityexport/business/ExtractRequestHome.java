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
 * This class provides instances management methods (create, find, ...) for ExtractDaemon objects
 */
public final class ExtractRequestHome
{
    // Static variable pointed at the DAO instance
    private static IExtractStoreDAO _dao = SpringContextService.getBean( "identityexport.ExtractStoreDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "identityexport" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ExtractRequestHome(  )
    {
    }

    /**
     * Create an instance of the extractDaemon class
     * @param extractDaemon The instance of the ExtractDaemon which contains the informations to store
     * @return The  instance of extractDaemon which has been created with its primary key.
     */
    public static ExportRequest create( ExportRequest extractRequest )
    {
        _dao.insert( extractRequest, _plugin );

        return extractRequest;
    }

    /**
     * Update of the extractDaemon which is specified in parameter
     * @param extractDaemon The instance of the ExtractDaemon which contains the data to store
     * @return The instance of the  extractDaemon which has been updated
     */
    public static ExportRequest update( ExportRequest extractRequest )
    {
        _dao.store( extractRequest, _plugin );

        return extractRequest;
    }

    /**
     * Remove the extractDaemon whose identifier is specified in parameter
     * @param nKey The extractDaemon Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a extractDaemon whose identifier is specified in parameter
     * @param nKey The extractDaemon primary key
     * @return an instance of ExtractDaemon
     */
    public static Optional<ExportRequest> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the extractDaemon objects and returns them as a list
     * @return the list which contains the data of all the extractDaemon objects
     */
    public static List<ExportRequest> getExportRequestList( )
    {
        return _dao.selectExtractDaemonsList( _plugin );
    }
    
    /**
     * Load the id of all the extractDaemon objects and returns them as a list
     * @return the list which contains the id of all the extractDaemon objects
     */
    public static List<Integer> getIdExportRequestList( )
    {
        return _dao.selectIdExtractDaemonsList( _plugin );
    }
    
    /**
     * Load the data of all the extractDaemon objects and returns them as a referenceList
     * @return the referenceList which contains the data of all the extractDaemon objects
     */
    public static ReferenceList getExportRequestReferenceList( )
    {
        return _dao.selectExtractDaemonsReferenceList( _plugin );
    }
    
	
    /**
     * Load the data of all the avant objects and returns them as a list
     * @param listIds liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<ExportRequest> getExportRequestListByIds( List<Integer> listIds )
    {
        return _dao.selectExtractDaemonsListByIds( _plugin, listIds );
    }

}

