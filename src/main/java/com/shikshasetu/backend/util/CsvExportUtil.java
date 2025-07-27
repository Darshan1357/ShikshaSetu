package com.shikshasetu.backend.util;

import com.shikshasetu.backend.model.Course;
import org.apache.commons.csv.*;
import java.io.*;
import java.util.List;

public class CsvExportUtil {

    public static ByteArrayInputStream coursesToCSV(List<Course> courses) {
        final CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Title", "Description", "Instructor", "CreatedDate")
                .build();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {

            for (Course course : courses) {
                csvPrinter.printRecord(
                    course.getId(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getInstructor().getName(),
                    course.getCreatedDate()
                );
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to CSV", e);
        }
    }
}
