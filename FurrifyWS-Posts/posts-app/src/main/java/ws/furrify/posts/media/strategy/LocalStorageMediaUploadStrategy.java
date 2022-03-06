package ws.furrify.posts.media.strategy;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;
import ws.furrify.shared.exception.FileUploadCannotCreatePathException;
import ws.furrify.shared.exception.FileUploadFailedException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Upload media file to local storage strategy.
 * Needs to be created as bean for @Value to work.
 *
 * @author sky
 */
@RequiredArgsConstructor
public class LocalStorageMediaUploadStrategy implements MediaUploadStrategy {

    @Value("${LOCAL_STORAGE_MEDIA_PATH:/data/media}")
    private String LOCAL_STORAGE_MEDIA_PATH;

    @Value("${REMOTE_STORAGE_MEDIA_PATH:/media}")
    private String REMOTE_STORAGE_MEDIA_PATH;

    @Value("${THUMBNAIL_WIDTH:800}")
    private int THUMBNAIL_WIDTH;

    @Value("${THUMBNAIL_QUALITY:0.90}")
    private float THUMBNAIL_QUALITY;

    @Value("${THUMBNAIL_PREFIX:thumbnail_}")
    private String THUMBNAIL_PREFIX;

    private final static String THUMBNAIL_EXTENSION = ".jpg";

    @Override
    public UploadedMediaFile uploadMediaWithGeneratedThumbnail(final UUID mediaId,
                                                               final MediaExtension extension,
                                                               final MultipartFile fileSource) {

        try (
                // Generate thumbnail
                InputStream thumbnailInputStream = MediaUploadStrategyUtils.generateThumbnail(
                        extension,
                        THUMBNAIL_WIDTH,
                        THUMBNAIL_QUALITY,
                        fileSource.getInputStream()
                );

                InputStream mediaInputStream = fileSource.getInputStream()
        ) {

            return uploadFiles(
                    mediaId,
                    fileSource.getOriginalFilename(),
                    mediaInputStream,
                    thumbnailInputStream
            );

        } catch (IOException | URISyntaxException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }
    }

    @Override
    public UploadedMediaFile uploadMedia(final UUID mediaId,
                                         final MediaExtension extension,
                                         final MultipartFile fileSource,
                                         final MultipartFile thumbnailSource) {
        try (
                InputStream thumbnailInputStream = thumbnailSource.getInputStream();

                InputStream mediaInputStream = fileSource.getInputStream()
        ) {
            return uploadFiles(
                    mediaId,
                    fileSource.getOriginalFilename(),
                    mediaInputStream,
                    thumbnailInputStream
            );

        } catch (IOException | URISyntaxException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }
    }

    private void writeToFile(File file, InputStream inputStream) {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new FileUploadFailedException(Errors.FILE_UPLOAD_FAILED.getErrorMessage());
        }
    }

    private UploadedMediaFile uploadFiles(
            final UUID mediaId,
            final String originalFilename,
            final InputStream mediaInputStream,
            final InputStream thumbnailInputStream
    ) throws URISyntaxException {

        // Check if filename is not null
        if (originalFilename == null) {
            throw new IllegalStateException("Filename cannot be empty.");
        }

        // Sanitize filename
        String filename = originalFilename.replaceAll("\\s+","_");

        // Create files
        File mediaFile = new File(LOCAL_STORAGE_MEDIA_PATH + "/" + mediaId + "/" + filename);
        // Create directories where file need to be located
        boolean wasMediaFileCreated = mediaFile.getParentFile().mkdirs() || mediaFile.getParentFile().exists();

        if (!wasMediaFileCreated) {
            throw new FileUploadCannotCreatePathException(Errors.FILE_UPLOAD_CANNOT_CREATE_PATH.getErrorMessage());
        }

        // Upload file
        writeToFile(mediaFile, mediaInputStream);

        URI fileUri = new URI(REMOTE_STORAGE_MEDIA_PATH + "/" + mediaId + "/" + filename);

        URI thumbnailUri = null;

        // If there is a thumbnail
        if (thumbnailInputStream != null) {
            // Create thumbnail filename by removing extension from original filename
            String thumbnailFileName = THUMBNAIL_PREFIX +
                    filename.substring(
                            0,
                            filename.lastIndexOf(".")
                    ) + THUMBNAIL_EXTENSION;

            File thumbnailFile = new File(LOCAL_STORAGE_MEDIA_PATH + "/" + mediaId + "/" + thumbnailFileName);
            // Create directories where file need to be located
            boolean wasMediaThumbnailFileCreated = thumbnailFile.getParentFile().mkdirs() || mediaFile.getParentFile().exists();

            if (!wasMediaThumbnailFileCreated) {
                throw new FileUploadCannotCreatePathException(Errors.FILE_UPLOAD_CANNOT_CREATE_PATH.getErrorMessage());
            }

            // Upload file
            writeToFile(thumbnailFile, thumbnailInputStream);

            thumbnailUri = new URI(REMOTE_STORAGE_MEDIA_PATH + "/" + mediaId + "/" + thumbnailFileName);
        }

        // Return created urls
        return new UploadedMediaFile(
                // Original
                fileUri,
                // Thumbnail
                thumbnailUri
        );
    }
}
