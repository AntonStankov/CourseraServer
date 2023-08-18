package com.example.demo.service.report;

import com.example.demo.config.DataSource;
import com.example.demo.entity.Enrollment;
import com.example.demo.service.enrollment.EnrollmentService;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDate;
import com.example.demo.service.enrollment.EnrollmentService;

@Repository
public class ReportsManager {

    private final DataSource datasource = new DataSource();
    String testString;

    public String getAllSuccessfulStudents(int minCredits, LocalDate startDate, LocalDate endDate) {
        StringBuilder csvOutput = new StringBuilder();
        String sql = "SELECT * FROM students " +
                "INNER JOIN enrollment ON students.student_id = enrollment.student_id " +
                "INNER JOIN courses ON enrollment.course_id = courses.courseId " +
                "WHERE students.credit >= ? ORDER BY students.student_id";

        try (Connection connection = datasource.createConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, minCredits);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    StringBuilder output = new StringBuilder();
                    output.append("student_name,credits,courses\n");
                    String currentStudentId = null;
                    String bufferForStudent = "";
                    while (resultSet.next()) {
                        String studentId = resultSet.getString("student_id");
                        if (!studentId.equals(currentStudentId) && bufferForStudent != "") {
                            output.append(resultSet.getString("name")).append(",");
                            output.append(resultSet.getString("credit")).append(",");
                            output.append(bufferForStudent).append('\n');
                            bufferForStudent = "";
                            currentStudentId = studentId;
                        }
                        if(resultSet.getDate("completion_date").toLocalDate().isAfter(startDate) &&
                                resultSet.getDate("completion_date").toLocalDate().isBefore(endDate))
                        {
                            bufferForStudent += resultSet.getString("courseName") + " (" +
                                    resultSet.getString("completion_date") + ") ";
                        }
                    }

                    testString = output.toString();
                    System.out.println("CSV content created successfully.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error occurred while fetching data from the database.");
        }

        return testString;
    }

}

