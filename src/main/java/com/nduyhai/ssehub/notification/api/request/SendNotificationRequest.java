package com.nduyhai.ssehub.notification.api.request;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record SendNotificationRequest(
    @NotBlank String userId,
    @NotBlank String message,
    String type,
    Map<String, String> templateVariables) {}
