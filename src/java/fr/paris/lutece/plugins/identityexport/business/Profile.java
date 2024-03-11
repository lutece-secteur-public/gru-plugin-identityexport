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

import javax.validation.constraints.Size;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
/**
 * This is the business class for the object Profil
 */ 
public class Profile implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations 
    private int _nId;
    
    @NotEmpty( message = "#i18n{identityexport.validation.profil.Name.notEmpty}" )
    @Size( max = 255 , message = "#i18n{identityexport.validation.profil.Name.size}" ) 
    private String _strName;
    
    @NotEmpty( message = "#i18n{identityexport.validation.profil.Certification.notEmpty}" )
    @Size( max = 255 , message = "#i18n{identityexport.validation.profil.Certification.size}" ) 
    private String _strCertification;
    
    @Size( max = 255 , message = "#i18n{identityexport.validation.profil.FileName.size}" ) 
    private String _strFileName;
    
    private boolean _bMonParis;
    
    private boolean _bAutoExtract;
    
    private String _strPassword;

    /**
     * Returns the Id
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * @param nId The Id
     */ 
    public void setId( int nId )
    {
        _nId = nId;
    }
    
    /**
     * Returns the Name
     * @return The Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the Name
     * @param strName The Name
     */ 
    public void setName( String strName )
    {
        _strName = strName;
    }
    
    
    /**
     * Returns the Certification
     * @return The Certification
     */
    public String getCertification( )
    {
        return _strCertification;
    }

    /**
     * Sets the Certification
     * @param strCertification The Certification
     */ 
    public void setCertification( String strCertification )
    {
        _strCertification = strCertification;
    }
    
    
    /**
     * Returns the FileName
     * @return The FileName
     */
    public String getFileName( )
    {
        return _strFileName;
    }

    /**
     * Sets the FileName
     * @param strFileName The FileName
     */ 
    public void setFileName( String strFileName )
    {
        _strFileName = strFileName;
    }

	public boolean isMonParis() {
		return _bMonParis;
	}

	public void setMonParis(boolean bMonParis) {
		this._bMonParis = bMonParis;
	}

	public boolean isAutoExtract() {
		return _bAutoExtract;
	}

	public void setAutoExtract(boolean _bAutoExtract) {
		this._bAutoExtract = _bAutoExtract;
	}

	public String getPassword() {
		return _strPassword;
	}

	public void setPassword(String _strPassword) {
		this._strPassword = _strPassword;
	}
    
}
