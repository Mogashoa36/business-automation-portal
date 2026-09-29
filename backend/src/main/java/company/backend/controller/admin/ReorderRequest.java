package company.backend.controller.admin;

import java.util.List;

import jakarta.validation.constraints.NotNull;

/** Body of the reorder endpoint: the case studies in their new order. */
public record ReorderRequest(@NotNull List<Long> ids) {
}