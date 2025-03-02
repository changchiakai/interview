package com.careline.interview.test.mission8;

import com.careline.interview.test.component.JwtTokenUtils;
import com.careline.interview.test.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("mission8")
public class Mission8Controller {

    @Autowired
    private MemberService memberService;
    @GetMapping("getInterest")
    public ResponseEntity<Map<String, Object>> getInterest(
            @RequestHeader("Authorization") String token
    ) {

        Map<String, Object> resp = new HashMap<>();
        if (!memberService.tokenVerify(token)) {
            resp.put("success", false);
            resp.put("errorMsg", "登入資訊已過期，請重新登入");
            return ResponseEntity.ok().body(resp);
        }
        String memberId = JwtTokenUtils.getMemberIdFromToken(token);

        List<Map<String, Object>> interestList = memberService.getMemberInterests(Integer.parseInt(memberId));

        if(interestList ==null){
            resp.put("success", false);
            resp.put("errorMsg", "");
        }else{
            resp.put("success", true);
            resp.put("errorMsg", "");
            resp.put("interestList", interestList);

        }

        return ResponseEntity.ok(resp);
    }

    // 保存会员兴趣
    @PostMapping("saveInterest")
    public ResponseEntity<Map<String, Object>> saveInterest(
            @RequestHeader("Authorization") String token,
            @RequestBody List<Map<String, Object>> interests) {
        Map<String, Object> resp = new HashMap<>();
        if (!memberService.tokenVerify(token)) {
            resp.put("success", false);
            resp.put("errorMsg", "登入資訊已過期，請重新登入");
            return ResponseEntity.ok().body(resp);
        }
        String memberId = JwtTokenUtils.getMemberIdFromToken(token);

        boolean status = memberService.saveMemberInterests(Integer.parseInt(memberId),interests);
        resp.put("success", status);
        resp.put("errorMsg", "");
        return ResponseEntity.ok(resp);

    }
}
