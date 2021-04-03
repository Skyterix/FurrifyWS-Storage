package ws.furrify.posts.media;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

interface MediaRepository {
    Set<Media> findAllByOwnerIdAndPostId(UUID ownerId, UUID postId);

    Optional<Media> findByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID postId, UUID mediaId);

    void deleteByMediaId(UUID mediaId);

    boolean existsByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID postId, UUID mediaId);

    Media save(Media media);
}
