package com.nduyhai.ssehub.notification.application;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
class NotificationTemplateService {

    String render(String template, Map<String, String> variables) {
        if (template == null || CollectionUtils.isEmpty(variables)) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}
