package cloud.zipbob.ingredientsmanageservice.domain.ingredient.service;

import cloud.zipbob.ingredientsmanageservice.config.RabbitMQProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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

    public void sendMessage(List<String> ingredients, List<String> quantities) {
        try {
            List<Map<String, String>> messagePayload = new ArrayList<>();

            for (int i = 0; i < ingredients.size(); i++) {
                String ingredientName = ingredients.get(i);
                String quantityWithUnit = quantities.get(i);

                messagePayload.add(Map.of(
                        "ingredients", ingredientName,
                        "quantities", quantityWithUnit
                ));
            }

            String message = objectMapper.writeValueAsString(messagePayload);

            rabbitTemplate.convertAndSend(
                    rabbitMQProperties.getExchangeName(),
                    rabbitMQProperties.getRoutingKey(),
                    message
            );

            log.info("Message sent successfully to RabbitMQ. Exchange: {}, Message: {}",
                    rabbitMQProperties.getExchangeName(), message);
        } catch (Exception e) {
            log.error("Failed to send message to RabbitMQ. Exchange: {}, Error: {}",
                    rabbitMQProperties.getExchangeName(), e.getMessage());
        }
    }
}
