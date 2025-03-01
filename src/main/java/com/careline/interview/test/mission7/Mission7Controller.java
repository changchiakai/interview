package com.careline.interview.test.mission7;

import com.careline.interview.test.component.Base64Utils;
import com.careline.interview.test.component.JwtTokenUtils;
import com.careline.interview.test.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("mission7")
public class Mission7Controller {
    @Autowired
    private MemberService memberService;

    @PostMapping("uploadPicture")
    public ResponseEntity<Map<String, Object>> uploadPicture(
            @RequestHeader("Authorization") String token,
            @RequestPart("picture") MultipartFile picture
    ) {

        Map<String, Object> resp = new HashMap<>();
        if (!memberService.tokenVerify(token)) {
            resp.put("success", false);
            resp.put("errorMsg", "登入資訊已過期，請重新登入");
            return ResponseEntity.ok().body(resp);
//            return new ResponseEntity<>(resp, HttpStatus.UNAUTHORIZED);
        }

        if (picture.isEmpty()) {
            resp.put("success", false);
            resp.put("errorMsg", "請選擇圖片");
            return ResponseEntity.ok(resp);
        }

        String memberId = JwtTokenUtils.getMemberIdFromToken(token);

        try {
            // 將檔案轉換成 Base64 字串
            String base64Image = Base64Utils.convertToBase64(picture);

            boolean status = memberService.saveAndUpdateMemberPicture(Integer.parseInt(memberId),base64Image);

            resp.put("success", status);
            resp.put("errorMsg", "");
            return ResponseEntity.ok(resp);

        } catch (IOException e) {
            resp.put("success", false);
            resp.put("errorMsg", e.getMessage());
            return ResponseEntity.ok(resp);
        }
    }
    @GetMapping("getPicture")
    public ResponseEntity<Map<String, Object>> getPicture(
            @RequestHeader("Authorization") String token
    ) {

        Map<String, Object> resp = new HashMap<>();
        if (!memberService.tokenVerify(token)) {
            resp.put("success", false);
            resp.put("errorMsg", "登入資訊已過期，請重新登入");
            return ResponseEntity.ok().body(resp);
        }

        String memberId = JwtTokenUtils.getMemberIdFromToken(token);

        Map<String, Object> memberPicture =  memberService.getMemberPicture(Integer.parseInt(memberId));

        resp.put("success", true);
        resp.put("imageUrl", memberPicture == null ? "" : "data:image/jpeg;base64,"+memberPicture.get("picturebase64").toString());
        resp.put("errorMsg", "");
        return ResponseEntity.ok(resp);
    }
}
