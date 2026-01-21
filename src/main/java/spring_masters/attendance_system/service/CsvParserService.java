package spring_masters.attendance_system.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spring_masters.attendance_system.dto.CsvAttendanceRecord;
import spring_masters.attendance_system.exception.CsvParsingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParserService {

    private static final String[] EXPECTED_HEADERS = { "studentEmail", "subjectName", "present" };

    public List<CsvAttendanceRecord> parseCsvFile(MultipartFile file) {
        List<CsvAttendanceRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(EXPECTED_HEADERS)
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .setIgnoreEmptyLines(true)
                    .build();

            CSVParser csvParser = csvFormat.parse(reader);

            int rowNumber = 1; // Start from 1 (header is row 0)
            for (CSVRecord csvRecord : csvParser) {
                rowNumber++;

                try {
                    String studentEmail = csvRecord.get("studentEmail");
                    String subjectName = csvRecord.get("subjectName");
                    String presentStr = csvRecord.get("present");

                    // Validate required fields
                    if (studentEmail == null || studentEmail.trim().isEmpty()) {
                        throw new CsvParsingException("Row " + rowNumber + ": Student email is required");
                    }
                    if (subjectName == null || subjectName.trim().isEmpty()) {
                        throw new CsvParsingException("Row " + rowNumber + ": Subject name is required");
                    }
                    if (presentStr == null || presentStr.trim().isEmpty()) {
                        throw new CsvParsingException("Row " + rowNumber + ": Present field is required");
                    }

                    // Parse boolean value
                    boolean present = parseBoolean(presentStr.trim(), rowNumber);

                    CsvAttendanceRecord record = new CsvAttendanceRecord(
                            studentEmail.trim(),
                            subjectName.trim(),
                            present,
                            rowNumber);
                    records.add(record);

                } catch (IllegalArgumentException e) {
                    throw new CsvParsingException("Row " + rowNumber + ": Invalid data format - " + e.getMessage());
                }
            }

            if (records.isEmpty()) {
                throw new CsvParsingException("CSV file is empty or contains no valid records");
            }

        } catch (IOException e) {
            throw new CsvParsingException("Failed to read CSV file: " + e.getMessage(), e);
        }

        return records;
    }

    /**
     * Parse boolean from various string representations
     */
    private boolean parseBoolean(String value, int rowNumber) {
        String lowerValue = value.toLowerCase();

        switch (lowerValue) {
            case "true":
            case "1":
            case "yes":
            case "present":
                return true;
            case "false":
            case "0":
            case "no":
            case "absent":
                return false;
            default:
                throw new CsvParsingException(
                        "Row " + rowNumber + ": Invalid boolean value '" + value +
                                "'. Expected: true/false, 1/0, yes/no, present/absent");
        }
    }
}
