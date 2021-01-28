package ws.furrify.posts.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.posts.tag.dto.TagDTO;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class TagTest implements CommandLineRunner {

    private final SqlTagRepository sqlTagRepository;
    private final Environment environment;

    @Override
    public void run(final String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter("dev"::equals)
                .findAny()
                .ifPresent((profile) -> createTestingPosts());
    }

    private void createTestingPosts() {
        var tagFactory = new TagFactory();

        var userId = UUID.fromString("152af668-4e26-4f78-9d4a-30e05e546536");
        var tagValue = "walking";

        sqlTagRepository.save(
                tagFactory.from(
                        TagDTO.builder()
                                .value(tagValue)
                                .ownerId(userId)
                                .type(TagType.ACTION)
                                .createDate(ZonedDateTime.now())
                                .build()
                ).getSnapshot()
        );

        System.out.println("Tag value: " + tagValue);
    }

}
