package com.cosmeticguardian.app.config;

import com.cosmeticguardian.app.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String email = request.getHeader("X-User-Email");
        if (email == null || userRepository.findByEmail(email).isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Требуется аутентификация");
            return false;
        }
        return true;
    }
}