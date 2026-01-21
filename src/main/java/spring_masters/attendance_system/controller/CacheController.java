package spring_masters.attendance_system.controller;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/cache")
public class CacheController {

    @Autowired
    private CacheManager cacheManager;

    /**
     * Get cache statistics for all caches
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> allStats = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache instanceof CaffeineCache) {
                com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = ((CaffeineCache) cache)
                        .getNativeCache();
                CacheStats stats = nativeCache.stats();

                Map<String, Object> cacheInfo = new HashMap<>();
                cacheInfo.put("hitCount", stats.hitCount());
                cacheInfo.put("missCount", stats.missCount());
                cacheInfo.put("hitRate", stats.hitRate());
                cacheInfo.put("evictionCount", stats.evictionCount());
                cacheInfo.put("estimatedSize", nativeCache.estimatedSize());

                allStats.put(cacheName, cacheInfo);
            }
        });

        return ResponseEntity.ok(allStats);
    }

    /**
     * Get statistics for a specific cache
     */
    @GetMapping("/stats/{cacheName}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Map<String, Object>> getCacheStatsByName(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);

        if (cache == null) {
            return ResponseEntity.notFound().build();
        }

        if (cache instanceof CaffeineCache) {
            com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = ((CaffeineCache) cache)
                    .getNativeCache();
            CacheStats stats = nativeCache.stats();

            Map<String, Object> cacheInfo = new HashMap<>();
            cacheInfo.put("cacheName", cacheName);
            cacheInfo.put("hitCount", stats.hitCount());
            cacheInfo.put("missCount", stats.missCount());
            cacheInfo.put("hitRate", stats.hitRate());
            cacheInfo.put("missRate", stats.missRate());
            cacheInfo.put("loadSuccessCount", stats.loadSuccessCount());
            cacheInfo.put("loadFailureCount", stats.loadFailureCount());
            cacheInfo.put("evictionCount", stats.evictionCount());
            cacheInfo.put("estimatedSize", nativeCache.estimatedSize());

            return ResponseEntity.ok(cacheInfo);
        }

        return ResponseEntity.ok(Map.of("cacheName", cacheName, "message", "Stats not available"));
    }

    /**
     * Clear a specific cache
     */
    @DeleteMapping("/clear/{cacheName}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> clearCache(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);

        if (cache != null) {
            cache.clear();
            return ResponseEntity.ok("Cache cleared: " + cacheName);
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Clear all caches
     */
    @DeleteMapping("/clear-all")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        Map<String, String> result = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                result.put(cacheName, "cleared");
            }
        });

        return ResponseEntity.ok(result);
    }

    /**
     * Get list of all cache names
     */
    @GetMapping("/names")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Map<String, Object>> getCacheNames() {
        Map<String, Object> response = new HashMap<>();
        response.put("caches", cacheManager.getCacheNames());
        response.put("count", cacheManager.getCacheNames().size());

        return ResponseEntity.ok(response);
    }

    /**
     * Evict a specific key from a cache
     */
    @DeleteMapping("/evict/{cacheName}/{key}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> evictCacheKey(
            @PathVariable String cacheName,
            @PathVariable String key) {

        Cache cache = cacheManager.getCache(cacheName);

        if (cache != null) {
            cache.evict(key);
            return ResponseEntity.ok("Key evicted from cache: " + cacheName + " - " + key);
        }

        return ResponseEntity.notFound().build();
    }
}
