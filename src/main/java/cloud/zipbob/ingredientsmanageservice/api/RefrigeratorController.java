package cloud.zipbob.ingredientsmanageservice.api;

import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorCreateRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response.RefrigeratorResponse;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response.RefrigeratorWithIngredientsResponse;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.service.RefrigeratorService;
import cloud.zipbob.ingredientsmanageservice.global.Responder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/refrigerators")
@RequiredArgsConstructor
public class RefrigeratorController {
    private final RefrigeratorService refrigeratorService;

    @PostMapping("")
    public ResponseEntity<RefrigeratorResponse> createRefrigerator(final @RequestBody RefrigeratorCreateRequest request) {
        RefrigeratorResponse response = refrigeratorService.createRefrigerator(request);
        return Responder.success(response);
    }

    @GetMapping("")
    public ResponseEntity<RefrigeratorWithIngredientsResponse> getRefrigerator(final @RequestBody RefrigeratorRequest request) {
        RefrigeratorWithIngredientsResponse response = refrigeratorService.getRefrigerator(request);
        return Responder.success(response);
    }

    @DeleteMapping("")
    public ResponseEntity<RefrigeratorResponse> deleteRefrigerator(final @RequestBody RefrigeratorRequest request) {
        RefrigeratorResponse response = refrigeratorService.deleteRefrigerator(request);
        return Responder.success(response);
    }
    // TODO 레시피 추천 api 제작하기 ( 재료 있는 지에 대한 여부 확인 및 rabbitmq에 올리기 )
}
