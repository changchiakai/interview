package com.careline.interview.test.mission5;


import com.careline.interview.test.component.JwtTokenUtils;
import com.careline.interview.test.dto.LoginRequest;
import com.careline.interview.test.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("mission5")
public class Mission5Controller {


    private final JdbcTemplate jdbcTemplate;

    public Mission5Controller(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private MemberService memberService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String email, @RequestParam String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(password);
        loginRequest.setEmail(email);

        System.out.println("LoginRequest:" + loginRequest.toString());
        String sql = "SELECT member_id, password,name FROM members WHERE email = ?";
        try {
            // 使用 queryForMap() 同時獲取 password 和 name
            Map<String, Object> member = jdbcTemplate.queryForMap(sql, loginRequest.getEmail());
            int member_id = (int) member.get("member_id"); // 取出密碼
            String storedPassword = (String) member.get("password"); // 取出密碼
            String name = (String) member.get("name"); // 取出 name

            System.out.println("Mission5Controller.java login-40:" + storedPassword);
            if (storedPassword != null && storedPassword.equals(loginRequest.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("errorMsg", "");
                response.put("token", JwtTokenUtils.generateToken(member_id, name)); // 可換成 JWT
                return response;
            }
        } catch (Exception e) {
            System.out.println("e:" + e);
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorMsg", "登入失敗");
        return errorResponse;
    }

    @PostMapping("/logout")
    public ResponseEntity<?>  logout(@RequestHeader("Authorization") String token) {

        Date expiration = JwtTokenUtils.getExpirationDateFromToken(token); // 取得 Token 過期時間

        String sql = "INSERT INTO blacklisted_tokens (token, expiry_time) VALUES (?, ?)";
        jdbcTemplate.update(sql, token, expiration);

        String checkLogoutSql = "SELECT COUNT(*) FROM blacklisted_tokens WHERE token = ?";
        int count = jdbcTemplate.queryForObject(checkLogoutSql, new Object[]{token}, Integer.class);
        if (count > 0) {
            System.out.println("Token 已入黑名單 ");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "logout success");
        return ResponseEntity.ok(response);
    }
}
