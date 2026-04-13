package fr.paris.lutece.plugins.identityexport.daemon;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import fr.paris.lutece.plugins.identityexport.business.ExportRequest;
import fr.paris.lutece.plugins.identityexport.business.ExtractRequestHome;
import fr.paris.lutece.plugins.identityexport.business.Profile;
import fr.paris.lutece.plugins.identityexport.business.ProfileHome;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.progressmanager.ProgressManagerService;

public class ExtractAutoDaemon extends Daemon {

    @Override
    public void run() {
        List<Profile> lstIdProfilsList = ProfileHome.getProfilsListAutoExtract();
        for (final Profile profil : lstIdProfilsList) {
            final Integer interval = profil.getAutoExtractInterval();
            if (interval != null) {
                final Timestamp lastExtractDate = profil.getLastExtractDate();
                if (lastExtractDate != null) {
                    if (lastExtractDate.toLocalDateTime().plusHours(interval).isAfter(LocalDateTime.now())) {
                        continue;
                    }
                }
            }
            Optional<ExportRequest> extractStore = ExtractRequestHome.findByPrimaryKey(profil.getId());
            if (extractStore.isEmpty()) {
                final String strProgressToken = ProgressManagerService.getInstance( ).registerFeed( "export-auto-profile-" + profil.getId(), 1 );
                ExportRequest extract = new ExportRequest();
                extract.setIdProfil(profil.getId());
                extract.setToken( strProgressToken );
                ExtractRequestHome.create(extract);
            }
        }

    }

}
