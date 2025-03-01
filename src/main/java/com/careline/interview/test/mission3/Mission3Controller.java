package com.careline.interview.test.mission3;

import com.careline.interview.test.dto.ErrorResponse;
import com.careline.interview.test.dto.RegisterResponse;
import com.careline.interview.test.entity.Member;
import com.careline.interview.test.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("mission3")
public class Mission3Controller {

    @Autowired
    private MemberService mission3Service;

    @PostMapping("register")
    public ResponseEntity<Object> register(@RequestParam String email, @RequestParam String password, @RequestParam String name) {
        // 空白欄位檢查
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.ok().body(new ErrorResponse("Email 不可為空白"));
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.ok().body(new ErrorResponse("密碼不可為空白"));
        }
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.ok().body(new ErrorResponse("會員稱呼不可為空白"));
        }


        // 信箱格式檢查
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            return ResponseEntity.ok().body(new ErrorResponse("請輸入有效的 Email 地址"));
        }

        // 檢查信箱是否已註冊
        if (mission3Service.isEmailRegistered(email)) {
            return ResponseEntity.ok().body(new ErrorResponse("Email 已經被註冊"));
        }

        Member member = new Member(email, password, name);
        System.out.println("member:" +member);

       int memberId =  mission3Service.addMember(member);


        return ResponseEntity.ok(new RegisterResponse(memberId));
    }

    @GetMapping("/all")
    public List<Member> getAllMembers() {

        return mission3Service.getAllMembers();

    }


//    private final Mission3Service mission3Service;
//
//    public Mission3Controller(Mission3Service mission3Service) {
//        this.mission3Service = mission3Service;
//    }
//
//    // 查詢所有用戶
//    @GetMapping("register")
//    public List<Member> getUsers() {
//        return mission3Service.getAllUsers();
//    }
//
//    // 新增用戶
//    @PostMapping
//    public void addUser(@RequestBody Member user) {
//        mission3Service.addUser(user.getName(), user.getAge());
//    }
//
//    // 刪除用戶
//    @DeleteMapping("/{id}")
//    public void deleteUser(@PathVariable Long id) {
//        mission3Service.deleteUser(id);
//    }
// 註冊錯誤回應資料

}


