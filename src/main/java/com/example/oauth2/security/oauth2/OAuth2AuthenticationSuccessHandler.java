package com.example.oauth2.security.oauth2;

import com.example.oauth2.config.AppProperties;
import com.example.oauth2.exception.BadRequestException;
import com.example.oauth2.model.User;
import com.example.oauth2.repository.UserRepository;
import com.example.oauth2.security.TokenProvider;
import com.example.oauth2.token.Token;
import com.example.oauth2.token.TokenService;
import com.example.oauth2.token.TokenType;
import com.example.oauth2.util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.example.oauth2.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

/**
 * Lớp xử lý khi xác thực OAuth2 thành công, xác định URL điều hướng tới sau khi xác thực.
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private UserRepository userRepository;
   private final TokenService tokenService;
    private final TokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    /**
     * Tạo lớp xử lý xác thực thành công bằng cách inject các phụ thuộc cần thiết.
     *
     * @param tokenService
     * @param tokenProvider                                  Provider tạo token.
     * @param appProperties                                  Cấu hình OAuth2 của ứng dụng.
     * @param httpCookieOAuth2AuthorizationRequestRepository Xử lý cookie OAuth2 authorization request.
     */
    @Autowired
    public OAuth2AuthenticationSuccessHandler(TokenService tokenService, TokenProvider tokenProvider, AppProperties appProperties,
                                              HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.tokenService = tokenService;
        this.tokenProvider = tokenProvider;
        this.appProperties = appProperties;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    /**
     * Xử lý khi người dùng xác thực thành công.
     *
     * @param request        HTTP request
     * @param response       HTTP response
     * @param authentication Authentication object chứa thông tin xác thực người dùng
     * @throws IOException      Lỗi khi gửi yêu cầu HTTP
     * @throws ServletException Lỗi khi xử lý Servlet
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Xác định URL điều hướng sau xác thực thành công.
        String targetUrl = determineTargetUrl(request, response, authentication);

        // Kiểm tra xem response đã được điều hướng hay chưa.
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        // Xóa các attribute xác thực của người dùng.
        clearAuthenticationAttributes(request, response);

        // Điều hướng đến URL xác định.
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Xác định URL điều hướng sau khi xác thực thành công, và thêm token vào URL.
     *
     * @param request        HTTP request
     * @param response       HTTP response
     * @param authentication Authentication object chứa thông tin xác thực người dùng
     * @return URL điều hướng với token xác thực.
     */
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Lấy redirect URI từ cookie.
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        // Kiểm tra URL điều hướng đã được ủy quyền chưa.
        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        // Đặt URL điều hướng và tạo token xác thực.
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        String token = tokenProvider.createToken(authentication);
        User user = userRepository.findByEmail(authentication.getName()).get();
        tokenService.saveUserToken(user,token);
//        CookieUtils.addCookie(response, "token", token, 7200);
        // Trả về URL điều hướng với token xác thực.
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    /**
     * Xóa các attribute xác thực của người dùng và xóa cookie authorization request.
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    /**
     * Kiểm tra xem URL redirect được cung cấp có được ủy quyền hay không.
     *
     * @param uri URL redirect.
     * @return True nếu URL được ủy quyền, false nếu không.
     */
    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }


}
