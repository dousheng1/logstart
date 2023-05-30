package com.dddd.controller;

import com.doudou.log.annotation.ApiLog;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class TestLogController {

    @GetMapping("log/info")
    @ApiLog("日志")
    public String logInfo() {
//        throw new RuntimeException("失败");
        double random = Math.random();
        log.info("random:{}", random);
        if (random > 0.5) {
//            double a = random / 0;
            throw new IndexOutOfBoundsException("123");
        }
        return "log";
    }
}
