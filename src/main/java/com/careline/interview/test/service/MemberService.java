package com.careline.interview.test.service;
import com.careline.interview.test.mission3.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String addMember(Member member) {
        String sql = "INSERT INTO Members (email, password, name) VALUES (?, ?, ?)";
        int memberId = jdbcTemplate.update(sql, member.getEmail(), member.getPassword(), member.getNickname());
        System.out.println("memberId: "+ memberId);
        return "Member added successfully";
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
//    // 查詢所有用戶
//    public List<Member> getAllUsers() {
//        String querySQL = "SELECT * FROM users";
//        return jdbcTemplate.query(querySQL, (rs, rowNum) -> new Member(
//                rs.getLong("id"),
//                rs.getString("name"),
//                rs.getInt("age")
//        ));
//    }
//
//    // 刪除用戶
//    public void deleteUser(Long id) {
//        String deleteSQL = "DELETE FROM users WHERE id = ?";
//        jdbcTemplate.update(deleteSQL, id);
//    }
}
