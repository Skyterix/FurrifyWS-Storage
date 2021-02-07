package ws.furrify.artists.artist.dto.command;


import lombok.ToString;
import lombok.Value;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.shared.dto.CommandDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author Skyte
 */
@Value
@ToString
public class ArtistCreateCommandDTO implements CommandDTO<ArtistDTO> {

    @Size(min = 1, max = 255)
    Set<@NotBlank @Size(max = 255) String> nicknames;

    @NotBlank
    @Size(min = 1, max = 255)
    String preferredNickname;

    @Override
    public ArtistDTO toDTO() {
        return ArtistDTO.builder()
                .nicknames(nicknames)
                .preferredNickname(preferredNickname)
                .build();
    }
}
