package fr.paris.lutece.plugins.identityexport.daemon;

import java.util.List;
import java.util.Optional;

import fr.paris.lutece.plugins.identityexport.business.ExportRequest;
import fr.paris.lutece.plugins.identityexport.business.ExtractRequestHome;
import fr.paris.lutece.plugins.identityexport.business.ProfileHome;
import fr.paris.lutece.portal.service.daemon.Daemon;

public class ExtractAutoDaemon extends Daemon {

	@Override
	public void run() {
		List<Integer> lstIdProfilsList = ProfileHome.getIdProfilsListAutoExtract();
		
		for (Integer nProfil : lstIdProfilsList )
		{
			Optional<ExportRequest> extractStore = ExtractRequestHome.findByPrimaryKey( nProfil );
			if ( extractStore.isEmpty( ) )
			{
				ExportRequest extract = new ExportRequest();
				extract.setIdProfil( nProfil );
				ExtractRequestHome.create( extract );
			}
		}
		
	}

}
