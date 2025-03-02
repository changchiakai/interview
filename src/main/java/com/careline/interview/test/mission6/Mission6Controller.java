package com.careline.interview.test.mission6;

import com.careline.interview.test.component.JwtTokenUtils;
import com.careline.interview.test.entity.Member;
import com.careline.interview.test.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("mission6")
public class Mission6Controller {

    @Autowired
    private MemberService memberService;

    @PostMapping("updateProfile")
    public ResponseEntity<Map<String, Object>> login(
            @RequestHeader("Authorization") String token,
            @RequestParam String email, @RequestParam String name
    ) {
        Map<String, Object> resp = new HashMap<>();
        if (!memberService.tokenVerify(token)) {
            resp.put("success", false);
            resp.put("errorMsg", "登入資訊已過期，請重新登入");
            return ResponseEntity.ok().body(resp);
        }

        // 確認信箱是否已有人使用
        Map<String, Object> extMember = memberService.getUserByEmail(email);
        System.out.println("extMember:" + extMember);


        // 信箱已有人使用
        if (extMember != null) {
            resp.put("success", false);
            resp.put("errorMsg", "填寫信箱無法使用，請調整");
            return ResponseEntity.ok().body(resp);
        }

        // 信箱格式檢查
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            resp.put("success", false);
            resp.put("errorMsg", "請輸入有效的 Email 地址");
            return ResponseEntity.ok().body(resp);
        }

        Map<String, Object> memberData = memberService.getUserByMemberId(JwtTokenUtils.getMemberIdFromToken(token));
        System.out.println("memberData:" + memberData);


        Member updateMember = memberService.updateMemberProfile(memberData, email, name);
        if (updateMember == null) {
            resp.put("success", false);
            resp.put("errorMsg", "更新失敗");
        } else {
            resp.put("success", true);
            resp.put("errorMsg", "");
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("updatePassword")
    public ResponseEntity<?> updatePassword(
            @RequestHeader("Authorization") String token,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String newPasswordConfirm
    ) {
        ResponseEntity<?> result = memberService.updatePassword(oldPassword, newPassword, newPasswordConfirm, JwtTokenUtils.getEmailFromToken(token));
        return result;
    }
}
