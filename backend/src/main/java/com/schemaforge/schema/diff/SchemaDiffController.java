package com.schemaforge.schema.diff;

import com.schemaforge.common.dto.ApiResponse;
import com.schemaforge.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Schema Diff", description = "Compare two schema versions and see structural differences")
public class SchemaDiffController {

    private final SchemaDiffService schemaDiffService;

    @GetMapping("/api/schemas/{schemaId}/versions/{fromVersion}/diff/{toVersion}")
    @Operation(
            summary = "Diff two schema versions",
            description = "Returns a structured comparison between two versions of a schema: "
                    + "tables added/removed/modified, columns added/removed/modified, "
                    + "relationships added/removed. Caller must own the parent project."
    )
    public ResponseEntity<ApiResponse<SchemaDiffResponse>> diff(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID schemaId,
            @PathVariable int fromVersion,
            @PathVariable int toVersion
    ) {
        SchemaDiffResponse response =
                schemaDiffService.diffVersions(currentUser, schemaId, fromVersion, toVersion);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}