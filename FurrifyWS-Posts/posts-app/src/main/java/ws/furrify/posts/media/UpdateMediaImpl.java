package ws.furrify.posts.media;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.strategy.MediaUploadStrategy;
import ws.furrify.posts.media.vo.MediaFile;
import ws.furrify.posts.media.vo.MediaPriority;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
final class UpdateMediaImpl implements UpdateMedia {

    private final DomainEventPublisher<MediaEvent> domainEventPublisher;
    private final MediaRepository mediaRepository;
    private final MediaUploadStrategy mediaUploadStrategy;

    @Override
    public void updateMedia(@NonNull final UUID userId,
                            @NonNull final UUID postId,
                            @NonNull final UUID mediaId,
                            @NonNull final MediaDTO mediaDTO,
                            final MultipartFile mediaFile,
                            final MultipartFile thumbnailFile) {
        Media media = mediaRepository.findByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(mediaId.toString())));

        final MediaSnapshot mediaSnapshot = media.getSnapshot();

        // Update changed fields in media

        // If only thumbnail file present with no media file
        if (thumbnailFile != null && !thumbnailFile.isEmpty() && mediaFile == null) {
            FileUtils.validateThumbnail(
                    thumbnailFile
            );

            MediaUploadStrategy.UploadedMediaFile uploadedThumbnailFile = mediaUploadStrategy.uploadThumbnail(
                    mediaId,
                    mediaSnapshot.getFilename(),
                    thumbnailFile
            );

            media.replaceMediaFile(
                    MediaFile.builder()
                            .filename(mediaSnapshot.getFilename())
                            .fileUri(mediaSnapshot.getFileUri())
                            .thumbnailUri(uploadedThumbnailFile.getThumbnailUri())
                            .extension(mediaSnapshot.getExtension())
                            .md5(mediaSnapshot.getMd5())
                            .build()
            );
        }

        // If new media file provided with extension
        if (mediaFile != null && !mediaFile.isEmpty() && mediaDTO.getExtension() != null) {
            final String md5 = FileUtils.generateMd5FromFile(mediaFile);

            FileUtils.validateMedia(
                    userId,
                    postId,
                    mediaDTO,
                    mediaFile,
                    md5,
                    mediaRepository
            );

            MediaUploadStrategy.UploadedMediaFile uploadedMediaFile;

            // If thumbnail file is present
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                // Upload media with thumbnail
                uploadedMediaFile = mediaUploadStrategy.uploadMedia(
                        mediaId,
                        mediaDTO.getExtension(),
                        mediaFile,
                        thumbnailFile
                );
            } else {
                // Upload media with thumbnail
                uploadedMediaFile = mediaUploadStrategy.uploadMediaWithGeneratedThumbnail(
                        mediaId,
                        mediaDTO.getExtension(),
                        mediaFile
                );
            }

            media.replaceMediaFile(
                    MediaFile.builder()
                            .filename(Objects.requireNonNull(mediaFile.getOriginalFilename()))
                            .fileUri(uploadedMediaFile.getFileUri())
                            .thumbnailUri(uploadedMediaFile.getThumbnailUri())
                            .extension(mediaDTO.getExtension())
                            .md5(md5)
                            .build()
            );
        }

        if (mediaDTO.getPriority() != null) {
            media.replacePriority(
                    MediaPriority.of(mediaDTO.getPriority())
            );
        }

        // Publish update media event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.MEDIA,
                // User userId as key
                userId,
                MediaUtils.createMediaEvent(
                        DomainEventPublisher.MediaEventType.UPDATED,
                        media
                )
        );
    }


}
