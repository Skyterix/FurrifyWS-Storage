package ws.furrify.posts.tag.dto.command;

import lombok.Data;
import ws.furrify.posts.CommandDTO;
import ws.furrify.posts.tag.TagType;
import ws.furrify.posts.tag.dto.TagDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Skyte
 */
@Data
public class TagCreateCommandDTO implements CommandDTO<TagDTO> {

    @NotBlank
    @Size(max = 32)
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$")
    private String value;

    @NotNull
    private TagType type;

    @Override
    public TagDTO toDTO() {
        return TagDTO.builder()
                .value(value)
                .type(type)
                .build();
    }
}