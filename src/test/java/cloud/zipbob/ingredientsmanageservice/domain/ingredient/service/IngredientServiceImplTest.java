package cloud.zipbob.ingredientsmanageservice.domain.ingredient.service;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.repository.IngredientRepository;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.*;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.*;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.repository.RefrigeratorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IngredientServiceImplTest {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    private Refrigerator refrigerator;

    @BeforeEach
    void setUp() {
        refrigerator = refrigeratorRepository.save(
                Refrigerator.builder()
                        .memberId(5L)
                        .build()
        );
    }

    @Test
    @DisplayName("재료 추가 - 냉장고에 새로운 재료를 추가하면 저장")
    void addIngredient_ShouldAddIngredientToRefrigerator() throws AccessDeniedException {
        // Given
        IngredientAddRequest request = new IngredientAddRequest(5L, IngredientType.EGG, 10, UnitType.PIECE, LocalDate.now().plusDays(7));

        // When
        IngredientAddResponse response = ingredientService.addIngredient(request, 5L);

        // Then
        assertNotNull(response);
        assertEquals(refrigerator.getId(), response.getRefrigeratorId());
        assertEquals(IngredientType.EGG, response.getType());
        assertEquals(10, response.getQuantity());
    }

    @Test
    @DisplayName("재료 삭제 - 냉장고에서 특정 재료를 삭제")
    void deleteIngredient_ShouldDeleteIngredientFromRefrigerator() {
        // Given
        Ingredient ingredient = ingredientRepository.save(
                Ingredient.builder()
                        .type(IngredientType.MILK)
                        .quantity(1)
                        .unitType(UnitType.LITER)
                        .expiredDate(LocalDate.now().plusDays(5))
                        .refrigerator(refrigerator)
                        .build()
        );
        IngredientRequest request = new IngredientRequest(5L, IngredientType.MILK);

        // When
        IngredientDeleteResponse response = ingredientService.deleteIngredient(request, 5L);

        // Then
        assertNotNull(response);
        assertEquals(refrigerator.getId(), response.getRefrigeratorId());
        assertEquals(IngredientType.MILK, response.getIngredientType());
        assertFalse(ingredientRepository.findById(ingredient.getId()).isPresent());
    }

    @Test
    @DisplayName("재료 수량 업데이트 - 냉장고의 특정 재료 수량을 업데이트")
    void updateQuantity_ShouldUpdateIngredientQuantity() {
        // Given
        ingredientRepository.save(
                Ingredient.builder()
                        .type(IngredientType.CARROT)
                        .quantity(5)
                        .unitType(UnitType.PIECE)
                        .expiredDate(LocalDate.now().plusDays(3))
                        .refrigerator(refrigerator)
                        .build()
        );
        UpdateQuantityRequest request = new UpdateQuantityRequest(5L, IngredientType.CARROT, 10);

        // When
        UpdateQuantityResponse response = ingredientService.updateQuantity(request, 5L);

        // Then
        assertNotNull(response);
        assertEquals(5L, response.getMemberId());
        assertEquals(IngredientType.CARROT, response.getIngredientType());
        assertEquals(10, response.getQuantity());
    }

    @Test
    @DisplayName("유통기한 지난 재료 조회 - 냉장고에서 유통기한이 지난 재료를 조회")
    void getExpiredIngredients_ShouldReturnExpiredIngredients() {
        // Given
        ingredientRepository.save(
                Ingredient.builder()
                        .type(IngredientType.BEEF)
                        .quantity(2)
                        .unitType(UnitType.KILOGRAM)
                        .expiredDate(LocalDate.now().minusDays(1))
                        .refrigerator(refrigerator)
                        .build()
        );
        ingredientRepository.save(
                Ingredient.builder()
                        .type(IngredientType.PORK)
                        .quantity(3)
                        .unitType(UnitType.KILOGRAM)
                        .expiredDate(LocalDate.now().plusDays(5))
                        .refrigerator(refrigerator)
                        .build()
        );
        ExpiredIngredientRequest request = new ExpiredIngredientRequest(5L);

        // When
        ExpiredIngredientResponse response = ingredientService.getExpiredIngredients(request, 5L);

        // Then
        assertNotNull(response);
        assertEquals(refrigerator.getId(), response.getRefrigeratorId());
        assertEquals(1, response.getExpiredIngredients().size());
        assertEquals(IngredientType.BEEF, response.getExpiredIngredients().get(0).getType());
    }

    @Test
    @DisplayName("카테고리별 재료 조회 - 특정 카테고리에 속하는 재료 목록을 반환")
    void getIngredientsByType_ShouldReturnIngredientsByCategory() {
        // Given
        GetIngredientsByTypeRequest request = new GetIngredientsByTypeRequest(IngredientType.Category.MAIN);

        // When
        GetIngredientsByTypeResponse response = ingredientService.getIngredientsByType(request);

        // Then
        assertNotNull(response);
        assertTrue(response.getIngredients().contains(IngredientType.EGG));
        assertTrue(response.getIngredients().contains(IngredientType.CHICKEN));
        assertFalse(response.getIngredients().contains(IngredientType.SALT));
    }
}
