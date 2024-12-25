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
    public ResponseEntity<RefrigeratorResponse> createRefrigerator(final @RequestBody RefrigeratorCreateRequest request, @RequestHeader("X-Member-Id") Long authenticatedMemberId) {
        RefrigeratorResponse response = refrigeratorService.createRefrigerator(request, authenticatedMemberId);
        return Responder.success(response);
    }

    @GetMapping("")
    public ResponseEntity<RefrigeratorWithIngredientsResponse> getRefrigerator(@RequestParam Long memberId, @RequestHeader("X-Member-Id") Long authenticatedMemberId) {
        RefrigeratorWithIngredientsResponse response = refrigeratorService.getRefrigerator(memberId, authenticatedMemberId);
        return Responder.success(response);
    }

    @DeleteMapping("")
    public ResponseEntity<RefrigeratorResponse> deleteRefrigerator(final @RequestBody RefrigeratorRequest request, @RequestHeader("X-Member-Id") Long authenticatedMemberId) {
        RefrigeratorResponse response = refrigeratorService.deleteRefrigerator(request, authenticatedMemberId);
        return Responder.success(response);
    }
}
