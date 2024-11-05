package cloud.zipbob.ingredientsmanageservice.api;

import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorCreateRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response.RefrigeratorResponse;
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

    @DeleteMapping("")
    public ResponseEntity<RefrigeratorResponse> deleteRefrigerator(final @RequestBody RefrigeratorRequest request) {
        RefrigeratorResponse response = refrigeratorService.deleteRefrigerator(request);
        return Responder.success(response);
    }
}
