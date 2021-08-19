package ws.furrify.sources.source;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.pageable.PageableRequest;
import ws.furrify.sources.source.dto.query.SourceDetailsQueryDTO;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/{userId}/sources")
@RequiredArgsConstructor
class QuerySourceController {

    private final SqlSourceQueryRepositoryImpl sourceQueryRepository;
    private final PagedResourcesAssembler<SourceDetailsQueryDTO> pagedResourcesAssembler;

    @GetMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('query_user_sources') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public PagedModel<EntityModel<SourceDetailsQueryDTO>> getUserSources(
            @PathVariable UUID userId,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer page,
            KeycloakAuthenticationToken keycloakAuthenticationToken) {

        // Build page from page information
        Pageable pageable = PageableRequest.builder()
                .order(order)
                .sort(sort)
                .size(size)
                .page(page)
                .build().toPageable();

        PagedModel<EntityModel<SourceDetailsQueryDTO>> posts = pagedResourcesAssembler.toModel(
                sourceQueryRepository.findAllByOwnerId(userId, pageable)
        );

        posts.forEach(this::addSourceRelations);

        // Add hateoas relation
        var sourcesRel = linkTo(methodOn(QuerySourceController.class).getUserSources(
                userId,
                null,
                null,
                null,
                null,
                null
        )).withSelfRel();

        posts.add(sourcesRel);

        return posts;
    }

    @GetMapping("/{sourceId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('query_user_sources') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public EntityModel<SourceDetailsQueryDTO> getUserSource(@PathVariable UUID userId,
                                                            @PathVariable UUID sourceId,
                                                            KeycloakAuthenticationToken keycloakAuthenticationToken) {

        SourceDetailsQueryDTO sourceQueryDTO = sourceQueryRepository.findByOwnerIdAndSourceId(userId, sourceId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(sourceId)));

        return addSourceRelations(
                EntityModel.of(sourceQueryDTO)
        );
    }

    private EntityModel<SourceDetailsQueryDTO> addSourceRelations(EntityModel<SourceDetailsQueryDTO> sourceQueryDtoModel) {
        var sourceQueryDto = sourceQueryDtoModel.getContent();
        // Check if model content is empty
        if (sourceQueryDto == null) {
            throw new IllegalStateException("Entity model contains empty content.");
        }

        var selfRel = linkTo(methodOn(QuerySourceController.class).getUserSource(
                sourceQueryDto.getOwnerId(),
                sourceQueryDto.getSourceId(),
                null
        )).withSelfRel().andAffordance(
                afford(methodOn(CommandSourceController.class).deleteSource(
                        sourceQueryDto.getOwnerId(), sourceQueryDto.getSourceId(), null
                ))
        ).andAffordance(
                afford(methodOn(CommandSourceController.class).replaceSource(
                        sourceQueryDto.getOwnerId(), sourceQueryDto.getSourceId(), null, null
                ))
        ).andAffordance(
                afford(methodOn(CommandSourceController.class).updateSource(
                        sourceQueryDto.getOwnerId(), sourceQueryDto.getSourceId(), null, null
                ))
        );

        var sourcesRel = linkTo(methodOn(QuerySourceController.class).getUserSources(
                sourceQueryDto.getOwnerId(),
                null,
                null,
                null,
                null,
                null
        )).withRel("userSources");

        sourceQueryDtoModel.add(selfRel);
        sourceQueryDtoModel.add(sourcesRel);

        return sourceQueryDtoModel;
    }
}
