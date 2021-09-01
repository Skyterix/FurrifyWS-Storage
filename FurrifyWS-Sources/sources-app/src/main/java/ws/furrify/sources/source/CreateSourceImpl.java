package ws.furrify.sources.source;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.source.dto.SourceDTO;

import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
final class CreateSourceImpl implements CreateSource {

    private final SourceRepository sourceRepository;
    private final SourceFactory sourceFactory;
    private final DomainEventPublisher<SourceEvent> eventPublisher;

    @Override
    public UUID createSource(@NonNull final UUID ownerId,
                             @NonNull final SourceDTO sourceDTO) {
        // Generate source UUID
        UUID sourceId = UUID.randomUUID();

        // Update sourceDTO and create Source from that data
        Source source = sourceFactory.from(
                sourceDTO.toBuilder()
                        .sourceId(sourceId)
                        .ownerId(ownerId)
                        .createDate(ZonedDateTime.now())
                        .build()
        );

        // Publish create source event
        eventPublisher.publish(
                DomainEventPublisher.Topic.ARTIST,
                // Use ownerId as key
                ownerId,
                SourceUtils.createSourceEvent(
                        DomainEventPublisher.SourceEventType.CREATED,
                        source
                )
        );


        return sourceId;
    }
}