package ws.furrify.posts.attachment;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.posts.attachment.dto.AttachmentDtoFactory;
import ws.furrify.posts.kafka.KafkaTopicEventPublisher;

@Configuration
@RequiredArgsConstructor
class AttachmentConfig {

    private final AttachmentRepositoryImpl attachmentRepository;
    private final AttachmentQueryRepository attachmentQueryRepository;
    private final KafkaTopicEventPublisher<AttachmentEvent> eventPublisher;

    @Bean
    AttachmentFacade attachmentFacade() {
        var attachmentFactory = new AttachmentFactory();
        var attachmentDtoFactory = new AttachmentDtoFactory(attachmentQueryRepository);

        return new AttachmentFacade(
                new CreateAttachmentImpl(attachmentFactory, eventPublisher),
                new DeleteAttachmentImpl(eventPublisher, attachmentRepository),
                new UpdateAttachmentImpl(eventPublisher, attachmentRepository),
                new ReplaceAttachmentImpl(eventPublisher, attachmentRepository),
                attachmentRepository,
                attachmentFactory,
                attachmentDtoFactory
        );
    }
}