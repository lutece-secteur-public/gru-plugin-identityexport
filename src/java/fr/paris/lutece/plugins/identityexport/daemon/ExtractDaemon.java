package fr.paris.lutece.plugins.identityexport.daemon;

import java.util.List;

import fr.paris.lutece.plugins.identityexport.ExportService;
import fr.paris.lutece.plugins.identityexport.business.ExportRequest;
import fr.paris.lutece.plugins.identityexport.business.ExtractRequestHome;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.util.AppLogService;

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
				AppLogService.debug( "Trying to generate export of profile id : " + extra.getId( ) );
				sb.append(  "Trying to generate export of profile id : " + extra.getId( ) + "\n");
			}
			
			for ( ExportRequest extra : lstExtractDaemonsList)
			{		
				try 
				{
					ExportService.generateExport( extra.getIdProfil( ) );
					sb.append( "Export generated of profile id : " + extra.getId( ) + "\n");
				}
				catch (Exception e)
				{
					setLastRunLogs( "Error : an exception occured while generating export  of profile id " + extra.getId( ) + " : " + e.getMessage());
					AppLogService.error( e.getMessage( ), e);
				}
			}
			
			setLastRunLogs( sb.toString( ) );
		}
		
	
	}
	

}
