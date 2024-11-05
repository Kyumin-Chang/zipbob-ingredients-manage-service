package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.service;

import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.exception.RefrigeratorException;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.exception.RefrigeratorExceptionType;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.repository.RefrigeratorRepository;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorCreateRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response.RefrigeratorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RefrigeratorServiceImpl implements RefrigeratorService {

    private final RefrigeratorRepository refrigeratorRepository;

    @Override
    public RefrigeratorResponse createRefrigerator(RefrigeratorCreateRequest request) {
        Refrigerator refrigerator = request.toEntity();
        if (refrigeratorRepository.findByMemberId(refrigerator.getMemberId()).isPresent()) {
            throw new RefrigeratorException(RefrigeratorExceptionType.ALREADY_EXIST_REFRIGERATOR);
        }
        refrigeratorRepository.save(refrigerator);
        return RefrigeratorResponse.of(refrigerator);
    }

    @Override
    public RefrigeratorResponse deleteRefrigerator(RefrigeratorRequest request) {
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId()).orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        refrigeratorRepository.delete(refrigerator);
        return RefrigeratorResponse.of(refrigerator);
    }
}
