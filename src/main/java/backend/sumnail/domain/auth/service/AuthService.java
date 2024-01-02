package backend.sumnail.domain.auth.service;

import backend.sumnail.domain.auth.controller.dto.AuthKakaoLoginDto;
import backend.sumnail.domain.auth.controller.dto.response.AuthTokenResponse;
import backend.sumnail.domain.auth.entity.Provider;
import backend.sumnail.domain.auth.service.helper.GoogleClient;
import backend.sumnail.domain.auth.service.helper.KakaoClient;
import backend.sumnail.domain.refresh_token.entity.RefreshToken;
import backend.sumnail.domain.refresh_token.service.RefreshTokenService;
import backend.sumnail.domain.user.entity.User;
import backend.sumnail.domain.user.repository.UserRepository;
import backend.sumnail.global.config.jwt.JwtTokenProvider;
import backend.sumnail.global.exception.CustomException;
import backend.sumnail.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleClient googleClient;
    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthTokenResponse signIn(String provider, String idToken) {
        User user = signInByProvider(provider, idToken);

        User findUser = userRepository.findByEmail(user.getEmail()).
                orElse(null);

        if (findUser == null) { // 최초 로그인이라면 회원가입 시키기
            userRepository.save(user);
        }

        return createAndSaveToken(user);

    }


    public AuthTokenResponse refresh(String token) {
        String refreshToken = token.replace("Bearer", "");

        //refreshToken 유효성 확인
        jwtTokenProvider.validateRefreshToken(refreshToken);

        // userId 는 userPK
        Long userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);

        RefreshToken findRefreshToken = refreshTokenService.getByKeyUserId(userId);

        if (!refreshToken.equals(findRefreshToken.getRefreshToken())) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        User user = userRepository.getById(userId);
        return createAndSaveToken(user);
    }


    private User signInByProvider(String provider, String idToken) {
        if (!provider.equals(Provider.GOOGLE.getProviderName()) && !provider.equals(Provider.KAKAO.getProviderName())) {
            throw new CustomException(ErrorCode.INVALID_PROVIDER_NAME);
        }

        // 구글 로그인
        if (Provider.GOOGLE.getProviderName().equals(provider)) {
            System.out.println("?????"+googleClient.getUserInfo(idToken).getEmail());
            return User.createUserByGoogleLogin(googleClient.getUserInfo(idToken));
        }

        // 카카오 로그인
        AuthKakaoLoginDto userInfo = kakaoClient.getUserInfo("Bearer "+ idToken);
        //System.out.println(userInfo.getKakaoAccount().getProfile()+"!!!!!!"+userInfo.getKakaoAccount().getProfile().getNickname()+userInfo.getKakaoAccount().getEmail());
        return User.createUserByKakaoLogin(userInfo);
    }


    private AuthTokenResponse createAndSaveToken(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        refreshTokenService.saveRefreshToken(refreshToken, user.getId());

        return AuthTokenResponse.of(accessToken, refreshToken);
    }


}