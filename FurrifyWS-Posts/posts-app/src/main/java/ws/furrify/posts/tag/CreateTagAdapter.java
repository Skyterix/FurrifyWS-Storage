package ws.furrify.posts.tag;

import lombok.RequiredArgsConstructor;
import ws.furrify.posts.DomainEventPublisher;
import ws.furrify.posts.TagEvent;
import ws.furrify.posts.exception.Errors;
import ws.furrify.posts.exception.RecordAlreadyExistsException;
import ws.furrify.posts.tag.dto.TagDTO;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
class CreateTagAdapter implements CreateTagPort {

    private final TagFactory tagFactory;
    private final DomainEventPublisher<TagEvent> domainEventPublisher;
    private final TagRepository tagRepository;

    @Override
    public String createTag(final UUID userId, final TagDTO tagDTO) {
        if (tagRepository.existsByValue(tagDTO.getValue())) {
            throw new RecordAlreadyExistsException(Errors.TAG_ALREADY_EXISTS.getErrorMessage(tagDTO.getValue()));
        }

        // Edit tagDTO with current time and userId
        TagDTO updatedTagToCreateDTO = tagDTO.toBuilder()
                .ownerId(userId)
                .createDate(ZonedDateTime.now())
                .build();

        // Publish create tag event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.TAG,
                // User userId as key
                userId,
                createTagEvent(tagFactory.from(updatedTagToCreateDTO))
        );

        return updatedTagToCreateDTO.getValue();
    }

    private TagEvent createTagEvent(final Tag tag) {
        TagSnapshot tagSnapshot = tag.getSnapshot();

        return TagEvent.newBuilder()
                .setState(TagEventType.CREATED.name())
                .setValue(tagSnapshot.getValue())
                .setOwnerId(tagSnapshot.getOwnerId().toString())
                .setType(tagSnapshot.getType().name())
                .setCreateDate(tagSnapshot.getCreateDate().toInstant().toEpochMilli())
                .setOccurredOn(Instant.now().toEpochMilli())
                .build();
    }
}
