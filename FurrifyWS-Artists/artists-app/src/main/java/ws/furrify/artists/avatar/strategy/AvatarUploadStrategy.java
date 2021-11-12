package ws.furrify.artists.avatar.strategy;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

/**
 * Strategy should implement way to upload the file to remote location.
 *
 * @author sky
 */
public interface AvatarUploadStrategy {

    UploadedAvatarFile uploadAvatarWithGeneratedThumbnail(final UUID artistId, final UUID avatarId, final MultipartFile fileSource);

    @Value
    class UploadedAvatarFile {
        URI fileUri;
        URI thumbnailUri;
    }

}
