package ws.furrify.artists.artist;

import java.util.Optional;
import java.util.UUID;

interface ArtistRepository {
    Artist save(Artist artist);

    void deleteByArtistId(UUID artistId);

    boolean existsByOwnerIdAndPreferredNickname(UUID ownerId, String preferredNickname);

    Optional<Artist> findByOwnerIdAndArtistId(UUID ownerId, UUID artistId);

    boolean existsByOwnerIdAndArtistId(UUID ownerId, UUID artistId);

    long countPostsByUserId(UUID userId);
}
