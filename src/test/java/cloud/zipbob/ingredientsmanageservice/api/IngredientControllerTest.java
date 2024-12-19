package cloud.zipbob.ingredientsmanageservice.api;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.repository.IngredientRepository;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.CheckAndSendMessageRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.ExpiredIngredientRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.GetIngredientsByTypeRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.IngredientAddRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.IngredientRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.RecipeSelectRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.UpdateQuantityRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.service.RabbitMQProducer;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.repository.RefrigeratorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class IngredientControllerTest {

    @Container
    static final MariaDBContainer<?> mariadbContainer = new MariaDBContainer<>("mariadb:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadbContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariadbContainer::getUsername);
        registry.add("spring.datasource.password", mariadbContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @MockBean
    private RabbitMQProducer rabbitMQProducer;

    private Long refrigeratorId;

    @BeforeEach
    void setUp() {
        Refrigerator refrigerator = refrigeratorRepository.save(
                Refrigerator.builder()
                        .memberId(10L)
                        .build()
        );
        refrigeratorId = refrigerator.getId();
    }

    @Test
    @DisplayName("재료 추가 - 새로운 재료들이 성공적으로 추가")
    void addIngredient_ShouldReturnSuccessResponse() throws Exception {
        // Given
        IngredientAddRequest request = new IngredientAddRequest(
                10L,
                List.of(IngredientType.EGG, IngredientType.MILK),
                List.of(10, 2),
                List.of(UnitType.PIECE, UnitType.LITER),
                List.of(LocalDate.now().plusDays(7), LocalDate.now().plusDays(5))
        );

        // When & Then
        mockMvc.perform(post("/ingredients")
                        .header("X-Member-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("EGG"))
                .andExpect(jsonPath("$[0].quantity").value(10))
                .andExpect(jsonPath("$[0].unitType").value("PIECE"))
                .andExpect(jsonPath("$[0].expiredDate").exists())
                .andExpect(jsonPath("$[1].type").value("MILK"))
                .andExpect(jsonPath("$[1].quantity").value(2))
                .andExpect(jsonPath("$[1].unitType").value("LITER"))
                .andExpect(jsonPath("$[1].expiredDate").exists());
    }


    @Test
    @DisplayName("재료 삭제 - 재료가 성공적으로 삭제")
    void deleteIngredient_ShouldReturnSuccessResponse() throws Exception {
        // Given
        ingredientRepository.save(
                Ingredient.builder()
                        .type(IngredientType.MILK)
                        .quantity(1)
                        .unitType(UnitType.LITER)
                        .expiredDate(LocalDate.now().plusDays(5))
                        .refrigerator(refrigeratorRepository.findById(refrigeratorId).get())
                        .build()
        );
        IngredientRequest request = new IngredientRequest(10L, IngredientType.MILK);

        // When & Then
        mockMvc.perform(delete("/ingredients")
                        .header("X-Member-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refrigeratorId").value(refrigeratorId))
                .andExpect(jsonPath("$.ingredientType").value("MILK"));
    }

    @Test
    @DisplayName("재료 삭제 - 냉장고가 존재하지 않을 때 실패")
    void deleteIngredient_ShouldReturnBadRequestForInvalidRefrigerator() throws Exception {
        // Given
        IngredientRequest request = new IngredientRequest(99L, IngredientType.MILK);

        // When & Then
        mockMvc.perform(delete("/ingredients")
                        .header("X-Member-Id", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("해당 회원의 냉장고가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("재료 삭제 - 재료가 존재하지 않을 때 실패")
    void deleteIngredient_ShouldReturnBadRequestForNonExistentIngredient() throws Exception {
        // Given
        IngredientRequest request = new IngredientRequest(10L, IngredientType.SALT);

        // When & Then
        mockMvc.perform(delete("/ingredients")
                        .header("X-Member-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("해당 회원에게 재료가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("재료 수량 업데이트 - 재료의 수량이 성공적으로 업데이트")
    void updateQuantity_ShouldReturnSuccessResponse() throws Exception {
        // Given
        ingredientRepository.save(
                Ingredient.builder()
                        .type(IngredientType.CARROT)
                        .quantity(5)
                        .unitType(UnitType.PIECE)
                        .expiredDate(LocalDate.now().plusDays(3))
                        .refrigerator(refrigeratorRepository.findById(refrigeratorId).get())
                        .build()
        );
        UpdateQuantityRequest request = new UpdateQuantityRequest(10L, IngredientType.CARROT, 10);

        // When & Then
        mockMvc.perform(patch("/ingredients/update")
                        .header("X-Member-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(10L))
                .andExpect(jsonPath("$.ingredientType").value("CARROT"))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    @DisplayName("유통기한 지난 재료 조회 - 유통기한이 지난 재료 목록을 반환")
    void getExpiredIngredients_ShouldReturnExpiredIngredients() throws Exception {
        // Given
        ingredientRepository.save(
                Ingredient.builder()
                        .type(IngredientType.BEEF)
                        .quantity(2)
                        .unitType(UnitType.KILOGRAM)
                        .expiredDate(LocalDate.now().minusDays(1))
                        .refrigerator(refrigeratorRepository.findById(refrigeratorId).get())
                        .build()
        );
        ExpiredIngredientRequest request = new ExpiredIngredientRequest(10L);

        // When & Then
        mockMvc.perform(get("/ingredients/expired")
                        .header("X-Member-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refrigeratorId").value(refrigeratorId))
                .andExpect(jsonPath("$.expiredIngredients[0].type").value("BEEF"));
    }

    @Test
    @DisplayName("카테고리별 재료 조회 - 특정 카테고리의 재료 목록을 반환")
    void getIngredientsByType_ShouldReturnIngredientsByCategory() throws Exception {
        // Given
        GetIngredientsByTypeRequest request = new GetIngredientsByTypeRequest(IngredientType.Category.MAIN);

        // When & Then
        mockMvc.perform(get("/ingredients/type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients").isArray())
                .andExpect(jsonPath("$.ingredients").value(org.hamcrest.Matchers.hasItem("EGG")))
                .andExpect(jsonPath("$.ingredients").value(org.hamcrest.Matchers.hasItem("CHICKEN")))
                .andExpect(jsonPath("$.ingredients").value(
                        org.hamcrest.Matchers.not(org.hamcrest.Matchers.hasItem("SALT"))));
    }

    @Test
    @DisplayName("재료 존재 여부 확인 및 RabbitMQ 메시지 전송 - 성공")
    void checkAndSendMessage_ShouldReturnSuccess() throws Exception {
        // Given: 냉장고에 재료 추가
        IngredientAddRequest request = new IngredientAddRequest(
                10L,
                List.of(IngredientType.EGG, IngredientType.MILK),
                List.of(10, 2),
                List.of(UnitType.PIECE, UnitType.LITER),
                List.of(LocalDate.now().plusDays(7), LocalDate.now().plusDays(5))
        );

        mockMvc.perform(post("/ingredients")
                        .header("X-Member-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].refrigeratorId").value(refrigeratorId))
                .andExpect(jsonPath("$[0].type").value("EGG"))
                .andExpect(jsonPath("$[0].quantity").value(10))
                .andExpect(jsonPath("$[0].unitType").value("PIECE"))
                .andExpect(jsonPath("$[0].expiredDate").exists())
                .andExpect(jsonPath("$[1].type").value("MILK"))
                .andExpect(jsonPath("$[1].quantity").value(2))
                .andExpect(jsonPath("$[1].unitType").value("LITER"))
                .andExpect(jsonPath("$[1].expiredDate").exists());

        CheckAndSendMessageRequest request2 = new CheckAndSendMessageRequest(
                10L,
                List.of(IngredientType.EGG, IngredientType.MILK)
        );

        // When & Then
        mockMvc.perform(post("/ingredients/recipeRecommend")
                        .header("X-Member-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(10L))
                .andExpect(jsonPath("$.message").value("큐에 메시지가 정상적으로 등록되었습니다."))
                .andExpect(jsonPath("$.ingredients[0]").value("계란"))
                .andExpect(jsonPath("$.ingredients[1]").value("우유"))
                .andExpect(jsonPath("$.quantities[0]").value("10조각"))
                .andExpect(jsonPath("$.quantities[1]").value("2리터"));

        Mockito.verify(rabbitMQProducer, times(1)).sendMessage(
                List.of(IngredientType.EGG.getKoreanName(), IngredientType.MILK.getKoreanName()),
                List.of("10조각", "2리터")
        );
    }

    @Test
    @DisplayName("레시피 선택 및 재료 삭제 - 성공")
    void selectRecipeAndDeleteIngredients_ShouldReturnSuccess() throws Exception {
        // Given: 냉장고에 재료 추가
        IngredientAddRequest request = new IngredientAddRequest(
                10L,
                List.of(IngredientType.EGG, IngredientType.MILK),
                List.of(10, 2),
                List.of(UnitType.COUNT, UnitType.LITER),
                List.of(LocalDate.now().plusDays(7), LocalDate.now().plusDays(5))
        );

        mockMvc.perform(post("/ingredients")
                        .header("X-Member-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].refrigeratorId").value(refrigeratorId))
                .andExpect(jsonPath("$[0].type").value("EGG"))
                .andExpect(jsonPath("$[0].quantity").value(10))
                .andExpect(jsonPath("$[0].unitType").value("COUNT"))
                .andExpect(jsonPath("$[0].expiredDate").exists())
                .andExpect(jsonPath("$[1].type").value("MILK"))
                .andExpect(jsonPath("$[1].quantity").value(2))
                .andExpect(jsonPath("$[1].unitType").value("LITER"))
                .andExpect(jsonPath("$[1].expiredDate").exists());

        RecipeSelectRequest request2 = new RecipeSelectRequest(
                10L,
                List.of("우유 1리터", "계란 2개")
        );

        // When & Then
        mockMvc.perform(patch("/ingredients/recipeSelect")
                        .header("X-Member-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(10L))
                .andExpect(jsonPath("$.message").value("재료 업데이트가 완료되었습니다."));
    }

    @Test
    @DisplayName("레시피 선택 및 재료 삭제 - 실패 (단위 오류)")
    void selectRecipeAndDeleteIngredients_ShouldReturnFail_BecauseOfInvalidUnitType() throws Exception {
        // Given: 냉장고에 재료 추가
        IngredientAddRequest request = new IngredientAddRequest(
                10L,
                List.of(IngredientType.EGG, IngredientType.MILK),
                List.of(10, 2),
                List.of(UnitType.PIECE, UnitType.LITER),
                List.of(LocalDate.now().plusDays(7), LocalDate.now().plusDays(5))
        );

        mockMvc.perform(post("/ingredients")
                        .header("X-Member-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].refrigeratorId").value(refrigeratorId))
                .andExpect(jsonPath("$[0].type").value("EGG"))
                .andExpect(jsonPath("$[0].quantity").value(10))
                .andExpect(jsonPath("$[0].unitType").value("PIECE"))
                .andExpect(jsonPath("$[0].expiredDate").exists())
                .andExpect(jsonPath("$[1].type").value("MILK"))
                .andExpect(jsonPath("$[1].quantity").value(2))
                .andExpect(jsonPath("$[1].unitType").value("LITER"))
                .andExpect(jsonPath("$[1].expiredDate").exists());

        RecipeSelectRequest request2 = new RecipeSelectRequest(
                10L,
                List.of("우유 5리터", "소금 2그램")
        );

        // When & Then
        mockMvc.perform(patch("/ingredients/recipeSelect")
                        .header("X-Member-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("I005"))
                .andExpect(jsonPath("$.errorMessage").value("냉장고에 재료가 부족하거나 단위가 맞지 않습니다."));
    }
}
