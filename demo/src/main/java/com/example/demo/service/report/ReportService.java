package com.example.demo.service.report;
import java.time.LocalDate;

public interface ReportService {
    ReportsManager reportsManager = new ReportsManager();

    default String createReport(int minCredits, LocalDate startDate, LocalDate endDate) {
        return reportsManager.getAllSuccessfulStudents(minCredits, startDate, endDate);
    }
}
