package com.schemaforge.ai.service;

import com.schemaforge.ai.dto.AiRequestResponse;
import com.schemaforge.ai.dto.AiRequestSummaryResponse;
import com.schemaforge.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AiRequestService {

    Page<AiRequestSummaryResponse> getRequestsForUser(User user, Pageable pageable);

    AiRequestResponse getRequestById(User user, UUID requestId);
}