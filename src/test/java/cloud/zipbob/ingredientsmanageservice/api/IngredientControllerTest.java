package cloud.zipbob.ingredientsmanageservice.api;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.repository.IngredientRepository;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.*;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.repository.RefrigeratorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

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
    @DisplayName("재료 추가 - 새로운 재료가 성공적으로 추가")
    void addIngredient_ShouldReturnSuccessResponse() throws Exception {
        // Given
        IngredientAddRequest request = new IngredientAddRequest(10L, IngredientType.EGG, 10, UnitType.PIECE, LocalDate.now().plusDays(7));

        // When & Then
        mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refrigeratorId").value(refrigeratorId))
                .andExpect(jsonPath("$.type").value("EGG"))
                .andExpect(jsonPath("$.quantity").value(10));
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refrigeratorId").value(refrigeratorId))
                .andExpect(jsonPath("$.ingredientType").value("MILK"));
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
                .andExpect(jsonPath("$.ingredients").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.hasItem("SALT"))));
    }
}
