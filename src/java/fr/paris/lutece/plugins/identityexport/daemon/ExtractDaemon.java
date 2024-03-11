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
			for ( ExportRequest extra : lstExtractDaemonsList)
			{				
				ExtractRequestHome.remove( extra.getIdProfil( ) );
				AppLogService.debug( "Trying to generate export of profile id : " + extra.getId( ) );
			}
			
			for ( ExportRequest extra : lstExtractDaemonsList)
			{				
				ExportService.generateExport( extra.getIdProfil( ) );
			}
			
		}
	
	}
	

}
