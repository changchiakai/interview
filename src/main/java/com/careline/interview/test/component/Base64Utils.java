package com.careline.interview.test.component;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Component
public class Base64Utils {

    // 將圖片轉換為 Base64 字串
    public static String convertToBase64(MultipartFile picture){
        try{
            byte[] fileBytes = picture.getBytes();
            return Base64.getEncoder().encodeToString(fileBytes);
        }catch (Exception e ){
            System.out.println(" 轉圖發生錯誤: "+ e.getMessage());
            return "";
        }

    }
}
