package fr.paris.lutece.plugins.identityexport.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;

public class ExportFileService {
	
    private static BufferedWriter writer;
    
    
    public static BufferedWriter getFileWriter( )
    {
    	return writer;
    }
    
    public static void newFileCSV( String strFileName, String strHeaders, String strTmpDir )
    {
    	try {
    		//writer = new BufferedWriter(new FileWriter( AppPropertiesService.getProperty( Constants.PROPERTY_EXPORT_DIR_PATH ) + strFileName + ".csv" ));
    		writer = new BufferedWriter(new FileWriter( strTmpDir + "/" + strFileName + ".csv" ));
			writer.write(strHeaders);
		} catch (IOException e) {
			AppLogService.error(e.getMessage(), e);
		}
    }
	
	/**
	 * zip file
	 * 
	 * @param strFileName
	 * @param strPassword
	 */
	public static void zipFile(String strFileName, String strPassword, String strTmpDir )
	{
		try 
		{
			//String strPath = AppPropertiesService.getProperty( Constants.PROPERTY_EXPORT_DIR_PATH );
			String strPath = strTmpDir + "/";
			
 		    ZipParameters zipParameters = new ZipParameters( );
		    zipParameters.setEncryptFiles(true);
		    zipParameters.setCompressionLevel(CompressionLevel.NORMAL );
		    zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);


		    ZipFile zipFile = new ZipFile( strPath + strFileName + ".zip", strPassword.toCharArray());
		    File fileCSV = new File( strPath + strFileName + ".csv" );
		    zipFile.addFile( fileCSV, zipParameters);
		    zipFile.close();
		    fileCSV.delete();
		} catch (IOException e) 
		{			
			AppLogService.error(e.getMessage(), e);
		}
	    
	}
	
	public static OutputStream  getOutputSteam( String fileName ) {
		return new OutputStream() {
			
			@Override
			public void write(int arg0) throws IOException {
				// TODO Auto-generated method stub
				
			}
			
			// writer = new FileWriter( strFilePath,  StandardCharsets.UTF_8);  ??
		};
	}
	
	public static void close ( OutputStream out ) 
	{
		// finalize file
		//     writer.close(); ??
		try {
			writer.close();
		} catch (IOException e) {
			AppLogService.error(e.getMessage(), e);
		}
	}
	
	public static void close ( ) 
	{
		// finalize file
		//     writer.close(); ??
		try {
			writer.close();
		} catch (IOException e) {
			AppLogService.error(e.getMessage(), e);
		}
	}
	
	
	public static void addContentToFile( OutputStream out, String content )
	{
		
		// append content
	}
	
	public static void addContentToFile( String content )
	{
		try {
			writer.append( System.lineSeparator() );
			writer.append( content);
		} catch (IOException e) {
			AppLogService.error(e.getMessage(), e);
		}
	}

}
