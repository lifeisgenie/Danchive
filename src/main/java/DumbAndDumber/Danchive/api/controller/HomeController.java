package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.home.HomeResponse;
import DumbAndDumber.Danchive.api.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<HomeResponse>> getHome() {
        HomeResponse payload = homeService.fetchHomeData();
        return ResponseEntity.ok(ApiResponse.success("홈 데이터 조회 성공", payload));
    }
}
