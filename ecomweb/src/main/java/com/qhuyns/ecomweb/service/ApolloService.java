package com.qhuyns.ecomweb.service;

import com.nimbusds.oauth2.sdk.Request;
import com.nimbusds.oauth2.sdk.Response;
import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.mapper.CategoryMapper;
import com.qhuyns.ecomweb.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApolloService {
    public void get() throws IOException {
        String url = "https://api.apollo.io/v1/organizations/search";
        String apiKey = "LjzJi2j3G3-G_430hZDn9g";

        // Body JSON
        Map<String, Object> body = new HashMap<>();
        body.put("q_keywords", "Information Technology");
        body.put("locations", Arrays.asList("Vietnam"));
        body.put("page", 100);
        body.put("per_page", 30);

        // Header (API key phải nằm ở đây)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl(CacheControl.noCache());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-Api-Key", apiKey); // ✅ API key vào đây

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Gửi request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());

        // Lấy dữ liệu
        List<Map<String, Object>> organizations = (List<Map<String, Object>>) response.getBody().get("organizations");

        // Tạo Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Organizations");

        // Header
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Name", "Website", "Industry", "Location"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Ghi dữ liệu
        int rowNum = 1;
        for (Map<String, Object> org : organizations) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) org.get("name"));
            row.createCell(1).setCellValue((String) org.get("website_url"));
            row.createCell(2).setCellValue((String) org.get("industry"));
            row.createCell(3).setCellValue((String) org.get("country"));
        }

        // Auto-size cột
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Xuất file
        try (FileOutputStream fileOut = new FileOutputStream("C:\\Users\\Admin\\Downloads\\organizations4.xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        workbook.close();

        System.out.println("✅ Xuất file Excel thành công: organizations.xlsx");
    }
}

