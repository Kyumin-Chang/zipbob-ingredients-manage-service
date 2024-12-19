package cloud.zipbob.ingredientsmanageservice.domain.ingredient.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.repository.IngredientRepository;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.CheckAndSendMessageRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.ExpiredIngredientRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.IngredientAddRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.IngredientRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.UpdateQuantityRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.ExpiredIngredientResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.IngredientAddResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.IngredientDeleteResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.UpdateQuantityResponse;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.repository.RefrigeratorRepository;
import cloud.zipbob.ingredientsmanageservice.global.exception.CustomAuthenticationException;
import cloud.zipbob.ingredientsmanageservice.global.exception.CustomAuthenticationExceptionType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class IngredientServiceImplTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RefrigeratorRepository refrigeratorRepository;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    private Refrigerator refrigerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        refrigerator = Refrigerator.builder()
                .id(1L)
                .memberId(5L)
                .build();

        // 기본 Refrigerator Mock 설정
        when(refrigeratorRepository.findByMemberId(5L)).thenReturn(Optional.of(refrigerator));
    }

    @Test
    @DisplayName("재료 추가 - 냉장고에 새로운 재료들을 추가")
    void addIngredient_ShouldAddMultipleIngredientsToRefrigerator() {
        // Given
        IngredientAddRequest request = new IngredientAddRequest(
                5L,
                List.of(IngredientType.EGG, IngredientType.MILK),
                List.of(10, 2),
                List.of(UnitType.PIECE, UnitType.LITER),
                List.of(LocalDate.now().plusDays(7), LocalDate.now().plusDays(5))
        );

        Ingredient eggIngredient = Ingredient.builder()
                .id(1L)
                .refrigerator(refrigerator)
                .type(IngredientType.EGG)
                .quantity(10)
                .unitType(UnitType.PIECE)
                .expiredDate(request.expiredDates().get(0))
                .build();

        Ingredient milkIngredient = Ingredient.builder()
                .id(2L)
                .refrigerator(refrigerator)
                .type(IngredientType.MILK)
                .quantity(2)
                .unitType(UnitType.LITER)
                .expiredDate(request.expiredDates().get(1))
                .build();

        when(refrigeratorRepository.findByMemberId(5L)).thenReturn(Optional.of(refrigerator));
        when(ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(), IngredientType.EGG))
                .thenReturn(Optional.empty());
        when(ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(), IngredientType.MILK))
                .thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class)))
                .thenReturn(eggIngredient)
                .thenReturn(milkIngredient);

        // When
        List<IngredientAddResponse> responses = ingredientService.addIngredient(request, 5L);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        IngredientAddResponse eggResponse = responses.get(0);
        assertEquals(1L, eggResponse.getRefrigeratorId());
        assertEquals(IngredientType.EGG, eggResponse.getType());
        assertEquals(10, eggResponse.getQuantity());
        assertEquals(UnitType.PIECE, eggResponse.getUnitType());

        IngredientAddResponse milkResponse = responses.get(1);
        assertEquals(1L, milkResponse.getRefrigeratorId());
        assertEquals(IngredientType.MILK, milkResponse.getType());
        assertEquals(2, milkResponse.getQuantity());
        assertEquals(UnitType.LITER, milkResponse.getUnitType());

        verify(ingredientRepository, times(2)).save(any(Ingredient.class));
    }


    @Test
    @DisplayName("재료 삭제 - 냉장고에서 특정 재료 삭제")
    void deleteIngredient_ShouldDeleteIngredientFromRefrigerator() {
        // Given
        Ingredient ingredient = Ingredient.builder()
                .id(1L)
                .type(IngredientType.MILK)
                .quantity(1)
                .unitType(UnitType.LITER)
                .refrigerator(refrigerator)
                .expiredDate(LocalDate.now().plusDays(5))
                .build();

        IngredientRequest request = new IngredientRequest(5L, IngredientType.MILK);

        when(ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(), IngredientType.MILK))
                .thenReturn(Optional.of(ingredient));

        // When
        IngredientDeleteResponse response = ingredientService.deleteIngredient(request, 5L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getRefrigeratorId());
        assertEquals(IngredientType.MILK, response.getIngredientType());
        verify(ingredientRepository).delete(ingredient);
    }

    @Test
    @DisplayName("재료 수량 업데이트 - 특정 재료의 수량을 업데이트")
    void updateQuantity_ShouldUpdateIngredientQuantity() {
        // Given
        Ingredient ingredient = Ingredient.builder()
                .id(1L)
                .type(IngredientType.CARROT)
                .quantity(5)
                .unitType(UnitType.PIECE)
                .refrigerator(refrigerator)
                .build();

        UpdateQuantityRequest request = new UpdateQuantityRequest(5L, IngredientType.CARROT, 10);

        when(ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(), IngredientType.CARROT))
                .thenReturn(Optional.of(ingredient));

        // When
        UpdateQuantityResponse response = ingredientService.updateQuantity(request, 5L);

        // Then
        assertNotNull(response);
        assertEquals(5L, response.getMemberId());
        assertEquals(IngredientType.CARROT, response.getIngredientType());
        assertEquals(10, response.getQuantity());
        assertEquals(10, ingredient.getQuantity());
    }

    @Test
    @DisplayName("유통기한 지난 재료 조회 - 냉장고의 유통기한 지난 재료 조회")
    void getExpiredIngredients_ShouldReturnExpiredIngredients() {
        // Given
        Ingredient expiredIngredient = Ingredient.builder()
                .type(IngredientType.BEEF)
                .quantity(2)
                .unitType(UnitType.KILOGRAM)
                .expiredDate(LocalDate.now().minusDays(1))
                .refrigerator(refrigerator)
                .build();

        Ingredient validIngredient = Ingredient.builder()
                .type(IngredientType.PORK)
                .quantity(3)
                .unitType(UnitType.KILOGRAM)
                .expiredDate(LocalDate.now().plusDays(5))
                .refrigerator(refrigerator)
                .build();

        when(ingredientRepository.findByRefrigeratorId(refrigerator.getId()))
                .thenReturn(List.of(expiredIngredient, validIngredient));

        ExpiredIngredientRequest request = new ExpiredIngredientRequest(5L);

        // When
        ExpiredIngredientResponse response = ingredientService.getExpiredIngredients(request, 5L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getRefrigeratorId());
        assertEquals(1, response.getExpiredIngredients().size());
        assertEquals(IngredientType.BEEF, response.getExpiredIngredients().get(0).getType());
    }

    @Test
    @DisplayName("멤버 검증 실패 - 인증된 멤버 ID와 요청 멤버 ID가 다른 경우 예외 발생")
    void validationMember_ShouldThrowAuthenticationException() {
        // Given
        CheckAndSendMessageRequest request = new CheckAndSendMessageRequest(6L, List.of(IngredientType.EGG));

        // When & Then
        CustomAuthenticationException exception = assertThrows(CustomAuthenticationException.class,
                () -> ingredientService.checkAndSendMessage(request, 5L));
        assertEquals(CustomAuthenticationExceptionType.AUTHENTICATION_DENIED, exception.getExceptionType());
    }
}
