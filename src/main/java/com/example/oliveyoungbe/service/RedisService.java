package com.example.oliveyoungbe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ✅ 예약 가능한 시간대 조회
    public Map<String, Integer> getAvailableSlots() {
        Map<Object, Object> slots = redisTemplate.opsForHash().entries("available_slots");
        return slots.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> (String) e.getKey(),
                        e -> Integer.parseInt(e.getValue().toString())));
    }

    // ✅ 특정 시간대 예약 요청
    public String reserveTicket(String slotId) {
        Integer count = (Integer) redisTemplate.opsForHash().get("available_slots", slotId);

        if (count == null || count == 0) {
            redisTemplate.opsForHash().put("slot_status", slotId, "inactive");
            return "Sold Out";
        }

        redisTemplate.opsForHash().increment("available_slots", slotId, -1);

        if ((Integer) redisTemplate.opsForHash().get("available_slots", slotId) == 0) {
            redisTemplate.opsForHash().put("slot_status", slotId, "inactive");
        }

        return "Reservation Confirmed for " + slotId;
    }

    // ✅ 특정 시간대 초기화 (200명 제한)
    public void resetSlots() {
        redisTemplate.opsForHash().put("available_slots", "10:00AM", 200);
        redisTemplate.opsForHash().put("available_slots", "11:00AM", 200);
        redisTemplate.opsForHash().put("available_slots", "12:00PM", 200);
        redisTemplate.opsForHash().put("available_slots", "1:00PM", 200);
        redisTemplate.opsForHash().put("available_slots", "2:00PM", 200);

        redisTemplate.opsForHash().put("slot_status", "10:00AM", "active");
        redisTemplate.opsForHash().put("slot_status", "11:00AM", "active");
        redisTemplate.opsForHash().put("slot_status", "12:00PM", "active");
        redisTemplate.opsForHash().put("slot_status", "1:00PM", "active");
        redisTemplate.opsForHash().put("slot_status", "2:00PM", "active");
    }
}
