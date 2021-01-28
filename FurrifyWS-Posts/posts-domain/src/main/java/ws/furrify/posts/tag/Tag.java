package ws.furrify.posts.tag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import ws.furrify.posts.exception.Errors;
import ws.furrify.posts.exception.RecordAlreadyExistsException;

import java.time.ZonedDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
class Tag {
    private final Long id;
    @NonNull
    private String value;
    @NonNull
    private final UUID ownerId;
    @NonNull
    private TagType type;
    private final ZonedDateTime createDate;

    static Tag restore(TagSnapshot tagSnapshot) {
        return new Tag(
                tagSnapshot.getId(),
                tagSnapshot.getValue(),
                tagSnapshot.getOwnerId(),
                tagSnapshot.getType(),
                tagSnapshot.getCreateDate()
        );
    }

    TagSnapshot getSnapshot() {
        return new TagSnapshot(
                id,
                value,
                ownerId,
                type,
                createDate
        );
    }

    void updateValue(@NonNull final String value,
                     @NonNull final TagRepository tagRepository) {
        if (tagRepository.existsByOwnerIdAndValue(ownerId, value)) {
            throw new RecordAlreadyExistsException(Errors.TAG_ALREADY_EXISTS.getErrorMessage(value));
        }

        this.value = value;
    }

    void updateType(@NonNull final TagType type) {
        this.type = type;
    }
}
