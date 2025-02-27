package com.careline.interview.test.mission2;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequestMapping("mission2")
@RestController()
public class Mission2Controller {

    static class ComputeResponse {
        public double sum;
        public double max;
        public double min;
        public double avg;
        public List<Double> sorted_list;

        public ComputeResponse(double sum, double max, double min, double avg, List<Double> sorted_list) {
            this.sum = sum;
            this.max = max;
            this.min = min;
            this.avg = avg;
            this.sorted_list = sorted_list;
        }
    }

    @PostMapping("compute")
    public ComputeResponse  compute(@RequestBody Map<String, Object> numList) {
        System.out.println("numList:" + numList.toString());
        // 取得 numList 但先檢查是否為 List
        Object rawList = numList.get("numList");

        if (!(rawList instanceof List<?>)) {
            List<Double> sortedList = new ArrayList<>();
            return new ComputeResponse(0,0,0,0,sortedList);
        }

        List<Double> nList = new ArrayList<>();
        for (Object item : (List<?>) rawList) {
            if (item != null) {
                try {
                    nList.add(Double.parseDouble(item.toString())); // 轉換為 Double
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid number: " + item);
                }
            }
        }
        System.out.println("nList: " + nList);
        double sum = nList.stream().mapToDouble(Double::doubleValue).sum();
        double max = nList.stream().mapToDouble(Double::doubleValue).max().orElse(0.00);
        double min = nList.stream().mapToDouble(Double::doubleValue).min().orElse(0.00);
        double avg = sum / nList.size();

        List<Double> sortedList = new ArrayList<>(nList);
        Collections.sort(sortedList);

        return new ComputeResponse(sum, max, min, avg, sortedList);

    }

}
