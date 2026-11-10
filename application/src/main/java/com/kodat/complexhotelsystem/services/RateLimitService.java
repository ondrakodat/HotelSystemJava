package com.kodat.complexhotelsystem.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RateLimitService {

    private final ConcurrentHashMap<String, RequestCounter> counters = new ConcurrentHashMap<>();

    public Decision checkLimit(String key, int maxRequests, long windowMillis){
        long now = System.nanoTime();
        long windowTime = windowMillis * 1_000_000L;

        AtomicReference<Decision> decision = new AtomicReference<>();

        counters.compute(key, (k, current) -> {
            if(current == null){
                decision.set(new Decision(true, 0));
                return new RequestCounter(1, now);
            }
            if(now - current.windowStart() >= windowTime){
                decision.set(new Decision(true, 0));
                return new RequestCounter(1, now);
            }
            if(current.count() < maxRequests){
                decision.set(new Decision(true, 0));
                return new RequestCounter(
                        current.count() + 1,
                        current.windowStart()
                );
            }
            long remainingTime = windowTime - (now - current.windowStart());
            long retryAfterSeconds =
                    (remainingTime + 999_999_999L) / 1_000_000_000L;
            decision.set(new Decision(false, retryAfterSeconds));
            return current;

        });

        return decision.get();
    }


    private record RequestCounter(int count, long windowStart){}
    public record Decision(boolean allowed, long retryAfterSeconds){}
}
