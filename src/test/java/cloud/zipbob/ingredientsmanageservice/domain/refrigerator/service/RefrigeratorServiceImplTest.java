package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.service;

import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.exception.RefrigeratorException;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.exception.RefrigeratorExceptionType;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.repository.RefrigeratorRepository;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorCreateRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response.RefrigeratorResponse;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response.RefrigeratorWithIngredientsResponse;
import cloud.zipbob.ingredientsmanageservice.global.exception.CustomAuthenticationException;
import cloud.zipbob.ingredientsmanageservice.global.exception.CustomAuthenticationExceptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefrigeratorServiceImplTest {

    @Mock
    private RefrigeratorRepository refrigeratorRepository;

    @InjectMocks
    private RefrigeratorServiceImpl refrigeratorService;

    private Refrigerator refrigeratorWithIngredients;
    private Refrigerator refrigeratorWithoutIngredients;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        refrigeratorWithIngredients = Refrigerator.builder()
                .id(1L)
                .memberId(1L)
                .build();

        refrigeratorWithoutIngredients = Refrigerator.builder()
                .id(2L)
                .memberId(2L)
                .build();

        when(refrigeratorRepository.findByMemberId(1L))
                .thenReturn(Optional.of(refrigeratorWithIngredients));
        when(refrigeratorRepository.findByMemberId(2L))
                .thenReturn(Optional.of(refrigeratorWithoutIngredients));
    }

    @Test
    @DisplayName("냉장고 생성 - 새로운 멤버 ID로 냉장고가 정상적으로 생성")
    void createRefrigerator_ShouldSaveRefrigeratorSuccessfully() {
        // Given
        RefrigeratorCreateRequest request = new RefrigeratorCreateRequest(3L);
        Refrigerator newRefrigerator = Refrigerator.builder()
                .id(3L)
                .memberId(3L)
                .build();

        when(refrigeratorRepository.findByMemberId(3L)).thenReturn(Optional.empty());
        when(refrigeratorRepository.save(any(Refrigerator.class))).thenReturn(newRefrigerator);

        // When
        RefrigeratorResponse response = refrigeratorService.createRefrigerator(request, 3L);

        // Then
        assertNotNull(response);
        assertEquals(3L, response.getMemberId());
        verify(refrigeratorRepository).save(any(Refrigerator.class));
    }

    @Test
    @DisplayName("냉장고 생성 실패 - 이미 존재하는 멤버 ID로 생성 요청 시 예외 발생")
    void createRefrigerator_ShouldThrowException_WhenRefrigeratorAlreadyExists() {
        // Given
        RefrigeratorCreateRequest request = new RefrigeratorCreateRequest(1L);

        // When & Then
        RefrigeratorException exception = assertThrows(RefrigeratorException.class, () ->
                refrigeratorService.createRefrigerator(request, 1L)
        );

        assertEquals(RefrigeratorExceptionType.ALREADY_EXIST_REFRIGERATOR, exception.getExceptionType());
    }

    @Test
    @DisplayName("냉장고 조회 - 재료가 있는 냉장고를 조회할 때 재료 목록이 포함")
    void getRefrigerator_WithIngredients_ShouldReturnRefrigeratorWithIngredients() {
        // When
        RefrigeratorWithIngredientsResponse response = refrigeratorService.getRefrigerator(1L, 1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getMemberId());
        assertEquals(1L, response.getRefrigeratorId());
    }

    @Test
    @DisplayName("냉장고 조회 - 재료가 없는 냉장고를 조회할 때 빈 재료 목록을 반환")
    void getRefrigerator_WithoutIngredients_ShouldReturnRefrigeratorWithoutIngredients() {
        // When
        RefrigeratorWithIngredientsResponse response = refrigeratorService.getRefrigerator(2L, 2L);

        // Then
        assertNotNull(response);
        assertEquals(2L, response.getMemberId());
        assertEquals(2L, response.getRefrigeratorId());
    }

    @Test
    @DisplayName("냉장고 삭제 - 멤버 ID로 냉장고를 정상적으로 삭제")
    void deleteRefrigerator_ShouldDeleteRefrigeratorSuccessfully() {
        // Given
        RefrigeratorRequest request = new RefrigeratorRequest(2L);

        // When
        RefrigeratorResponse response = refrigeratorService.deleteRefrigerator(request, 2L);

        // Then
        assertNotNull(response);
        assertEquals(2L, response.getMemberId());
        verify(refrigeratorRepository).delete(refrigeratorWithoutIngredients);
    }

    @Test
    @DisplayName("냉장고 조회 실패 - 존재하지 않는 멤버 ID로 조회 시 예외 발생")
    void getRefrigerator_ShouldThrowException_WhenRefrigeratorNotFound() {
        when(refrigeratorRepository.findByMemberId(999L)).thenReturn(Optional.empty());

        // When & Then
        RefrigeratorException exception = assertThrows(RefrigeratorException.class, () ->
                refrigeratorService.getRefrigerator(999L, 999L)
        );

        assertEquals(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND, exception.getExceptionType());
    }

    @Test
    @DisplayName("냉장고 삭제 실패 - 존재하지 않는 멤버 ID로 삭제 시 예외 발생")
    void deleteRefrigerator_ShouldThrowException_WhenRefrigeratorNotFound() {
        // Given
        RefrigeratorRequest request = new RefrigeratorRequest(999L);

        when(refrigeratorRepository.findByMemberId(999L)).thenReturn(Optional.empty());

        // When & Then
        RefrigeratorException exception = assertThrows(RefrigeratorException.class, () ->
                refrigeratorService.deleteRefrigerator(request, 999L)
        );

        assertEquals(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND, exception.getExceptionType());
    }

    @Test
    @DisplayName("멤버 검증 실패 - 인증된 멤버 ID와 요청 멤버 ID가 다른 경우 예외 발생")
    void validationMember_ShouldThrowAuthenticationException() {
        // When & Then
        CustomAuthenticationException exception = assertThrows(CustomAuthenticationException.class, () ->
                refrigeratorService.getRefrigerator(5L, 1L)
        );

        assertEquals(CustomAuthenticationExceptionType.AUTHENTICATION_DENIED, exception.getExceptionType());
    }
}
