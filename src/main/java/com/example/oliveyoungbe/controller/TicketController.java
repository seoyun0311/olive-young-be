package com.example.oliveyoungbe.controller;
import com.example.oliveyoungbe.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/slots")
public class TicketController {

    @Autowired
    private RedisService redisService;

    // ✅ 특정 시간대 예약 요청
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveTicket(@RequestParam String slotId) {
        String result = redisService.reserveTicket(slotId);
        return result.equals("Sold Out") ? ResponseEntity.badRequest().body(result) : ResponseEntity.ok(result);
    }

    // ✅ 모든 시간대 초기화 (200명으로 설정) → 관리자용
    @PostMapping("/reset")
    public ResponseEntity<String> resetSlots() {
        redisService.resetSlots();
        return ResponseEntity.ok("All slots have been reset to 200.");
    }
}
