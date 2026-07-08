package com.schemaforge.activity.dto;

import com.schemaforge.activity.entity.ActivityType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

public record ActivityFilterRequest(
        ActivityType activityType,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant from,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant to
) {
}