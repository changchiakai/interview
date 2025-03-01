package com.careline.interview.test.service;

import com.careline.interview.test.component.Base64Utils;
import com.careline.interview.test.component.JwtTokenUtils;
import com.careline.interview.test.dto.LoginRequest;
import com.careline.interview.test.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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
            int member_id = (int) member.get("member_id");
            String storedPassword = (String) member.get("password");
            String name = (String) member.get("name");

            System.out.println("Mission5Controller.java login-40:" + storedPassword);
            if (storedPassword != null && storedPassword.equals(loginRequest.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("errorMsg", "");

                // 如果有存過 就將舊的更新
                String token = JwtTokenUtils.generateToken(member_id, name, loginRequest.getEmail());
                // 將已簽發的token 存起來 以利做到 後台登出
                updateAndSaveToken(member_id,token);

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
        // token 是否黑名單(已登出)

        boolean isExpired = JwtTokenUtils.isTokenExpired(token);

        boolean isLogout = checkTokenInBlack(token);

        return (!isExpired && !isLogout);

    }

    public int createMember(Member member) {
        String sql = "INSERT INTO Members (email, password, name) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, member.getEmail(), member.getPassword(), member.getName());

        Map<String, Object> newMember = getUserByEmail(member.getEmail());
        return Integer.parseInt(newMember.get("member_id").toString());
    }

    public Member updateMemberProfile(Map<String, Object> memberData, String email, String name) {

        String sql = "UPDATE Members SET name = ?, email = ? WHERE member_id = ?";

        int rowsAffected = jdbcTemplate.update(sql, name, email, Integer.parseInt(memberData.get("member_id").toString()));

        if (rowsAffected > 0) {
            recordOpLog(memberData, email, name);
            Member updatedMember = new Member();
            updatedMember.setEmail(email);
            updatedMember.setName(name);
            updatedMember.setMember_id(Integer.parseInt(memberData.get("member_id").toString()));
            return updatedMember;
        } else {
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

    public  List<Map<String, Object>> getAllMembersNoPassword() {
        String querySQL = "SELECT member_id, email, name FROM Members";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(querySQL);

        if (!result.isEmpty()) {
            return result;
        } else {
            return null;
        }
    }

    public boolean checkEmailRegister(String email) {
        String sql = "SELECT COUNT(*) FROM members WHERE email = ?";
        try {
            int count = jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
            return count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }


    public   List<Map<String, Object>> getMemberInterests(int memberId) {
        String sql = "SELECT is_movie_checked, is_food_checked, is_sport_checked, is_travel_checked, is_music_checked FROM member_interest WHERE member_id = ?";

        List<Map<String, Object>> interestList = new ArrayList<>();
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, memberId);
            interestList.add(createInterestItem("Movie", (Boolean) result.get("is_movie_checked")));
            interestList.add(createInterestItem("Food", (Boolean) result.get("is_food_checked")));
            interestList.add(createInterestItem("Sport", (Boolean) result.get("is_sport_checked")));
            interestList.add(createInterestItem("Travel", (Boolean) result.get("is_travel_checked")));
            interestList.add(createInterestItem("Music", (Boolean) result.get("is_music_checked")));
            return interestList;

        } catch (Exception e) {
            return null;
        }

    }

    public  boolean  saveMemberInterests(int memberId,List<Map<String, Object>> interests) {

        Map<String, Boolean> interestMap = convertToInterestMap(interests);
        String sql = "INSERT INTO member_interest (member_id, is_movie_checked, is_food_checked, is_sport_checked, is_travel_checked, is_music_checked) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "is_movie_checked = ?, is_food_checked = ?, is_sport_checked = ?, is_travel_checked = ?, is_music_checked = ?";

        try {
            jdbcTemplate.update(sql, memberId,
                    interestMap.get("Movie"), interestMap.get("Food"), interestMap.get("Sport"),
                    interestMap.get("Travel"), interestMap.get("Music"),
                    interestMap.get("Movie"), interestMap.get("Food"), interestMap.get("Sport"),
                    interestMap.get("Travel"), interestMap.get("Music"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Transactional
    public void updateMemberDataByAdmin(int memberId, String  name, String email, String newPassword, MultipartFile pictureFile) {
        // 更新基本資料

        // 更新圖片
        saveAndUpdateMemberPicture(memberId,Base64Utils.convertToBase64(pictureFile));

    }
    private Map<String, Object> createInterestItem(String key, boolean isChecked) {
        Map<String, Object> interestItem = new HashMap<>();
        interestItem.put("key", key);
        interestItem.put("isChecked", isChecked);
        return interestItem;
    }

    private Map<String, Boolean> convertToInterestMap(List<Map<String, Object>> interests) {
        Map<String, Boolean> interestMap = new HashMap<>();
        for (Map<String, Object> interest : interests) {
            String key = (String) interest.get("key");
            Boolean isChecked = (Boolean) interest.get("isChecked");
            interestMap.put(key, isChecked);
        }
        return interestMap;
    }

    public List<Member> getAllUsers() {
        String querySQL = "SELECT * FROM members";
        return jdbcTemplate.query(querySQL, (rs, rowNum) -> new Member(
                rs.getInt("member_id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("name")
        ));
    }

    public Map<String, Object> getUserByEmail(String email) {
        String querySQL = "SELECT * FROM members WHERE email = ?";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(querySQL, email);

        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }

    public Map<String, Object> getUserByMemberId(String memberId) {
        String querySQL = "SELECT * FROM members WHERE member_id = ?";

        // 使用 jdbcTemplate.queryForList 來取得結果並映射為 Map
        List<Map<String, Object>> result = jdbcTemplate.queryForList(querySQL, memberId);

        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }

    public boolean saveAndUpdateMemberPicture(int memberId, String base64Image) {

        // 先檢查是否已經存在圖片資料
        String checkSql = "SELECT COUNT(*) FROM member_picture WHERE member_id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, memberId);

        String sql;
        if (count > 0) {
            // 如果已經有圖片，執行更新操作
            sql = "UPDATE member_picture SET pictureBase64 = ? WHERE member_id = ?";
            count = jdbcTemplate.update(sql, base64Image, memberId);
        } else {
            // 如果沒有圖片，執行新增操作
            sql = "INSERT INTO member_picture (member_id, pictureBase64) VALUES (?, ?)";
            count = jdbcTemplate.update(sql, memberId, base64Image);
        }

        return count > 0;
    }

    public Map<String, Object> getMemberPicture(int memberId) {

        String querySQL = "SELECT * FROM member_picture WHERE member_id = ?";

        List<Map<String, Object>> result = jdbcTemplate.queryForList(querySQL, memberId);

        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }

    public Map<String, Object> getMemberById(int memberId) {

        String querySQL = "SELECT members.member_id , members.email, members.name , member_picture.pictureBase64 FROM members left join member_picture on members.member_id =member_picture.member_id  WHERE members.member_id = ?";

        // 使用 jdbcTemplate.queryForList 來取得結果並映射為 Map
        List<Map<String, Object>> result = jdbcTemplate.queryForList(querySQL, memberId);

        // 如果找到資料，返回第一條紀錄
        if (!result.isEmpty()) {
            result.get(0).put("pictureBase64",result.get(0).get("pictureBase64")==null ? "" :  "data:image/jpeg;base64,"+result.get(0).get("pictureBase64"));

            return result.get(0);
        } else {
            return null;
        }
    }

    public void updateAndSaveToken(int member_id , String token) {

        String isExtSql = "SELECT * FROM online_tokens where member_id = ?  ";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(isExtSql,member_id);
        // 如果找到資料，返回第一條紀錄
        if (!result.isEmpty()) {
            System.out.println("已存在");
            // 更新用戶的 password 和 name
            String sql = "UPDATE online_tokens SET token = ? WHERE member_id = ?";
            // 使用 jdbcTemplate.update 來執行 SQL 更新操作
            jdbcTemplate.update(sql, token , member_id);

        } else {
            String sql = "INSERT INTO online_tokens (member_id, token) VALUES (?, ?)";
            jdbcTemplate.update(sql, member_id, token);
        }

    }

    // 取得所有在線會員
    public List<Member> getOnlineMembers() {
        String sql = "SELECT online_tokens.member_id AS member_id,members.name as name FROM online_tokens left join members on online_tokens.member_id = members.member_id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Member(
                rs.getInt("member_id"),
                rs.getString("name")
        ));
    }
    // 強制登出會員
    public boolean forceLogout(int memberId) {
        try {
            // 取得 token
            String token = jdbcTemplate.queryForObject("SELECT token FROM online_tokens WHERE member_id = ?",
                    new Object[]{memberId}, String.class);

            if (token != null) {
                // 加入黑名單
                String insertSql = "INSERT INTO blacklisted_tokens (token, expiry_time) VALUES (?, NOW())";
                jdbcTemplate.update(insertSql, token);

                // 從 online_tokens 移除
                String deleteSql = "DELETE FROM online_tokens WHERE member_id = ?";
                jdbcTemplate.update(deleteSql, memberId);
            }
            return true;
        } catch (Exception e) {
            return false;
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

}
