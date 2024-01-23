package com.jpcc.CFBProject.security.securityutil;

import com.jpcc.CFBProject.security.securityservices.JwtServiceImpl;
import com.jpcc.CFBProject.security.securityservices.RefreshTokenService;
import com.jpcc.CFBProject.security.securityservices.UserServiceImpl;
import com.jpcc.CFBProject.request.RefreshTokenRequest;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtServiceImpl jwtService;
    private final UserServiceImpl userService;
    private final RefreshTokenService refreshTokenService;
    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtServiceImpl jwtService, UserServiceImpl userService,
                                   RefreshTokenService refreshTokenService) {
        super();
        this.jwtService = jwtService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        Cookie accessTokenCookie = null;
        Cookie refreshTokenCookie = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    accessTokenCookie = cookie;
                } else if (cookie.getName().equals("refreshToken")) {
                    refreshTokenCookie = cookie;
                }
            }
        }

        if (accessTokenCookie != null) {

            int loginAttempt = 0;

            while (loginAttempt <= 8) {
                String token = accessTokenCookie.getValue();

                try {
                    String subject = jwtService.extractUserName(token);
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    if (StringUtils.hasText(subject) && authentication == null) {
                        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(subject);

                        if (jwtService.isTokenValid(token, userDetails)) {
                            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                                    userDetails.getPassword(),
                                    userDetails.getAuthorities());
                            securityContext.setAuthentication(authToken);
                            SecurityContextHolder.setContext(securityContext);

                            // if successful login occurs:
                            break;
                        }
                    }
                } catch (ExpiredJwtException e) {
                    try {
                        token = refreshTokenService.createNewAccessToken(new RefreshTokenRequest(refreshTokenCookie.getValue()));
                        accessTokenCookie = CookieUtils.createAccessTokenCookie(token);

                        response.addCookie(accessTokenCookie);
//                        e.printStackTrace();
                    } catch (Exception e1) {

//                        e1.printStackTrace();
                    }
                }
                loginAttempt++;
            }
        }
        logger.debug("Request URI: {}", request.getRequestURI());
        logger.debug("Auth Header: {}", authHeader);
        logger.debug("Access Token Cookie: {}", accessTokenCookie);
        logger.debug("Refresh Token Cookie: {}", refreshTokenCookie);

        filterChain.doFilter(request, response);

    }
}