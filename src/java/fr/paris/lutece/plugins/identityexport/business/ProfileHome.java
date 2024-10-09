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
 * This class provides instances management methods (create, find, ...) for Profil objects
 */
public final class ProfileHome
{
    // Static variable pointed at the DAO instance
    private static IProfilDAO _dao = SpringContextService.getBean( "identityexport.profilDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "identityexport" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ProfileHome(  )
    {
    }

    /**
     * Create an instance of the profil class
     * @param profil The instance of the Profil which contains the informations to store
     * @return The  instance of profil which has been created with its primary key.
     */
    public static Profile create( Profile profil )
    {
        _dao.insert( profil, _plugin );

        return profil;
    }

    /**
     * Update of the profil which is specified in parameter
     * @param profil The instance of the Profil which contains the data to store
     * @return The instance of the  profil which has been updated
     */
    public static Profile update( Profile profil )
    {
        _dao.store( profil, _plugin );

        return profil;
    }

    /**
     * Remove the profil whose identifier is specified in parameter
     * @param nKey The profil Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a profil whose identifier is specified in parameter
     * @param nKey The profil primary key
     * @return an instance of Profil
     */
    public static Optional<Profile> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the profil objects and returns them as a list
     * @return the list which contains the data of all the profil objects
     */
    public static List<Profile> getProfilsList( )
    {
        return _dao.selectProfilsList( _plugin );
    }
    
    /**
     * Load the id of all the profil objects and returns them as a list
     * @return the list which contains the id of all the profil objects
     */
    public static List<Integer> getIdProfilsList( )
    {
        return _dao.selectIdProfilsList( _plugin );
    }
    
    /**
     * Load the data of all the profil objects and returns them as a referenceList
     * @return the referenceList which contains the data of all the profil objects
     */
    public static ReferenceList getProfilsReferenceList( )
    {
        return _dao.selectProfilsReferenceList( _plugin );
    }
    
	
    /**
     * Load the data of all the avant objects and returns them as a list
     * @param listIds liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<Profile> getProfilsListByIds( List<Integer> listIds )
    {
        return _dao.selectProfilsListByIds( _plugin, listIds );
    }
    
    /**
     * Load the id of all the profil objects and returns them as a list
     * @return the list which contains the id of all the profil objects
     */
    public static List<Profile> getProfilsListAutoExtract( )
    {
        return _dao.selectProfilsListAutoExtract( _plugin );
    }

}

