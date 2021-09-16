package ws.furrify.sources.source;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.source.dto.SourceDTO;
import ws.furrify.sources.source.strategy.DeviantArtV1SourceStrategy;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class SourceTest implements CommandLineRunner {

    private final SqlSourceRepository sqlSourceRepository;
    private final Environment environment;

    @Override
    public void run(final String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter("dev"::equals)
                .findAny()
                .ifPresent((profile) -> createTestingSources());
    }

    private void createTestingSources() {
        var sourceFactory = new SourceFactory();

        var userId = UUID.fromString("82722f67-ec52-461f-8294-158d8affe7a3");
        var postId = UUID.fromString("7c2c35f3-20e9-4b7e-a455-253b7b78e2fa");
        var sourceId = UUID.fromString("02038a77-9717-4de8-a21b-3a722f158be2");
        var originId = UUID.fromString("9551e7e0-4550-41b9-8c4a-57943642fa00");

        sqlSourceRepository.save(
                sourceFactory.from(
                        SourceDTO.builder()
                                .originId(originId)
                                .postId(postId)
                                .sourceId(sourceId)
                                .ownerId(userId)
                                .strategy(new DeviantArtV1SourceStrategy())
                                .data(new HashMap<>(1) {{
                                    put("id", "123");
                                }})
                                .originType(SourceOriginType.ARTIST)
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println("SourceId: " + sourceId);
    }

}
