package com.careline.interview.test.mission1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequestMapping("mission1")
@RestController()
public class Mission1Controller {

    @GetMapping("hello")
    public String hello(){
        // 取得當前時間
        LocalDateTime now = LocalDateTime.now();

        // 設定格式 (yyyy-MM-dd HH:mm:ss)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 轉換為格式化字串並輸出
        System.out.println();
        return "                        Hello! (" + now.format(formatter) + ")" ;
    }

}
