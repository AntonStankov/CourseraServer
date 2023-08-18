package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.report.ReportService;
import com.example.demo.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import com.example.demo.jwt.JwtTokenService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/reports")

public class ReportController {

    private ReportService ReportService = new ReportService() {
        public String createReport(int minCredits, LocalDate startDate, LocalDate endDate){
            return ReportService.super.createReport(minCredits, startDate, endDate);
        };
    };

    @Autowired
    private JwtTokenService jwtTokenUtil;

    private UserService userService = new UserService() {
        @Override
        public User findByEmail(String email) {
            return UserService.super.findByEmail(email);
        }
    };


    @GetMapping
    public String generateReport(@RequestParam("minCredits") int minCredits, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate, HttpServletRequest httpServletRequest) {
        if(startDate.isAfter(endDate)) throw new ResponseStatusException(HttpStatusCode.valueOf(440), "Start date after end date!");

        if (jwtTokenUtil.isTokenExpired(jwtTokenUtil.getTokenFromRequest(httpServletRequest))) throw new ResponseStatusException(HttpStatusCode.valueOf(403), "JWT has expired!");

        User user = userService.findByEmail(jwtTokenUtil.getEmailFromToken(jwtTokenUtil.getTokenFromRequest(httpServletRequest)));

        if (!user.getRole().toString().equals("TEACHER")) throw new RuntimeException("You are not a teacher!");

        return ReportService.createReport(minCredits, startDate, endDate);
    }
}