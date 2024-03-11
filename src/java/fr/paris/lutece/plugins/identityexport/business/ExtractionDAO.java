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
 * This class provides Data Access methods for Extraction objects
 */
public final class ExtractionDAO implements IExtractionDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_profile_attributes, attribute_key, id_profile FROM identityexport_profile_attributes WHERE id_profile_attributes = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO identityexport_profile_attributes ( attribute_key, id_profile ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM identityexport_profile_attributes WHERE id_profile_attributes = ? ";
    private static final String SQL_QUERY_DELETE_PROFIL = "DELETE FROM identityexport_profile_attributes WHERE id_profile = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE identityexport_profile_attributes SET attribute_key = ?, id_profile= ? WHERE id_profile_attributes = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_profile_attributes, attribute_key, id_profile FROM identityexport_profile_attributes";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_profile_attributes FROM identityexport_profile_attributes";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_profile_attributes, attribute_key, id_profile FROM identityexport_profile_attributes WHERE id_profile_attributes IN (  ";
    private static final String SQL_QUERY_SELECTALL_BY_ID_PROFIL = "SELECT id_profile_attributes, attribute_key, id_profile FROM identityexport_profile_attributes WHERE id_profile = ? ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( ExportAttribute extraction, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++ , extraction.getKey( ) );
            daoUtil.setInt( nIndex++, extraction.getIdProfil( ) );
            
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) ) 
            {
                extraction.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<ExportAttribute> load( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeQuery( );
	        ExportAttribute extraction = null;
	
	        if ( daoUtil.next( ) )
	        {
	            extraction = new ExportAttribute();
	            int nIndex = 1;
	            
	            extraction.setId( daoUtil.getInt( nIndex++ ) );
			    extraction.setKey( daoUtil.getString( nIndex++ ) );
			    extraction.setIdProfil( daoUtil.getInt( nIndex ) );
	        }
	
	        return Optional.ofNullable( extraction );
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
    public void deleteFromProfil( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_PROFIL, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( ExportAttribute extraction, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;
	        
            	daoUtil.setString( nIndex++ , extraction.getKey( ) );
            	daoUtil.setInt( nIndex++, extraction.getIdProfil( ) );
	        daoUtil.setInt( nIndex , extraction.getId( ) );
	
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ExportAttribute> selectExtractionsList( Plugin plugin )
    {
        List<ExportAttribute> extractionList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            ExportAttribute extraction = new ExportAttribute(  );
	            int nIndex = 1;
	            
	            extraction.setId( daoUtil.getInt( nIndex++ ) );
			    extraction.setKey( daoUtil.getString( nIndex++ ) );
			    extraction.setIdProfil( daoUtil.getInt( nIndex ) );
	
	            extractionList.add( extraction );
	        }
	
	        return extractionList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdExtractionsList( Plugin plugin )
    {
        List<Integer> extractionList = new ArrayList<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            extractionList.add( daoUtil.getInt( 1 ) );
	        }
	
	        return extractionList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectExtractionsReferenceList( Plugin plugin )
    {
        ReferenceList extractionList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            extractionList.addItem( daoUtil.getInt( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        return extractionList;
    	}
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	public List<ExportAttribute> selectExtractionsListByIds( Plugin plugin, List<Integer> listIds ) {
		List<ExportAttribute> extractionList = new ArrayList<>(  );
		
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
		        	ExportAttribute extraction = new ExportAttribute(  );
		            int nIndex = 1;
		            
		            extraction.setId( daoUtil.getInt( nIndex++ ) );
				    extraction.setKey( daoUtil.getString( nIndex++ ) );
				    extraction.setIdProfil( daoUtil.getInt( nIndex ) );
		            
		            extractionList.add( extraction );
		        }
		
		        daoUtil.free( );
		        
	        }
	    }
		return extractionList;
		
	}
	
	/**
     * {@inheritDoc }
     */
    @Override
    public List<ExportAttribute> selectExtractionsListByIdProfil( Plugin plugin, int nIdProfil )
    {
        List<ExportAttribute> extractionList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_ID_PROFIL, plugin ) )
        {
        	daoUtil.setInt( 1 , nIdProfil );
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            ExportAttribute extraction = new ExportAttribute(  );
	            int nIndex = 1;
	            
	            extraction.setId( daoUtil.getInt( nIndex++ ) );
			    extraction.setKey( daoUtil.getString( nIndex++ ) );
			    extraction.setIdProfil( daoUtil.getInt( nIndex ) );
	
	            extractionList.add( extraction );
	        }
	
	        return extractionList;
        }
    }
}
