package com.careline.interview.test.mission11;

import com.careline.interview.test.component.Base64Utils;
import com.careline.interview.test.dto.ErrorResponse;
import com.careline.interview.test.dto.RegisterResponse;
import com.careline.interview.test.entity.Member;
import com.careline.interview.test.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
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

    @PostMapping("member/create")
    public ResponseEntity<?> adminCreateMember(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "picture", required = false) MultipartFile pictureFile) {

        Map<String, Object> verifyData = memberService.memberDataVerify(email, password, name, true,true);
        if (!(Boolean) verifyData.get("success")) {
            return ResponseEntity.ok(verifyData);
        }
        System.out.println(" 沒有註冊過");
        Member member = new Member(email, password, name);
        int memberId = memberService.createMember(member);
        if (pictureFile != null) {
            String base64Image = Base64Utils.convertToBase64(pictureFile);
            boolean status = memberService.saveAndUpdateMemberPicture(memberId, base64Image);
            if (!status) {
                Map<String, Object> resp = new HashMap<>();
                // 回傳結果
                resp.put("success", false);
                resp.put("errorMsg", "照片上傳失敗,稍後再試");
                return ResponseEntity.ok(resp);
            }
        }

        return ResponseEntity.ok(new RegisterResponse(memberId));
    }

    @PostMapping("member/update")
    public ResponseEntity<?> adminUpdateMember(
            @RequestParam("memberId") String memberId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "new-password", required = false) String newPassword,
            @RequestParam(value = "picture", required = false) MultipartFile pictureFile) {
        Map<String, Object> resp = new HashMap<>();
        // 比對舊資料 沒有的就清空
        Map<String, Object> oldMemberData = memberService.getMemberById(Integer.parseInt(memberId));

        // 如果信箱這次沒有異動
        Map<String, Object> verifyData = memberService.memberDataVerify(email, newPassword, name, false,!oldMemberData.get("email").toString().equals(email));
        if (!(Boolean) verifyData.get("success")) {
            return ResponseEntity.ok(verifyData);
        }


        memberService.updateMemberDataByAdmin(Integer.parseInt(memberId), name, email, newPassword, pictureFile);

        resp.put("success", true);
        resp.put("errorMsg", "");
        return ResponseEntity.ok(resp);
    }

    // 取得所有在線會員
    @GetMapping("online")
    public ResponseEntity<List<Member>> getOnlineMembers(@RequestHeader("Authorization") String token) {
        List<Member> m = memberService.getOnlineMembers();
        System.out.println("m :" + m.toString());

        return ResponseEntity.ok(m);
    }

    // 強制登出會員
    @PostMapping("force-logout/{memberId}")
    public ResponseEntity<String> forceLogout(
            @PathVariable int memberId
    ) {
        System.out.println("Mission11Controller.java forceLogout-33");
        boolean success = memberService.forceLogout(memberId);
        return success ? ResponseEntity.ok("已強制登出")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("該用戶無登入紀錄");
    }
}
