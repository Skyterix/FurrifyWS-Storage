package ws.furrify.posts.tag.dto.query;

import ws.furrify.posts.tag.TagType;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface TagDetailsQueryDTO extends Serializable {
    String getValue();

    UUID getOwnerId();

    TagType getType();

    ZonedDateTime getCreateDate();
}
