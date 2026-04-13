package fr.paris.lutece.plugins.identityexport.daemon;

import java.util.List;

import fr.paris.lutece.plugins.identityexport.ExportService;
import fr.paris.lutece.plugins.identityexport.business.ExportRequest;
import fr.paris.lutece.plugins.identityexport.business.ExtractRequestHome;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.progressmanager.ProgressManagerService;
import fr.paris.lutece.portal.service.util.AppLogService;
import org.apache.commons.lang3.StringUtils;

public class ExtractDaemon extends Daemon {
	
	@Override
	public void run() {
		
		List<ExportRequest> lstExtractDaemonsList = ExtractRequestHome.getExportRequestList( );
		if ( lstExtractDaemonsList!= null && !lstExtractDaemonsList.isEmpty( ) )
		{
			StringBuffer sb = new StringBuffer();
			
			for ( ExportRequest extra : lstExtractDaemonsList)
			{				
				ExtractRequestHome.remove( extra.getIdProfil( ) );
				AppLogService.debug( "Trying to generate export of profile id : " + extra.getIdProfil( ) );
				sb.append(  "Trying to generate export of profile id : " + extra.getIdProfil( ) + "\n");
			}
			
			for ( ExportRequest extra : lstExtractDaemonsList)
			{
				final String strProgressToken = extra.getToken( );
				try
				{
					ExportService.generateExport( extra.getIdProfil( ), extra.getRecipientEmail( ), strProgressToken );
					sb.append( "Export generated of profile id : " + extra.getIdProfil( ) + "\n");
				}
				catch (Exception e)
				{
					setLastRunLogs( "Error : an exception occured while generating export  of profile id " + extra.getIdProfil( ) + " : " + e.getMessage());
					AppLogService.error( e.getMessage( ), e);
				}
				finally
				{
					if ( StringUtils.isNotBlank( strProgressToken ) )
					{
						ProgressManagerService.getInstance( ).unRegisterFeed( strProgressToken );
					}
				}
			}
			
			setLastRunLogs( sb.toString( ) );
		}
		
	
	}
	

}
