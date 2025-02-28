package com.careline.interview.test.service;
import com.careline.interview.test.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MemberService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int  addMember(Member member) {
        String sql = "INSERT INTO Members (email, password, name) VALUES (?, ?, ?)";
        int memberId = jdbcTemplate.update(sql, member.getEmail(), member.getPassword(), member.getName());
        System.out.println("memberId: "+ memberId);

        Map<String, Object> newMember = getUserByEmail(member.getEmail());
        // 取得 "member_id" 並轉換為 int
        return Integer.parseInt(newMember.get("member_id").toString());
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
        return jdbcTemplate.query(querySQL, (rs, rowNum) ->new Member(
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
//
//    // 刪除用戶
//    public void deleteUser(Long id) {
//        String deleteSQL = "DELETE FROM users WHERE id = ?";
//        jdbcTemplate.update(deleteSQL, id);
//    }
}
