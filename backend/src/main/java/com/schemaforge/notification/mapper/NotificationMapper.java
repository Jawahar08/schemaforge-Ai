package com.schemaforge.notification.mapper;

import com.schemaforge.notification.dto.NotificationResponse;
import com.schemaforge.notification.dto.NotificationSummaryResponse;
import com.schemaforge.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);

    NotificationSummaryResponse toSummaryResponse(Notification notification);
}
