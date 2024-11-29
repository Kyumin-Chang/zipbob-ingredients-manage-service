package cloud.zipbob.ingredientsmanageservice.api;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.repository.IngredientRepository;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.repository.RefrigeratorRepository;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorCreateRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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
class RefrigeratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Long memberIdWithIngredients = 1L;
    private static final Long memberIdWithoutIngredients = 2L;

    @BeforeAll
    static void setUp(@Autowired RefrigeratorRepository refrigeratorRepository, @Autowired IngredientRepository ingredientRepository) {
        Refrigerator refrigeratorWithIngredients = refrigeratorRepository.save(
                Refrigerator.builder()
                        .memberId(memberIdWithIngredients)
                        .build()
        );

        refrigeratorRepository.save(
                Refrigerator.builder()
                        .memberId(memberIdWithoutIngredients)
                        .build()
        );

        Ingredient ingredient = Ingredient.builder()
                .type(IngredientType.EGG)
                .quantity(10)
                .unitType(UnitType.PIECE)
                .addedDate(LocalDate.now())
                .expiredDate(LocalDate.now().plusDays(7))
                .refrigerator(refrigeratorWithIngredients)
                .build();

        ingredientRepository.save(ingredient);
    }

    @Test
    @DisplayName("냉장고 생성 - 요청이 성공하면 200 OK와 냉장고 응답 반환")
    void createRefrigerator_ShouldReturnOkAndRefrigeratorResponse() throws Exception {
        RefrigeratorCreateRequest request = new RefrigeratorCreateRequest(3L);

        mockMvc.perform(post("/refrigerators")
                        .header("X-Member-Id", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(3L));
    }

    @Test
    @DisplayName("냉장고 생성 실패 - 이미 존재하는 멤버 ID로 생성 요청 시 409 Conflict 반환")
    void createRefrigerator_ShouldReturnBadRequest_WhenRefrigeratorAlreadyExists() throws Exception {
        RefrigeratorCreateRequest request = new RefrigeratorCreateRequest(memberIdWithIngredients);

        mockMvc.perform(post("/refrigerators")
                        .header("X-Member-Id", memberIdWithIngredients)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("해당 회원의 냉장고가 이미 존재합니다."));
    }

    @Test
    @DisplayName("냉장고 조회 - 재료가 있는 냉장고를 조회할 때 재료 목록이 포함된다")
    void getRefrigerator_WithIngredients_ShouldReturnRefrigeratorWithIngredients() throws Exception {
        RefrigeratorRequest request = new RefrigeratorRequest(memberIdWithIngredients);

        mockMvc.perform(get("/refrigerators")
                        .header("X-Member-Id", memberIdWithIngredients)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(memberIdWithIngredients))
                .andExpect(jsonPath("$.ingredients[0].type").value("EGG"));
    }

    @Test
    @DisplayName("냉장고 조회 - 재료가 없는 냉장고를 조회할 때 빈 재료 목록을 반환한다")
    void getRefrigerator_WithoutIngredients_ShouldReturnRefrigeratorWithoutIngredients() throws Exception {
        RefrigeratorRequest request = new RefrigeratorRequest(memberIdWithoutIngredients);

        mockMvc.perform(get("/refrigerators")
                        .header("X-Member-Id", memberIdWithoutIngredients)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(memberIdWithoutIngredients))
                .andExpect(jsonPath("$.ingredients").isEmpty());
    }

    @Test
    @DisplayName("냉장고 삭제 - 요청이 성공하면 200 OK와 삭제된 냉장고 응답 반환")
    void deleteRefrigerator_ShouldReturnOkAndDeletedRefrigeratorResponse() throws Exception {
        RefrigeratorRequest request = new RefrigeratorRequest(memberIdWithoutIngredients);

        mockMvc.perform(delete("/refrigerators")
                        .header("X-Member-Id", memberIdWithoutIngredients)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(memberIdWithoutIngredients));

        // 삭제 후 확인
        mockMvc.perform(get("/refrigerators")
                        .header("X-Member-Id", memberIdWithoutIngredients)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("냉장고 조회 실패 - 존재하지 않는 멤버 ID로 조회 시 404 Not Found 반환")
    void getRefrigerator_ShouldReturnNotFound_WhenRefrigeratorNotFound() throws Exception {
        RefrigeratorRequest request = new RefrigeratorRequest(999L);

        mockMvc.perform(get("/refrigerators")
                        .header("X-Member-Id", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("해당 회원의 냉장고가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("냉장고 삭제 실패 - 존재하지 않는 멤버 ID로 삭제 시 404 Not Found 반환")
    void deleteRefrigerator_ShouldReturnNotFound_WhenRefrigeratorNotFound() throws Exception {
        RefrigeratorRequest request = new RefrigeratorRequest(999L);

        mockMvc.perform(delete("/refrigerators")
                        .header("X-Member-Id", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("해당 회원의 냉장고가 존재하지 않습니다."));
    }
}
