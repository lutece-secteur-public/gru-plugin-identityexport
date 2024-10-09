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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides Data Access methods for Profil objects
 */
public final class ProfilDAO implements IProfilDAO
{
    // Constants
    private static final String ALL_COLUMNS = "id_profile, name, certifier_code, file_name, is_monparis, is_auto_extract, auto_extract_interval, password";

    private static final String SQL_QUERY_SELECT = "SELECT " + ALL_COLUMNS + " FROM identityexport_profile WHERE id_profile = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO identityexport_profile ( name, certifier_code, file_name, is_monparis, is_auto_extract, auto_extract_interval, password ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM identityexport_profile WHERE id_profile = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE identityexport_profile SET name = ?, certifier_code = ?, file_name = ?, is_monparis = ?, is_auto_extract = ?, auto_extract_interval = ?, password = ? WHERE id_profile = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT " + ALL_COLUMNS + " FROM identityexport_profile";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_profile FROM identityexport_profile";
    private static final String SQL_QUERY_SELECTALL_AUTO_EXTRACT = "SELECT " + ALL_COLUMNS + " FROM identityexport_profile WHERE is_auto_extract = 1 ";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT " + ALL_COLUMNS + " FROM identityexport_profile WHERE id_profile IN (  ";

    private Profile load(final DAOUtil daoUtil) {
        final Profile profil = new Profile();
        int nIndex = 1;

        profil.setId( daoUtil.getInt( nIndex++ ) );
        profil.setName( daoUtil.getString( nIndex++ ) );
        profil.setCertification( daoUtil.getString( nIndex++ ) );
        profil.setFileName( daoUtil.getString( nIndex++ ) );
        profil.setMonParis( daoUtil.getBoolean( nIndex++ ) );
        profil.setAutoExtract( daoUtil.getBoolean( nIndex++ ) );
        final int interval = daoUtil.getInt(nIndex++);
        profil.setAutoExtractInterval( interval > 0 ? interval : null );
        profil.setPassword( daoUtil.getString( nIndex ) );

        return profil;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Profile profil, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++ , profil.getName( ) );
            daoUtil.setString( nIndex++ , profil.getCertification( ) );
            daoUtil.setString( nIndex++ , profil.getFileName( ) );
            daoUtil.setBoolean( nIndex++, profil.isMonParis( ) );
            daoUtil.setBoolean( nIndex++, profil.isAutoExtract() );
            if (profil.getAutoExtractInterval() == null) {
                daoUtil.setIntNull(nIndex++);
            } else {
                daoUtil.setInt(nIndex++, profil.getAutoExtractInterval());
            }
            daoUtil.setString(nIndex, profil.getPassword());
            
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) ) 
            {
                profil.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Profile> load( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeQuery( );
	        if ( daoUtil.next( ) )
	        {
                return Optional.of( load( daoUtil ) );
	        }
	        return Optional.empty();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Profile profil, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;
	        
            daoUtil.setString( nIndex++ , profil.getName( ) );
            daoUtil.setString( nIndex++ , profil.getCertification( ) );
            daoUtil.setString( nIndex++ , profil.getFileName( ) );
            daoUtil.setBoolean( nIndex++, profil.isMonParis( ) );
            daoUtil.setBoolean( nIndex++, profil.isAutoExtract() );
            if (profil.getAutoExtractInterval() == null) {
                daoUtil.setIntNull(nIndex++);
            } else {
                daoUtil.setInt(nIndex++, profil.getAutoExtractInterval());
            }
            daoUtil.setString( nIndex++ , profil.getPassword( ) );
	        daoUtil.setInt( nIndex , profil.getId( ) );
	
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Profile> selectProfilsList( Plugin plugin )
    {
        List<Profile> profilList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            profilList.add( load( daoUtil ) );
	        }
	
	        return profilList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdProfilsList( Plugin plugin )
    {
        List<Integer> profilList = new ArrayList<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            profilList.add( daoUtil.getInt( 1 ) );
	        }
	
	        return profilList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectProfilsReferenceList( Plugin plugin )
    {
        ReferenceList profilList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            profilList.addItem( daoUtil.getInt( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        return profilList;
    	}
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	public List<Profile> selectProfilsListByIds( Plugin plugin, List<Integer> listIds ) {
		List<Profile> profilList = new ArrayList<>(  );
		
		StringBuilder builder = new StringBuilder( );

		if ( !listIds.isEmpty( ) )
		{
			for( int i = 0 ; i < listIds.size(); i++ ) {
			    builder.append( "?," );
			}
	
			String placeHolders =  builder.deleteCharAt( builder.length( ) -1 ).toString( );
			String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";
			
			
	        try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
	        {
	        	int index = 1;
				for( Integer n : listIds ) {
					daoUtil.setInt(  index++, n ); 
				}
	        	
	        	daoUtil.executeQuery(  );
	        	while ( daoUtil.next(  ) )
		        {
		            profilList.add( load( daoUtil ) );
		        }
		
		        daoUtil.free( );
		        
	        }
	    }
		return profilList;
		
	}
	
	/**
     * {@inheritDoc }
     */
    @Override
    public List<Profile> selectProfilsListAutoExtract( Plugin plugin )
    {
        List<Profile> profilList = new ArrayList<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_AUTO_EXTRACT, plugin ) )
        {
	        daoUtil.executeQuery(  );
	        while ( daoUtil.next(  ) )
	        {
	            profilList.add( load( daoUtil ) );
	        }
	
	        return profilList;
        }
    }
}
