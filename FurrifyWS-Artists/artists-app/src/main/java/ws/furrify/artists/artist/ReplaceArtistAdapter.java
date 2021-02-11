package ws.furrify.artists.artist;

import lombok.RequiredArgsConstructor;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.artists.artist.vo.ArtistData;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor
class ReplaceArtistAdapter implements ReplaceArtistPort {

    private final ArtistRepository artistRepository;
    private final DomainEventPublisher<ArtistEvent> eventPublisher;

    @Override
    public void replaceArtist(final UUID ownerId, final UUID artistId, final ArtistDTO artistDTO) {
        Artist artist = artistRepository.findByOwnerIdAndArtistId(ownerId, artistId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId.toString())));

        // Replace fields in artist
        artist.updateNicknames(
                artistDTO.getNicknames(), artistDTO.getPreferredNickname(), artistRepository
        );

        // Publish create artist event
        eventPublisher.publish(
                DomainEventPublisher.Topic.ARTIST,
                // Use ownerId as key
                ownerId,
                createArtistEvent(artist)
        );

    }

    private ArtistEvent createArtistEvent(Artist artist) {
        ArtistSnapshot artistSnapshot = artist.getSnapshot();

        return ArtistEvent.newBuilder()
                .setState(DomainEventPublisher.ArtistEventType.REPLACED.name())
                .setId(artistSnapshot.getId())
                .setArtistId(artistSnapshot.getArtistId().toString())
                .setData(
                        ArtistData.newBuilder()
                                .setOwnerId(artistSnapshot.getOwnerId().toString())
                                .setNicknames(new ArrayList<>(artistSnapshot.getNicknames()))
                                .setPreferredNickname(artistSnapshot.getPreferredNickname())
                                .setCreateDate(artistSnapshot.getCreateDate().toInstant().toEpochMilli())
                                .build()
                )
                .setOccurredOn(Instant.now().toEpochMilli())
                .build();
    }
}
