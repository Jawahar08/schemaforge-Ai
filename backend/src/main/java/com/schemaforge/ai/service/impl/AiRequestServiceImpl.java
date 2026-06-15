package com.schemaforge.ai.service.impl;

import com.schemaforge.ai.dto.AiRequestResponse;
import com.schemaforge.ai.dto.AiRequestSummaryResponse;
import com.schemaforge.ai.entity.AiRequest;
import com.schemaforge.ai.exception.AiRequestNotFoundException;
import com.schemaforge.ai.mapper.AiRequestMapper;
import com.schemaforge.ai.repository.AiRequestRepository;
import com.schemaforge.ai.service.AiRequestService;
import com.schemaforge.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiRequestServiceImpl implements AiRequestService {

    private final AiRequestRepository aiRequestRepository;
    private final AiRequestMapper aiRequestMapper;

    @Override
    public Page<AiRequestSummaryResponse> getRequestsForUser(User user, Pageable pageable) {
        return aiRequestRepository.findAllByUserId(user.getId(), pageable)
                .map(aiRequestMapper::toSummaryResponse);
    }

    @Override
    public AiRequestResponse getRequestById(User user, UUID requestId) {
        AiRequest request = aiRequestRepository.findByIdAndUserId(requestId, user.getId())
                .orElseThrow(() -> new AiRequestNotFoundException(requestId));

        return aiRequestMapper.toResponse(request);
    }
}