package fr.paris.lutece.plugins.identityexport.service.file.implementation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.file.ExpiredLinkException;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileDownloadUrlService;
import fr.paris.lutece.portal.service.file.IFileRBACService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
*
* LocalFileSystemDirectoryFileService.
*
*/
public class LocalFileSystemDirectoryFileService implements IFileStoreServiceProvider
{
   private static final long serialVersionUID = 1L;

   private IFileDownloadUrlService _fileDownloadUrlService;
   private IFileRBACService _fileRBACService;
   private String _strName;
   private boolean _bDefault;
   private java.io.File _storageDir ;

   /**
    * init
    *
    * @param strPath the storage dir
    * @param fileDownloadUrlService
    * @param fileRBACService
    */
   public LocalFileSystemDirectoryFileService( String strPath, IFileDownloadUrlService fileDownloadUrlService, IFileRBACService fileRBACService )
   {
       this._storageDir = new java.io.File( strPath );
       this._fileDownloadUrlService = fileDownloadUrlService;
       this._fileRBACService = fileRBACService;
   }

   /**
    * get the FileRBACService
    *
    * @return the FileRBACService
    */
   public IFileRBACService getFileRBACService( )
   {
       return _fileRBACService;
   }

   /**
    * set the FileRBACService
    *
    * @param fileRBACService
    */
   public void setFileRBACService( IFileRBACService fileRBACService )
   {
       this._fileRBACService = fileRBACService;
   }

   /**
    * Get the downloadService
    *
    * @return the downloadService
    */
   public IFileDownloadUrlService getDownloadUrlService( )
   {
       return _fileDownloadUrlService;
   }

   /**
    * Sets the downloadService
    *
    * @param downloadUrlService
    *            downloadService
    */
   public void setDownloadUrlService( IFileDownloadUrlService downloadUrlService )
   {
       _fileDownloadUrlService = downloadUrlService;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName( )
   {
       return _strName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void delete( String strKey )
   {
       // get file
       java.io.File resource = new java.io.File( _storageDir, strKey );
       
       // check if the file exists
       if ( resource.exists( ) )
       {
           resource.delete( );
       }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public File getFile( String strKey )
   {
       return getFile( strKey, true );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public File getFileMetaData( String strKey )
   {
       return getFile( strKey, false );
   }

   /**
    * get file from OS dir
    *
    * @param strKey
    * @param withPhysicalFile
    *
    * @return the file with the physical file content if withPhysicalFile is true, null otherwise
    */
   public File getFile( String strKey, boolean withPhysicalFile )
   {
       if ( StringUtils.isBlank( strKey ) || strKey.contains("/") || strKey.contains("\\") || strKey.contains("..") )
       {
           return null;
       }

       // get file
       java.io.File resource = new java.io.File( _storageDir, strKey );
       
       // check if the file exists
       if ( !resource.exists( ) )
       {
           return null;
       }
       
       File file = new File( );
       file.setFileKey( strKey );
       file.setTitle( strKey );
       file.setOrigin( this.getName( ) );
       file.setSize( (int) resource.length( ) );
       
       try {
           file.setMimeType( Files.probeContentType( resource.toPath( ) ) );
       } catch (IOException e) {
           AppLogService.error( "unable to get MimeType of file", e);
       }
       
       
       if ( withPhysicalFile )
       {
           PhysicalFile physicalFile = new PhysicalFile( );
           try
           {
               physicalFile.setValue( Files.readAllBytes( resource.toPath( ) ) );
               file.setPhysicalFile( physicalFile );
           }
           catch (IOException e)
           {
               AppLogService.error(e);
           }
       }
       
       return file;

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String storeBytes( byte [ ] blob )
   {
       String randomFileName = UUID.randomUUID( ).toString( );
       java.io.File resource = new java.io.File( _storageDir, randomFileName );
       
       Path path = Paths.get( resource.getAbsolutePath( ) );

       try {
           Files.write( path, blob );
       } catch (IOException e) {
           AppLogService.error( e );
       }
      
       return String.valueOf( randomFileName );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String storeInputStream( InputStream inputStream )
   {
       return "method not implemented yet";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String storeFileItem( FileItem fileItem )
   {
       java.io.File file = new java.io.File( _storageDir, FilenameUtils.getName( fileItem.getName( ) ) );
       
       byte [ ] byteArray;
       try
       {
           byteArray = IOUtils.toByteArray( fileItem.getInputStream( ) );
       }
       catch( IOException ex )
       {
           throw new AppException( ex.getMessage( ), ex );
       }

       try {
           Files.write( Paths.get( file.getAbsolutePath( ) ), byteArray );
       } catch (IOException e) {
           AppLogService.error( e );
       }

       return fileItem.getName( );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String storeFile( File file )
   {
       java.io.File resource = new java.io.File( _storageDir, file.getTitle( ) );
       
       Path path = Paths.get( resource.getAbsolutePath( ) );

       try {
           Files.write( path, file.getPhysicalFile( ).getValue( ) );
       } catch (IOException e) {
           AppLogService.error( e );
       }
      
       return String.valueOf( file.getTitle( ) );
   }

   public void setDefault( boolean bDefault )
   {
       this._bDefault = bDefault;
   }

   public void setName( String strName )
   {
       _strName = strName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isDefault( )
   {
       return _bDefault;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public InputStream getInputStream( String strKey )
   {
       File file = getFile( strKey );

       return new ByteArrayInputStream( file.getPhysicalFile( ).getValue( ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getFileDownloadUrlFO( String strKey )
   {
       return _fileDownloadUrlService.getFileDownloadUrlFO( strKey, getName( ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getFileDownloadUrlFO( String strKey, Map<String, String> additionnalData )
   {
       return _fileDownloadUrlService.getFileDownloadUrlFO( strKey, additionnalData, getName( ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getFileDownloadUrlBO( String strKey )
   {
       return _fileDownloadUrlService.getFileDownloadUrlBO( strKey, getName( ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getFileDownloadUrlBO( String strKey, Map<String, String> additionnalData )
   {
       return _fileDownloadUrlService.getFileDownloadUrlBO( strKey, additionnalData, getName( ) );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void checkAccessRights( Map<String, String> fileData, User user ) throws AccessDeniedException, UserNotSignedException
   {
       if ( _fileRBACService != null )
       {
           _fileRBACService.checkAccessRights( fileData, user );
       }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void checkLinkValidity( Map<String, String> fileData ) throws ExpiredLinkException
   {
       _fileDownloadUrlService.checkLinkValidity( fileData );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public File getFileFromRequestBO( HttpServletRequest request ) throws AccessDeniedException, ExpiredLinkException, UserNotSignedException
   {
       Map<String, String> fileData = _fileDownloadUrlService.getRequestDataBO( request );

       // check access rights
       checkAccessRights( fileData, AdminAuthenticationService.getInstance( ).getRegisteredUser( request ) );

       // check validity
       checkLinkValidity( fileData );

       String strFileId = fileData.get( FileService.PARAMETER_FILE_ID );

       return getFile( strFileId );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public File getFileFromRequestFO( HttpServletRequest request ) throws AccessDeniedException, ExpiredLinkException, UserNotSignedException
   {

       Map<String, String> fileData = _fileDownloadUrlService.getRequestDataFO( request );

       // check access rights
       checkAccessRights( fileData, SecurityService.getInstance( ).getRegisteredUser( request ) );

       // check validity
       checkLinkValidity( fileData );

       String strFileId = fileData.get( FileService.PARAMETER_FILE_ID );

       return getFile( strFileId );
   }
}

