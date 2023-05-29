package com.dddd.controller;

import com.doudou.log.annotation.ApiLog;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TestLogController {

    @GetMapping("log/info")
    @ApiLog("日志")
    public String logInfo() {
        return "log";
    }
}
