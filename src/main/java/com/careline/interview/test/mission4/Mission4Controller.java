package com.careline.interview.test.mission4;

import com.careline.interview.test.dto.MemberDto;
import com.careline.interview.test.entity.Member;
import com.careline.interview.test.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("mission4")
public class Mission4Controller {


    @Autowired
    private MemberService mission3Service;

    @GetMapping("getAllMembers")
    public ResponseEntity<Object> getAllMembers() {
        List<Member> memberList =  mission3Service.getAllUsers();

        memberList.stream()
                .map(member -> new MemberDto(
                        member.getId(),       // 會員ID
                        member.getEmail(),    // 會員Email
                        member.getName()  // 會員名稱
                ))
                .collect(Collectors.toList());

        // 使用 HashMap 創建 Map
        Map<String, Object> response = new HashMap<>();
        response.put("memberList", memberList);

        return ResponseEntity.ok(response);
    }
}
