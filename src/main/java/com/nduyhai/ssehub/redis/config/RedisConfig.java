package com.nduyhai.ssehub.redis.config;

import com.nduyhai.ssehub.redis.subscriber.RedisNotificationSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfig {

    public static final String CHANNEL_PREFIX = "notification:user:";
    public static final String CHANNEL_PATTERN = CHANNEL_PREFIX + "*";

    @Bean
    RedisMessageListenerContainer redisListenerContainer(
            RedisConnectionFactory factory,
            RedisNotificationSubscriber subscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(subscriber, new PatternTopic(CHANNEL_PATTERN));
        return container;
    }
}
