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

import java.io.Serializable;
/**
 * This is the business class for the object ExtractDaemon
 */ 
public class ExportRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String _strToken;    
    private int _nIdProfil;
    private String _strRecipientEmail;

    /**
     * Returns the Token
     * @return The Token
     */
    public String getToken( )
    {
        return _strToken;
    }

    /**
     * Sets the Token
     * @param  The Token
     */ 
    public void setToken( String strToken )
    {
        _strToken = strToken;
    }
    
    /**
     * Returns the IdProfil
     * @return The IdProfil
     */
    public int getIdProfil( )
    {
        return _nIdProfil;
    }

    /**
     * Sets the IdProfil
     * @param nIdProfil The IdProfil
     */ 
    public void setIdProfil( int nIdProfil )
    {
        _nIdProfil = nIdProfil;
    }

    /**
     * get recipient email
     * @return recipient email
     */
    public String getRecipientEmail() {
        return _strRecipientEmail;
    }

    /**
     * set recipient email
     * @param recipientEmail the recipient email
     */
    public void setRecipientEmail(final String recipientEmail) {
        this._strRecipientEmail = recipientEmail;
    }
}
