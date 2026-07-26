package com.ugc.EmpMngmntAndTktingSys.controller;

import com.ugc.EmpMngmntAndTktingSys.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/redis")
public class RedisController {

    private final RedisService redisService;

    @PostMapping("/save")
    public String saveData(@RequestParam String key,
                           @RequestParam String value) {

        redisService.savedata(key, value);

        return "Data saved successfully!";
    }

    @GetMapping("/get")
    public String getData(@RequestParam String key) {

        return redisService.getdata(key);
    }

    @DeleteMapping("/delete")
    public String deleteData(@RequestParam String key) {

        redisService.deleteData(key);

        return "Data deleted successfully!";
    }
}