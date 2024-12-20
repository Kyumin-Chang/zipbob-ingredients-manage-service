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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RefrigeratorServiceImpl implements RefrigeratorService {

    private final RefrigeratorRepository refrigeratorRepository;

    @Override
    @Transactional
    public RefrigeratorResponse createRefrigerator(RefrigeratorCreateRequest request, Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = request.toEntity();
        if (refrigeratorRepository.findByMemberId(refrigerator.getMemberId()).isPresent()) {
            throw new RefrigeratorException(RefrigeratorExceptionType.ALREADY_EXIST_REFRIGERATOR);
        }
        refrigeratorRepository.save(refrigerator);
        return RefrigeratorResponse.of(refrigerator);
    }

    @Override
    @Transactional(readOnly = true)
    public RefrigeratorWithIngredientsResponse getRefrigerator(RefrigeratorRequest request, Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId()).orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        return RefrigeratorWithIngredientsResponse.of(refrigerator);
    }

    @Override
    @Transactional
    public RefrigeratorResponse deleteRefrigerator(RefrigeratorRequest request, Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId()).orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        refrigeratorRepository.delete(refrigerator);
        return RefrigeratorResponse.of(refrigerator);
    }

    private void validationMember(Long memberId, Long authenticatedMemberId) {
        if (!Objects.equals(memberId, authenticatedMemberId)) {
            throw new CustomAuthenticationException(CustomAuthenticationExceptionType.AUTHENTICATION_DENIED);
        }
    }
}
