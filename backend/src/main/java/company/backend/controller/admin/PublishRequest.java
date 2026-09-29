package company.backend.controller.admin;

import jakarta.validation.constraints.NotNull;

/** Body of the publish/unpublish endpoint. */
public record PublishRequest(@NotNull Boolean published) {
}