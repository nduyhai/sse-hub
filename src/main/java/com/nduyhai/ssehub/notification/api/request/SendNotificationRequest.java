package com.nduyhai.ssehub.notification.api.request;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public record SendNotificationRequest(
    @NotBlank String userId,
    @NotBlank String message,
    @Nullable String type,
    @Nullable Map<String, String> templateVariables) {}
