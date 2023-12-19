package backend.sumnail.domain.auth.controller.dto;

import backend.sumnail.domain.auth.controller.dto.request.AuthLoginRequest;
import backend.sumnail.domain.auth.controller.dto.request.AuthRefreshRequest;
import backend.sumnail.domain.auth.controller.dto.response.AuthTokenResponse;
import backend.sumnail.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 소셜 로그인 ( 구글, 카카오 )
     */
    @PostMapping("signin")
    public ResponseEntity<AuthTokenResponse> googleLogin(@RequestBody AuthLoginRequest request) {
        AuthTokenResponse response = authService.signin(request.getProvider(), request.getIdToken());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
