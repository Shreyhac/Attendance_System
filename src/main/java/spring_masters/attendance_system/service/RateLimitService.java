package spring_masters.attendance_system.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Value("${app.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${app.rate-limit.capacity:100}")
    private long capacity;

    @Value("${app.rate-limit.refill-tokens:100}")
    private long refillTokens;

    @Value("${app.rate-limit.refill-duration-minutes:1}")
    private long refillDurationMinutes;

    /**
     * Resolve a bucket for the given key (user email or IP address)
     */
    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }

    /**
     * Create a new bucket with configured limits
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(refillTokens, Duration.ofMinutes(refillDurationMinutes))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Check if rate limiting is enabled
     */
    public boolean isRateLimitEnabled() {
        return rateLimitEnabled;
    }

    /**
     * Get current configuration info
     */
    public String getConfigInfo() {
        return String.format("Rate Limit: %d requests per %d minute(s)",
                capacity, refillDurationMinutes);
    }
}
