package com.careline.interview.test.mission3;

import com.careline.interview.test.dto.RegisterResponse;
import com.careline.interview.test.entity.Member;
import com.careline.interview.test.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("mission3")
public class Mission3Controller {

    @Autowired
    private MemberService memberService;

    @PostMapping("register")
    public ResponseEntity<Object> register(@RequestParam String email, @RequestParam String password, @RequestParam String name) {
        Map<String, Object> verifyData =  memberService.memberDataVerify(email,password,name,true,true);

        if (!(Boolean) verifyData.get("success")) {
            return ResponseEntity.ok(verifyData);
        }

        Member member = new Member(email, password, name);
        System.out.println("member:" + member);

        int memberId = memberService.createMember(member);

        return ResponseEntity.ok(new RegisterResponse(memberId));
    }

    @GetMapping("/all")
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

}

