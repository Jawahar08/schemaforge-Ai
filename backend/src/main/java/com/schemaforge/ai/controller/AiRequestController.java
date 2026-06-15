package com.schemaforge.ai.controller;

import com.schemaforge.ai.dto.AiRequestResponse;
import com.schemaforge.ai.dto.AiRequestSummaryResponse;
import com.schemaforge.ai.service.AiRequestService;
import com.schemaforge.common.dto.ApiResponse;
import com.schemaforge.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/ai/requests")
@RequiredArgsConstructor
@Tag(name = "AI Requests", description = "Endpoints for viewing AI request audit history")
public class AiRequestController {

    private final AiRequestService aiRequestService;

    @GetMapping
    @Operation(summary = "List AI requests", description = "Returns a paginated history of AI requests made by the authenticated user")
    public ResponseEntity<ApiResponse<Page<AiRequestSummaryResponse>>> getRequests(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<AiRequestSummaryResponse> response = aiRequestService.getRequestsForUser(currentUser, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{requestId}")
    @Operation(summary = "Get AI request by id", description = "Returns the full prompt, response, and token usage for a specific AI request")
    public ResponseEntity<ApiResponse<AiRequestResponse>> getRequest(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID requestId
    ) {
        AiRequestResponse response = aiRequestService.getRequestById(currentUser, requestId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}