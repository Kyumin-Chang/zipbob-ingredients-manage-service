package cloud.zipbob.ingredientsmanageservice.domain.ingredient.service;

import cloud.zipbob.ingredientsmanageservice.config.RabbitMQProperties;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQProducer {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;
    private final ObjectMapper objectMapper;

    public void sendMessage(Long memberId, List<IngredientType> ingredients, List<UnitType> unitTypes,
                            List<Integer> quantities) {
        try {
            String message = objectMapper.writeValueAsString(
                    Map.of("memberId", memberId, "ingredients", ingredients, "unitTypes", unitTypes,
                            "quantities", quantities)
            );
            rabbitTemplate.convertAndSend(rabbitMQProperties.getExchangeName(), rabbitMQProperties.getRoutingKey(),
                    message);
            log.info("Message sent successfully to RabbitMQ. Exchange: {}, Message: {}",
                    rabbitMQProperties.getExchangeName(), message);
        } catch (Exception e) {
            log.error("Failed to send message to RabbitMQ. Exchange: {}, Error: {}",
                    rabbitMQProperties.getExchangeName(), e.getMessage());
        }
    }
}
