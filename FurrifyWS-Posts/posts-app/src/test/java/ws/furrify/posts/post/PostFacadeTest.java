package ws.furrify.posts.post;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.posts.DomainEventPublisher;
import ws.furrify.posts.PostEvent;
import ws.furrify.posts.exception.RecordNotFoundException;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.dto.PostDtoFactory;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostFacadeTest {

    private static PostRepository postRepository;
    private static PostFacade postFacade;

    private PostDTO postDTO;
    private Post post;

    @BeforeEach
    void setUp() {
        postDTO = PostDTO.builder()
                .title("Test")
                .description("dsa")
                .ownerId(UUID.randomUUID())
                .createDate(ZonedDateTime.now())
                .build();

        post = new PostFactory().from(postDTO);
    }

    @BeforeAll
    static void beforeAll() {
        postRepository = mock(PostRepository.class);

        var postFactory = new PostFactory();
        var postDTOFactory = new PostDtoFactory();
        @SuppressWarnings("unchecked")
        var eventPublisher = (DomainEventPublisher<PostEvent>) mock(DomainEventPublisher.class);

        postFacade = new PostFacade(
                new CreatePostAdapter(postFactory, eventPublisher),
                new DeletePostAdapter(eventPublisher, postRepository),
                new UpdatePostDetailsDetailsAdapter(eventPublisher, postRepository),
                new ReplacePostDetailsDetailsAdapter(eventPublisher, postRepository),
                postRepository,
                postFactory,
                postDTOFactory
        );
    }

    @Test
    @DisplayName("Create post")
    void createPost() {
        // Given ownerId and postDTO
        UUID userId = UUID.randomUUID();
        // When createPost() method called
        // Then return generated uuid
        assertNotNull(postFacade.createPost(userId, postDTO), "PostId was not returned.");
    }

    @Test
    @DisplayName("Replace post")
    void replacePost() {
        // Given postDTO, userId and postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When replacePost() method called
        when(postRepository.findByOwnerIdAndPostId(userId, postId)).thenReturn(Optional.of(post));
        // Then run successfully
        assertDoesNotThrow(() -> postFacade.replacePostDetails(userId, postId, postDTO), "Exception was thrown");
    }

    @Test
    @DisplayName("Replace post with non existing postId")
    void replacePost2() {
        // Given postDTO, userId and non existing postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When replacePost() method called
        when(postRepository.findByOwnerIdAndPostId(userId, postId)).thenReturn(Optional.empty());
        // Then throw no record found exception
        assertThrows(
                RecordNotFoundException.class,
                () -> postFacade.replacePostDetails(userId, postId, postDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update post")
    void updatePost() {
        // Given postDTO, userId and postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When updatePost() method called
        when(postRepository.findByOwnerIdAndPostId(userId, postId)).thenReturn(Optional.of(post));
        // Then run successfully
        assertDoesNotThrow(() -> postFacade.updatePostDetails(userId, postId, postDTO), "Exception was thrown");
    }

    @Test
    @DisplayName("Update post with non existing postId")
    void updatePost2() {
        // Given postDTO, userId and non existing postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When updatePost() method called
        when(postRepository.findByOwnerIdAndPostId(userId, postId)).thenReturn(Optional.empty());
        // Then throw no record found exception
        assertThrows(
                RecordNotFoundException.class,
                () -> postFacade.updatePostDetails(userId, postId, postDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Delete post")
    void deletePost() {
        // Given userId and postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When deletePost() method called
        when(postRepository.existsByOwnerIdAndPostId(userId, postId)).thenReturn(true);
        // Then run successfully
        assertDoesNotThrow(() -> postFacade.deletePost(userId, postId), "Exception was thrown");
    }

    @Test
    @DisplayName("Delete post with non existing postId")
    void deletePost2() {
        // Given userId and postId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        // When deletePost() method called
        when(postRepository.existsByOwnerIdAndPostId(userId, postId)).thenReturn(false);
        // Then throw record not found exception
        assertThrows(RecordNotFoundException.class, () -> postFacade.deletePost(userId, postId), "Exception was not thrown");
    }
}