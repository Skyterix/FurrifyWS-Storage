package ws.furrify.sources.source.dto.command;


import lombok.ToString;
import lombok.Value;
import ws.furrify.shared.dto.CommandDTO;
import ws.furrify.sources.source.dto.SourceDTO;
import ws.furrify.sources.source.strategy.SourceStrategy;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

/**
 * @author Skyte
 */
@Value
@ToString
public class SourceCreateCommandDTO implements CommandDTO<SourceDTO> {

    @NotNull
    HashMap<String, String> data;

    @NotNull
    SourceStrategy strategy;

    @Override
    public SourceDTO toDTO() {
        return SourceDTO.builder()
                .data(data)
                .strategy(strategy)
                .build();
    }
}
