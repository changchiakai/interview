package com.careline.interview.test.service;

import com.careline.interview.test.component.JwtTokenUtils;
import com.careline.interview.test.dto.LoginRequest;
import com.careline.interview.test.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemberService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> login(LoginRequest loginRequest) {
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
                response.put("token", JwtTokenUtils.generateToken(member_id, name, loginRequest.getEmail())); // 可換成 JWT
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

    public Map<String, Object> logout(String token) {
        Date expiration = JwtTokenUtils.getExpirationDateFromToken(token); // 取得 Token 過期時間

        String sql = "INSERT INTO blacklisted_tokens (token, expiry_time) VALUES (?, ?)";
        jdbcTemplate.update(sql, token, expiration);


        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "logout success");
        return response;
    }

    public boolean checkTokenInBlack(String token) {
        String checkLogoutSql = "SELECT COUNT(*) FROM blacklisted_tokens WHERE token = ?";
        int count = jdbcTemplate.queryForObject(checkLogoutSql, new Object[]{token}, Integer.class);
        if (count > 0) {
            System.out.println("Token 已入黑名單 ");
            return true;
        }
        return false;
    }

    public boolean tokenVerify(String token) {
        // token 是否過期
        // token 是否再黑名單(已登出)

        boolean isExpired = JwtTokenUtils.isTokenExpired(token);

        boolean isLogout = checkTokenInBlack(token);


        return (!isExpired && !isLogout);

    }

    public int addMember(Member member) {
        String sql = "INSERT INTO Members (email, password, name) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, member.getEmail(), member.getPassword(), member.getName());

        Map<String, Object> newMember = getUserByEmail(member.getEmail());
        // 取得 "member_id" 並轉換為 int
        return Integer.parseInt(newMember.get("member_id").toString());
    }

    public Member updateMemberProfile(Map<String, Object> memberData, String email, String name) {

        // 更新用戶的 password 和 name
        String sql = "UPDATE Members SET name = ?, email = ? WHERE member_id = ?";

        // 使用 jdbcTemplate.update 來執行 SQL 更新操作
        int rowsAffected = jdbcTemplate.update(sql, name, email, Integer.parseInt(memberData.get("member_id").toString()));

        if (rowsAffected > 0) {
            recordOpLog(memberData, email, name);
            // 若更新成功，返回更新後的 Member 物件
            Member updatedMember = new Member();
            updatedMember.setEmail(email);
            updatedMember.setName(name);
            updatedMember.setMember_id(Integer.parseInt(memberData.get("member_id").toString()));
            return updatedMember;
        } else {
            // 如果沒有更新到資料，返回 null 或根據需求拋出異常
            return null;
        }


    }

    public Member updatePassword(String oldPassword, String newPassword, String newPasswordConfirm, String email) {
        // 1. 檢查舊密碼是否正確
        String checkOldPasswordSQL = "SELECT password FROM Members WHERE email = ?";
        String currentPassword = jdbcTemplate.queryForObject(checkOldPasswordSQL, String.class, email);

        if (!oldPassword.equals(currentPassword)) {
            throw new IllegalArgumentException("原始密碼錯誤");
        }

        // 2. 檢查新密碼和確認新密碼是否一致
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new IllegalArgumentException("新密碼兩次輸入不同，請重新輸入");
        }

        // 3. 更新資料庫中的密碼
        String updatePasswordSQL = "UPDATE Members SET password = ? WHERE email = ?";
        int rowsAffected = jdbcTemplate.update(updatePasswordSQL, newPassword, email);

        if (rowsAffected > 0) {
            recordOpLogPwd(email);
            Member updatedMember = new Member();
            updatedMember.setEmail(email);
            updatedMember.setPassword(newPassword);
            return updatedMember;
        } else {
            throw new IllegalStateException("更新失敗");
        }
    }

    public List<Member> getAllMembers() {
        String sql = "SELECT member_id, email, password, name FROM Members";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Member(
                        rs.getInt("member_id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("name")
                )
        );
    }

    /**
     * 檢查電子郵件是否已註冊
     *
     * @param email 用戶的電子郵件
     * @return true 如果電子郵件已註冊，否則 false
     */
    public boolean isEmailRegistered(String email) {
        String sql = "SELECT COUNT(*) FROM members WHERE email = ?";
        try {
            int count = jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
            return count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;  // 如果沒有找到任何匹配的資料，則返回 false
        }
    }

    //    private final JdbcTemplate jdbcTemplate;
//
//    public Mission3Service(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    @PostConstruct
//    public void init() {
//        String createTableSQL = "CREATE TABLE users (" +
//                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
//                "name VARCHAR(255) NOT NULL, " +
//                "age INT NOT NULL" +
//                ")";
//        jdbcTemplate.execute(createTableSQL);
//    }
//
//    // 新增用戶
//    public void addUser(String name, int age) {
//        String insertSQL = "INSERT INTO users (name, age) VALUES (?, ?)";
//        jdbcTemplate.update(insertSQL, name, age);
//    }
//
    // 查詢所有用戶
    public List<Member> getAllUsers() {
        String querySQL = "SELECT * FROM members";
        return jdbcTemplate.query(querySQL, (rs, rowNum) -> new Member(
                rs.getInt("member_id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("name")
        ));
    }

    // 查詢單一用戶
    public Map<String, Object> getUserByEmail(String email) {
        String querySQL = "SELECT * FROM members WHERE email = ?";

        // 使用 jdbcTemplate.queryForList 來取得結果並映射為 Map
        List<Map<String, Object>> result = jdbcTemplate.queryForList(querySQL, email);

        // 如果找到資料，返回第一條紀錄
        if (!result.isEmpty()) {
            return result.get(0);  // 返回找到的第一個結果，若需要多筆資料可以返回整個 List
        } else {
            return null;  // 如果沒有資料，返回 null
        }
    }

    public Map<String, Object> getUserByMemberId(String memberId) {
        String querySQL = "SELECT * FROM members WHERE member_id = ?";

        // 使用 jdbcTemplate.queryForList 來取得結果並映射為 Map
        List<Map<String, Object>> result = jdbcTemplate.queryForList(querySQL, memberId);

        // 如果找到資料，返回第一條紀錄
        if (!result.isEmpty()) {
            return result.get(0);  // 返回找到的第一個結果，若需要多筆資料可以返回整個 List
        } else {
            return null;  // 如果沒有資料，返回 null
        }
    }

    public boolean saveMemberPicture(int memberId, String base64Image) {

        String sql = "INSERT INTO member_picture (member_id, pictureBase64) VALUES (?, ?)";
        int count = jdbcTemplate.update(sql, memberId, base64Image);
        return count > 0;
    }

    public Map<String, Object> getMemberPicture(int memberId) {

        String querySQL = "SELECT * FROM member_picture WHERE member_id = ?";

        // 使用 jdbcTemplate.queryForList 來取得結果並映射為 Map
        List<Map<String, Object>> result = jdbcTemplate.queryForList(querySQL, memberId);

        // 如果找到資料，返回第一條紀錄
        if (!result.isEmpty()) {
            return result.get(0);  // 返回找到的第一個結果，若需要多筆資料可以返回整個 List
        } else {
            return null;  // 如果沒有資料，返回 null
        }
    }

    private void recordOpLog(Map<String, Object> extMember, String email, String name) {
        String origName = extMember.get("name").toString();
        String origEmail = extMember.get("email").toString();

        String operationText = "使用者:" + extMember.get("member_id ").toString() + "修改資料，原始名稱:" + origName + " 調整為:" + name + ",原始信箱:" + origEmail + " 調整為:" + email;
        String sql = "INSERT INTO profile_operation_log (operation,update_time) VALUES (?, ?)";
        int add = jdbcTemplate.update(sql, operationText, new Date());
        System.out.println("add: " + add);
    }

    private void recordOpLogPwd(String email) {

        String operationText = "使用者信箱:" + email + " , 進行修改密碼";
        String sql = "INSERT INTO profile_operation_log (operation,update_time) VALUES (?, ?)";
        int add = jdbcTemplate.update(sql, operationText, new Date());
        System.out.println("add: " + add);
    }
////
//    // 刪除用戶
//    public void deleteUser(Long id) {
//        String deleteSQL = "DELETE FROM users WHERE id = ?";
//        jdbcTemplate.update(deleteSQL, id);
//    }
}
