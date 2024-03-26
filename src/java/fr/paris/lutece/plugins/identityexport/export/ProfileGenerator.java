package fr.paris.lutece.plugins.identityexport.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import fr.paris.lutece.plugins.identityexport.business.Profile;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.util.AppLogService;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;

public class ProfileGenerator extends Profile {

	private static final long serialVersionUID = 1L;
	private static String _strTmpDir = System.getProperty("java.io.tmpdir") + "/";

	private BufferedWriter _writer ;
	private Profile _profile;

	/**
	 * constructor 
	 */
	public ProfileGenerator( Profile profile ) 
	{
		_profile = profile;
	}

	/**
	 * init
	 * @throws IOException 
	 */
	private void init( ) throws IOException 
	{
		_writer = new BufferedWriter( new FileWriter( _strTmpDir + getFileName( ) + ".csv" ) );
	}

	/**
	 * add Line
	 * 
	 * @param content
	 * @throws IOException 
	 */
	public void addContent( String strContent ) throws IOException
	{
		if ( _writer == null )
		{
			init( );
		}

		_writer.append( strContent );			

	}

	/**
	 * zip file
	 * 
	 * @param strFileName
	 * @param strPassword
	 * @throws IOException 
	 */
	public File finalizeAndGenerateZipFile( ) throws IOException
	{
		_writer.close();

		ZipParameters zipParameters = new ZipParameters( );
		zipParameters.setEncryptFiles(true);
		zipParameters.setCompressionLevel(CompressionLevel.NORMAL );
		zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);

		ZipFile zipFile = new ZipFile( _strTmpDir + getFileName( ) + ".zip", getPassword( ).toCharArray( ) );
		java.io.File fileCSV = new java.io.File( _strTmpDir + getFileName( ) + ".csv" );

		zipFile.addFile( fileCSV, zipParameters);
		zipFile.close( );

		File luteceFile = getLuteceFile( zipFile.getFile( ) );

		// delete tmp files
		fileCSV.delete( );
		zipFile.getFile( ).delete( );

		return luteceFile;
	}


	/**
	 * get lutece file from file
	 * 
	 * @param resource
	 * @return the luteceFile
	 * @throws IOException 
	 */
	private File getLuteceFile( java.io.File resource ) throws IOException
	{
		// check if the file exists 
		if ( !resource.exists( ) )
		{
			return null;
		}

		File file = new File( );
		file.setFileKey( resource.getName( ) );
		file.setTitle( resource.getName( ) );
		file.setSize( (int) resource.length( ) );

		file.setMimeType( Files.probeContentType( resource.toPath( ) ) );

		PhysicalFile physicalFile = new PhysicalFile( );
		physicalFile.setValue( Files.readAllBytes( resource.toPath( ) ) );
		file.setPhysicalFile( physicalFile );


		return file;
	}


	// *** Decorator methods ***

	/**
	 * Returns the Id
	 * @return The Id
	 */
	public int getId( )
	{
		return _profile.getId();
	}

	/**
	 * Returns the Name
	 * @return The Name
	 */
	public String getName( )
	{
		return _profile.getName();
	}

	/**
	 * Returns the Certification
	 * @return The Certification
	 */
	public String getCertification( )
	{
		return _profile.getCertification( );
	}    

	/**
	 * Returns the FileName
	 * @return The FileName
	 */
	public String getFileName( )
	{
		return _profile.getFileName( );
	}

	/**
	 * 
	 */
	public boolean isAutoExtract() 
	{
		return _profile.isAutoExtract( );
	}

	/**
	 * 
	 */
	public String getPassword() 
	{
		return _profile.getPassword();
	}

}
