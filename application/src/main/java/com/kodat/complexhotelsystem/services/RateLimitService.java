package com.kodat.complexhotelsystem.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RateLimitService {

    private final ConcurrentHashMap<String, Integer> counters = new ConcurrentHashMap<>();

    public Decision checkLimit(String key, int maxRequests, long windowMilis){
        long now = System.nanoTime();
        long windowTime = windowMilis * 1_000_000L;

        AtomicReference<Decision> decision = new AtomicReference<>();


        return null;
    }


    private record RequstCounter(int start, long windowStart){}
    public record Decision(boolean allowed, long retryAfterSeconds){}
}
