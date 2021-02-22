package ws.furrify.artists.artist;

import lombok.RequiredArgsConstructor;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
class UpdateArtistAdapter implements UpdateArtistPort {

    private final ArtistRepository artistRepository;
    private final DomainEventPublisher<ArtistEvent> eventPublisher;

    @Override
    public void updateArtist(final UUID ownerId, final UUID artistId, final ArtistDTO artistDTO) {
        Artist artist = artistRepository.findByOwnerIdAndArtistId(ownerId, artistId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId.toString())));

        // Update changed fields in artist
        if (artistDTO.getNicknames() != null || artistDTO.getPreferredNickname() != null) {
            artist.updateNicknames(
                    artistDTO.getNicknames(), artistDTO.getPreferredNickname(), artistRepository
            );
        }

        // Publish create artist event
        eventPublisher.publish(
                DomainEventPublisher.Topic.ARTIST,
                // Use ownerId as key
                ownerId,
                ArtistUtils.createArtistEvent(
                        DomainEventPublisher.ArtistEventType.UPDATED,
                        artist
                )
        );
    }
}
