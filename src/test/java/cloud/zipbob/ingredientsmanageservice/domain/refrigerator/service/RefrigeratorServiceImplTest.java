package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.service;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.repository.IngredientRepository;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.exception.RefrigeratorException;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.exception.RefrigeratorExceptionType;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.repository.RefrigeratorRepository;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorCreateRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response.RefrigeratorResponse;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response.RefrigeratorWithIngredientsResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RefrigeratorServiceImplTest {

    @Autowired
    private RefrigeratorService refrigeratorService;

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

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
    @DisplayName("냉장고 생성 - 새로운 멤버 ID로 냉장고가 정상적으로 생성")
    void createRefrigerator_ShouldSaveRefrigeratorSuccessfully() {
        // Given
        RefrigeratorCreateRequest request = new RefrigeratorCreateRequest(3L);

        // When
        RefrigeratorResponse response = refrigeratorService.createRefrigerator(request, 3L);

        // Then
        Refrigerator saved = refrigeratorRepository.findById(response.getRefrigeratorId()).orElse(null);
        assertNotNull(saved);
        assertEquals(3L, saved.getMemberId());
    }

    @Test
    @DisplayName("냉장고 생성 실패 - 이미 존재하는 멤버 ID로 생성 요청 시 예외 발생")
    void createRefrigerator_ShouldThrowException_WhenRefrigeratorAlreadyExists() {
        // Given
        RefrigeratorCreateRequest request = new RefrigeratorCreateRequest(memberIdWithIngredients);

        // When & Then
        RefrigeratorException exception = assertThrows(RefrigeratorException.class, () ->
                refrigeratorService.createRefrigerator(request, 1L)
        );

        assertEquals(RefrigeratorExceptionType.ALREADY_EXIST_REFRIGERATOR, exception.getExceptionType());
    }

    @Test
    @DisplayName("냉장고 조회 - 재료가 있는 냉장고를 조회할 때 재료 목록이 포함")
    void getRefrigerator_WithIngredients_ShouldReturnRefrigeratorWithIngredients() {
        // Given
        RefrigeratorRequest request = new RefrigeratorRequest(memberIdWithIngredients);

        // When
        RefrigeratorWithIngredientsResponse response = refrigeratorService.getRefrigerator(request, 1L);

        // Then
        assertEquals(memberIdWithIngredients, response.getMemberId());
        assertNotNull(response.getIngredients());
        assertEquals(1, response.getIngredients().size());
        assertEquals(IngredientType.EGG, response.getIngredients().get(0).getType());
    }

    @Test
    @DisplayName("냉장고 조회 - 재료가 없는 냉장고를 조회할 때 빈 재료 목록을 반환")
    void getRefrigerator_WithoutIngredients_ShouldReturnRefrigeratorWithoutIngredients() {
        // Given
        RefrigeratorRequest request = new RefrigeratorRequest(memberIdWithoutIngredients);

        // When
        RefrigeratorWithIngredientsResponse response = refrigeratorService.getRefrigerator(request, 2L);

        // Then
        assertEquals(memberIdWithoutIngredients, response.getMemberId());
        assertNotNull(response.getIngredients());
        assertTrue(response.getIngredients().isEmpty());
    }

    @Test
    @DisplayName("냉장고 삭제 - 멤버 ID로 냉장고를 정상적으로 삭제")
    void deleteRefrigerator_ShouldDeleteRefrigeratorSuccessfully() {
        // Given
        RefrigeratorRequest request = new RefrigeratorRequest(memberIdWithoutIngredients);

        // When
        RefrigeratorResponse response = refrigeratorService.deleteRefrigerator(request, 2L);

        // Then
        assertFalse(refrigeratorRepository.existsById(response.getRefrigeratorId()));
        assertEquals(memberIdWithoutIngredients, response.getMemberId());
    }

    @Test
    @DisplayName("냉장고 조회 실패 - 존재하지 않는 멤버 ID로 조회 시 예외 발생")
    void getRefrigerator_ShouldThrowException_WhenRefrigeratorNotFound() {
        // Given
        RefrigeratorRequest request = new RefrigeratorRequest(999L);

        // When & Then
        RefrigeratorException exception = assertThrows(RefrigeratorException.class, () ->
                refrigeratorService.getRefrigerator(request, 999L)
        );

        assertEquals(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND, exception.getExceptionType());
    }

    @Test
    @DisplayName("냉장고 삭제 실패 - 존재하지 않는 멤버 ID로 삭제 시 예외 발생")
    void deleteRefrigerator_ShouldThrowException_WhenRefrigeratorNotFound() {
        // Given
        RefrigeratorRequest request = new RefrigeratorRequest(999L);

        // When & Then
        RefrigeratorException exception = assertThrows(RefrigeratorException.class, () ->
                refrigeratorService.deleteRefrigerator(request, 999L)
        );

        assertEquals(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND, exception.getExceptionType());
    }
}
