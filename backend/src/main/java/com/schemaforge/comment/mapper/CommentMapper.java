package com.schemaforge.comment.mapper;

import com.schemaforge.comment.dto.CommentResponse;
import com.schemaforge.comment.dto.CommentSummaryResponse;
import com.schemaforge.comment.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    CommentResponse toResponse(Comment comment);

    CommentSummaryResponse toSummaryResponse(Comment comment);
}
