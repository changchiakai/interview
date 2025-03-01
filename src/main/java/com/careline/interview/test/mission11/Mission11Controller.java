package com.careline.interview.test.mission11;

import com.careline.interview.test.component.Base64Utils;
import com.careline.interview.test.entity.Member;
import com.careline.interview.test.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("mission11")
public class Mission11Controller {
    @Autowired
    private MemberService memberService;

    // 取得所有會員清單
    @GetMapping("list")
    public ResponseEntity<List<Map<String, Object>>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembersNoPassword());
    }

    // 取得單筆會員資料
    @GetMapping("member/{memberId}")
    public ResponseEntity<Map<String, Object>> getMemberById(@PathVariable int memberId) {
        Map<String, Object> member = memberService.getMemberById(memberId);
        return member != null ? ResponseEntity.ok(member) : ResponseEntity.notFound().build();
    }

    @PostMapping("memberUpdate")
    public ResponseEntity<?> updateMember(
            @RequestParam("memberId") String memberId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "oldPassword", required = false) String oldPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "verifyPassword", required = false) String verifyPassword,
            @RequestParam(value = "picture", required = false) MultipartFile pictureFile) {

        // 基本驗證邏輯
        // 1. 信箱是否已存在
        // 2. 信箱是否符合邏輯
        // 3. 舊密碼 和新密碼兩次驗證是否正確
        // 4. 圖轉換


    // TODO
//        memberService.updateMemberDataByAdmin(memberId, name,email,newPassword,pictureFile);

        // 1. 校验权限（例如管理员或当前用户）
        // 2. 更新数据库（示例伪代码）
//        Map<String, Object> member = memberService.getMemberById(Integer.parseInt(memberId));
//        if (name != null) member.setName(name);
//        if (email != null) member.setEmail(email);
//        if (password != null) member.setPassword(encoder.encode(password));
//        if (pictureFile != null) {
//            String pictureBase64 = Base64.getEncoder().encodeToString(pictureFile.getBytes());
//            member.setPictureBase64(pictureBase64);
//        }
//        memberService.save(member);

        return ResponseEntity.ok().build();
    }
    // 取得所有在線會員
    @GetMapping("online")
    public ResponseEntity<List<Member>> getOnlineMembers(@RequestHeader("Authorization") String token) {
        List<Member>  m =memberService.getOnlineMembers();
        System.out.println("m :" + m.toString());

         return ResponseEntity.ok(m);
    }

    // 強制登出會員
    @PostMapping("force-logout/{memberId}")
    public ResponseEntity<String> forceLogout(
            @RequestHeader("Authorization") String token,
            @PathVariable int memberId
           ) {
        System.out.println("Mission11Controller.java forceLogout-33");
        boolean success = memberService.forceLogout(memberId);
        return success ? ResponseEntity.ok("Member logged out")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to logout");
    }
}
