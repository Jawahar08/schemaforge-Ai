package com.schemaforge.dashboard.service;

import com.schemaforge.dashboard.dto.DashboardSummaryResponse;
import com.schemaforge.user.entity.User;

public interface DashboardService {

    DashboardSummaryResponse getSummary(User currentUser);
}