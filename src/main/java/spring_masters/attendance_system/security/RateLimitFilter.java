package spring_masters.attendance_system.security;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import spring_masters.attendance_system.exception.RateLimitExceededException;
import spring_masters.attendance_system.service.RateLimitService;
import spring_masters.attendance_system.util.JwtUtil;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Skip rate limiting if disabled
        if (!rateLimitService.isRateLimitEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get client identifier (email from JWT or IP address)
        String identifier = getClientIdentifier(request);

        // Get or create bucket for this identifier
        Bucket bucket = rateLimitService.resolveBucket(identifier);

        // Try to consume 1 token from the bucket
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Request allowed - add rate limit headers
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded - throw exception to be handled by GlobalExceptionHandler
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            throw new RateLimitExceededException(
                    String.format("Too many requests. Please try again in %d seconds.", waitForRefill),
                    waitForRefill);
        }
    }

    /**
     * Extract client identifier from request
     * Priority: JWT email > IP address
     */
    private String getClientIdentifier(HttpServletRequest request) {
        // Try to extract email from JWT token
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String email = JwtUtil.extractEmail(token);
                if (email != null && !email.isEmpty()) {
                    return email;
                }
            } catch (Exception e) {
                // If JWT parsing fails, fall back to IP
            }
        }

        // Fall back to IP address
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }
}
