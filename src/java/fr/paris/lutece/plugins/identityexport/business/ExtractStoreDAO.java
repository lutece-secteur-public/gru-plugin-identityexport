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
 * This class provides Data Access methods for ExtractDaemon objects
 */
public final class ExtractStoreDAO implements IExtractStoreDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_profile FROM identityexport_daemon_stack WHERE id_profile = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO identityexport_daemon_stack ( id_profile ) VALUES ( ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM identityexport_daemon_stack WHERE id_profile = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE identityexport_daemon_stack SET id_profile = ? WHERE id_profile = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_profile FROM identityexport_daemon_stack";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_profile FROM identityexport_daemon_stack";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_profile FROM identityexport_daemon_stack WHERE id_profile IN (  ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( ExportRequest extractDaemon, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++ , extractDaemon.getIdProfil( ) );
            
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) ) 
            {
                extractDaemon.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<ExportRequest> load( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeQuery( );
	        ExportRequest extractDaemon = null;
	
	        if ( daoUtil.next( ) )
	        {
	            extractDaemon = new ExportRequest();
	            int nIndex = 1;
	            
			    extractDaemon.setIdProfil( daoUtil.getInt( nIndex ) );
	        }
	
	        return Optional.ofNullable( extractDaemon );
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
    public void store( ExportRequest extractDaemon, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;
	        
	        daoUtil.setInt( nIndex , extractDaemon.getId( ) );
	
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ExportRequest> selectExtractDaemonsList( Plugin plugin )
    {
        List<ExportRequest> extractDaemonList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            ExportRequest extractDaemon = new ExportRequest(  );
	            int nIndex = 1;
	            
			    extractDaemon.setIdProfil( daoUtil.getInt( nIndex ) );
	
	            extractDaemonList.add( extractDaemon );
	        }
	
	        return extractDaemonList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdExtractDaemonsList( Plugin plugin )
    {
        List<Integer> extractDaemonList = new ArrayList<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            extractDaemonList.add( daoUtil.getInt( 1 ) );
	        }
	
	        return extractDaemonList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectExtractDaemonsReferenceList( Plugin plugin )
    {
        ReferenceList extractDaemonList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            extractDaemonList.addItem( daoUtil.getInt( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        return extractDaemonList;
    	}
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	public List<ExportRequest> selectExtractDaemonsListByIds( Plugin plugin, List<Integer> listIds ) {
		List<ExportRequest> extractDaemonList = new ArrayList<>(  );
		
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
		        	ExportRequest extractDaemon = new ExportRequest(  );
		            int nIndex = 1;
		            
				    extractDaemon.setIdProfil( daoUtil.getInt( nIndex ) );
		            
		            extractDaemonList.add( extractDaemon );
		        }
		
		        daoUtil.free( );
		        
	        }
	    }
		return extractDaemonList;
		
	}
}
