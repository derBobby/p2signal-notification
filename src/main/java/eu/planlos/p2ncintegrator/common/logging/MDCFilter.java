package eu.planlos.p2ncintegrator.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class MDCFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Set MDC context data here
            MDC.put("requestUUID", UUID.randomUUID().toString());
            filterChain.doFilter(request, response);
        } finally {
            // Clear MDC context data after processing the request
            MDC.clear();
        }
    }
}