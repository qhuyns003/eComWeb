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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApolloService {
//    public void get() throws IOException {
//        String url = "https://api.apollo.io/v1/organizations/search";
//        String apiKey = "LjzJi2j3G3-G_430hZDn9g";
//
//        // Body JSON
//        Map<String, Object> body = new HashMap<>();
//        body.put("q_organization_name", "HYOSUNG");
//        body.put("page", 1);
//        body.put("per_page", 30);
//
//        // Header (API key phải nằm ở đây)
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setCacheControl(CacheControl.noCache());
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.set("X-Api-Key", apiKey); // API key vào đây
//
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//        // Gửi request
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Map> response = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                requestEntity,
//                Map.class
//        );
//
//        System.out.println("Status: " + response.getStatusCode());
//        System.out.println("Body: " + response.getBody());
//
//        // Lấy dữ liệu
//        List<Map<String, Object>> organizations = (List<Map<String, Object>>) response.getBody().get("organizations");
//
//        // Tạo Excel
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Organizations");
//
//        // Header
//        Row headerRow = sheet.createRow(0);
//        String[] columns = {"Name", "Website", "Industry", "Location"};
//        for (int i = 0; i < columns.length; i++) {
//            Cell cell = headerRow.createCell(i);
//            cell.setCellValue(columns[i]);
//        }
//
//        // Ghi dữ liệu
//        int rowNum = 1;
//        for (Map<String, Object> org : organizations) {
//            Row row = sheet.createRow(rowNum++);
//            row.createCell(0).setCellValue((String) org.get("name"));
//            row.createCell(1).setCellValue((String) org.get("website_url"));
//            row.createCell(2).setCellValue((String) org.get("industry"));
//            row.createCell(3).setCellValue((String) org.get("country"));
//        }
//
//        // Auto-size cột
//        for (int i = 0; i < columns.length; i++) {
//            sheet.autoSizeColumn(i);
//        }
//
//        // Xuất file
//        try (FileOutputStream fileOut = new FileOutputStream("C:\\Users\\Admin\\Downloads\\organizations10.xlsx")) {
//            workbook.write(fileOut);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        workbook.close();
//
//        System.out.println("Xuất file Excel thành công: organizations.xlsx");
//    }

//    public void get() throws IOException {
//        String url = "https://api.apollo.io/v1/organizations/search";
//        String apiKey = "h9XDtHuy7W75QSBPHBaB8w";
//
//        // Danh sách tên công ty cần tìm
//        List<String> companyNames = Arrays.asList("INOXPAL ACOS E","LG");
//
//        // Chuẩn bị Excel
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Organizations");
//        String[] columns = {"Name", "Website", "Industry", "Country"};
//
//        // Header
//        Row headerRow = sheet.createRow(0);
//        for (int i = 0; i < columns.length; i++) {
//            Cell cell = headerRow.createCell(i);
//            cell.setCellValue(columns[i]);
//        }
//
//        int rowNum = 1; // Bắt đầu ghi dữ liệu từ dòng 1
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        for (String companyName : companyNames) {
//            // Body JSON
//            Map<String, Object> body = new HashMap<>();
//            body.put("q_organization_name", companyName);
//            body.put("page", 1);
//            body.put("per_page", 30);
//
//            // Header
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("X-Api-Key", apiKey);
//
//            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//            // Gửi request
//            ResponseEntity<Map> response = restTemplate.exchange(
//                    url,
//                    HttpMethod.POST,
//                    requestEntity,
//                    Map.class
//            );
//
//            if (response.getStatusCode().is2xxSuccessful()) {
//                List<Map<String, Object>> organizations =
//                        (List<Map<String, Object>>) response.getBody().get("organizations");
//
//                for (Map<String, Object> org : organizations) {
//                    Row row = sheet.createRow(rowNum++);
//                    row.createCell(0).setCellValue((String) org.get("name"));
//                    row.createCell(1).setCellValue((String) org.get("website_url"));
//                    row.createCell(2).setCellValue((String) org.get("industry"));
//                    row.createCell(3).setCellValue((String) org.get("country"));
//                }
//            } else {
//                System.out.println("Request failed for company: " + companyName);
//            }
//        }
//
//        // Auto-size cột
//        for (int i = 0; i < columns.length; i++) {
//            sheet.autoSizeColumn(i);
//        }
//
//        // Lưu file Excel
//        try (FileOutputStream fileOut = new FileOutputStream("C:\\Users\\Admin\\Downloads\\organizations_multi10.xlsx")) {
//            workbook.write(fileOut);
//        }
//        workbook.close();
//
//        System.out.println("Xuất file Excel thành công!");
//    }

    public void get() throws IOException, InterruptedException {
        String url = "https://api.apollo.io/v1/organizations/search";
        String apiKey = "K159L-bMlGg0oUBOlql6Nw"; // Đổi thành API key của bạn

        // ===== Đọc danh sách tên công ty từ file Excel =====
        List<String> companyNames = new ArrayList<>();
        String inputFilePath = "C:\\Users\\Admin\\Downloads\\cpn3.xlsx";

        try (FileInputStream fis = new FileInputStream(inputFilePath);
             Workbook inputWorkbook = new XSSFWorkbook(fis)) {

            Sheet sheet = inputWorkbook.getSheetAt(0); // Lấy sheet đầu tiên
            for (int i = 0; i <= sheet.getLastRowNum(); i++) { // Bỏ header ở dòng 0
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null) { // Cột 0 là tên công ty
                    String companyName = row.getCell(0).getStringCellValue().trim();
                    if (!companyName.isEmpty()) {
                        companyNames.add(companyName);
                    }
                }
            }
        }

        // ===== Tạo file Excel xuất kết quả =====
        Workbook workbook = new XSSFWorkbook();
        Sheet outputSheet = workbook.createSheet("Organizations");
        String[] columns = {"Name", "Website", "Industry", "Country"};

        // Header
        Row headerRow = outputSheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int rowNum = 1; // Bắt đầu ghi dữ liệu từ dòng 1
        RestTemplate restTemplate = new RestTemplate();

        // ===== Gọi API Apollo cho từng công ty =====
        for (String companyName : companyNames) {
            Map<String, Object> body = new HashMap<>();
            body.put("q_organization_name", companyName);
            body.put("organization_not_locations", Arrays.asList(
                    "china", "south korea", "japan", "taiwan", "hong kong"
            ));
            body.put("page", 1);
            body.put("per_page", 30);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Api-Key", apiKey);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            boolean success = false;
            int retry = 0;
            while (!success && retry < 1) {
                try {
                    ResponseEntity<Map> response = restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            requestEntity,
                            Map.class
                    );

                    if (response.getStatusCode().is2xxSuccessful()) {
                        List<Map<String, Object>> organizations =
                                (List<Map<String, Object>>) response.getBody().get("organizations");

                        for (Map<String, Object> org : organizations) {
                            if((String)org.get("website_url") == null || "".equals((String)org.get("website_url")) ){
                                continue;
                            }
                            Row row = outputSheet.createRow(rowNum++);
                            row.createCell(0).setCellValue((String) org.get("name"));
                            row.createCell(1).setCellValue((String) org.get("website_url"));
                            row.createCell(2).setCellValue((String) org.get("industry"));
                            row.createCell(3).setCellValue((String) org.get("country"));
                        }
                        success = true;
                        System.out.println("SUCCESS "+companyName);
                    } else {
                        System.out.println("Request failed for company: " + companyName);
                        success = true; // Không retry nếu lỗi khác 429
                    }
                } catch (Exception e) {
                    if (e.getMessage() != null && e.getMessage().contains("429")) {
                        System.out.println("429 Too Many Requests for company: " + companyName + ", retrying...");
                        retry++;
//                        Thread.sleep(500); // Đợi 2 giây rồi thử lại
                    } else {
                        System.out.println("Error for company: " + companyName + " → " + e.getMessage());
                        success = true; // Không retry nếu lỗi khác
//                        Thread.sleep(500);
                    }
                }
            }
            Thread.sleep(1000); // Đợi 1.3 giây giữa các lần gọi để tránh vượt limit
        }

        // Auto-size cột
        for (int i = 0; i < columns.length; i++) {
            outputSheet.autoSizeColumn(i);
        }

        // Lưu file Excel kết quả
        try (FileOutputStream fileOut = new FileOutputStream("C:\\Users\\Admin\\Downloads\\org_url_4.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();

        System.out.println("Xuất file Excel thành công!");
    }


}

