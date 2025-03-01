package com.careline.interview.test.mission5;


import com.careline.interview.test.dto.LoginRequest;
import com.careline.interview.test.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("mission5")
public class Mission5Controller {

    @Autowired
    private MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String email, @RequestParam String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(password);
        loginRequest.setEmail(email);
        Map<String, Object> resp = memberService.login(loginRequest);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<?>  logout(@RequestHeader("Authorization") String token) {
        Map<String, Object> resp = memberService.logout(token);
        return ResponseEntity.ok(resp);
    }
}
