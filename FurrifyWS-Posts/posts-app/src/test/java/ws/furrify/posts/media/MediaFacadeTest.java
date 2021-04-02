package ws.furrify.posts.media;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.dto.MediaDtoFactory;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MediaFacadeTest {

    private static MediaRepository mediaRepository;
    private static MediaFacade mediaFacade;

    private MediaDTO mediaDTO;
    private Media media;

    @BeforeEach
    void setUp() {
        mediaDTO = MediaDTO.builder()
                .ownerId(UUID.randomUUID())
                .postId(UUID.randomUUID())
                .mediaId(UUID.randomUUID())
                .priority(0)
                .extension(MediaExtension.PNG)
                .filename("yes.png")
                .fileHash("3c518eeb674c71b30297f072fde7eba5")
                .createDate(ZonedDateTime.now())
                .build();

        media = new MediaFactory().from(mediaDTO);
    }

    @BeforeAll
    static void beforeAll() {
        mediaRepository = mock(MediaRepository.class);

        var mediaFactory = new MediaFactory();
        var mediaDtoFactory = new MediaDtoFactory();

        @SuppressWarnings("unchecked")
        var eventPublisher = (DomainEventPublisher<MediaEvent>) mock(DomainEventPublisher.class);

        mediaFacade = new MediaFacade(
                new CreateMediaAdapter(mediaFactory, eventPublisher),
                new DeleteMediaAdapter(eventPublisher, mediaRepository),
                new UpdateMediaAdapter(eventPublisher, mediaRepository),
                new ReplaceMediaAdapter(eventPublisher, mediaRepository),
                mediaRepository,
                mediaFactory,
                mediaDtoFactory
        );
    }

    @Test
    @DisplayName("Create media")
    void createMedia() {
        // Given ownerId and mediaDTO
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When createMedia() method called
        // Then return generated uuid
        assertNotNull(mediaFacade.createMedia(userId, postId, mediaDTO), "MediaId was not returned.");
    }

    @Test
    @DisplayName("Replace media")
    void replaceMedia() {
        // Given mediaDto, userId and postId and mediaId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        // When replaceMedia() method called
        when(mediaRepository.findByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId))
                .thenReturn(Optional.of(media));
        // Then run successfully
        assertDoesNotThrow(() -> mediaFacade.replaceMedia(userId, postId, mediaId, mediaDTO), "Exception was thrown");
    }

    @Test
    @DisplayName("Replace media with non existing mediaId")
    void replaceMedia2() {
        // Given mediaDTO, userId, postId and non existing mediaId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        // When replaceMedia() method called
        when(mediaRepository.findByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId)).thenReturn(Optional.empty());
        // Then throw no record found exception
        assertThrows(
                RecordNotFoundException.class,
                () -> mediaFacade.replaceMedia(userId, postId, mediaId, mediaDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update media")
    void updateMedia() {
        // Given mediaDTO, userId, postId and mediaId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        // When updateMedia() method called
        when(mediaRepository.findByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId))
                .thenReturn(Optional.of(media));
        // Then run successfully
        assertDoesNotThrow(() -> mediaFacade.updateMedia(userId, postId, mediaId, mediaDTO), "Exception was thrown");
    }

    @Test
    @DisplayName("Update media with non existing mediaId")
    void updateMedia2() {
        // Given mediaDTO, userId, postId and non existing mediaId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        // When updateMedia() method called
        when(mediaRepository.findByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId))
                .thenReturn(Optional.empty());
        // Then throw no record found exception
        assertThrows(
                RecordNotFoundException.class,
                () -> mediaFacade.updateMedia(userId, postId, mediaId, mediaDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Delete media")
    void deleteMedia() {
        // Given userId, postId and mediaId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        // When deleteMedia() method called
        when(mediaRepository.existsByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId)).thenReturn(true);
        // Then run successfully
        assertDoesNotThrow(() -> mediaFacade.deleteMedia(userId, postId, mediaId), "Exception was thrown");
    }

    @Test
    @DisplayName("Delete media with non existing mediaId")
    void deleteMedia2() {
        // Given userId, postId and non existing mediaId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        // When deleteMedia() method called
        when(mediaRepository.existsByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId)).thenReturn(false);
        // Then throw record not found exception
        assertThrows(RecordNotFoundException.class, () -> mediaFacade.deleteMedia(userId, postId, mediaId), "Exception was not thrown");
    }
}
