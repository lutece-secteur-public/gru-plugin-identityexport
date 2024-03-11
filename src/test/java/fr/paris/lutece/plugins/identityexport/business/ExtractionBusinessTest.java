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
 *"
 * License 1.0
 */

package fr.paris.lutece.plugins.identityexport.business;

import fr.paris.lutece.plugins.identityexport.business.ExportAttribute;
import fr.paris.lutece.plugins.identityexport.business.ExportAttributeHome;
import fr.paris.lutece.test.LuteceTestCase;

import java.util.Optional;


/**
 * This is the business class test for the object Extraction
 */
public class ExtractionBusinessTest extends LuteceTestCase
{
    private static final String FIELD1 = "Field1";
    private static final String FIELD2 = "Field2";

	/**
	* test Extraction
	*/
    public void testBusiness(  )
    {
        // Initialize an object
        ExportAttribute extraction = new ExportAttribute();
        extraction.setKey( FIELD1 );

        // Create test
        ExportAttributeHome.create( extraction );
        Optional<ExportAttribute> optExtractionStored = ExportAttributeHome.findByPrimaryKey( extraction.getId( ) );
        ExportAttribute extractionStored = optExtractionStored.orElse( new ExportAttribute ( ) );
        assertEquals( extractionStored.getKey( ) , extraction.getKey( ) );

        // Update test
        extraction.setKey( FIELD2 );
        ExportAttributeHome.update( extraction );
        optExtractionStored = ExportAttributeHome.findByPrimaryKey( extraction.getId( ) );
        extractionStored = optExtractionStored.orElse( new ExportAttribute ( ) );
        
        assertEquals( extractionStored.getKey( ) , extraction.getKey( ) );

        // List test
        ExportAttributeHome.getExportAttributeList( );

        // Delete test
        ExportAttributeHome.remove( extraction.getId( ) );
        optExtractionStored = ExportAttributeHome.findByPrimaryKey( extraction.getId( ) );
        extractionStored = optExtractionStored.orElse( null );
        assertNull( extractionStored );
        
    }
    
    
     

}