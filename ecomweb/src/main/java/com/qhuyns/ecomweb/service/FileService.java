package com.qhuyns.ecomweb.service;


import com.qhuyns.ecomweb.constant.ImagePrefix;
import com.qhuyns.ecomweb.dto.request.RoleRequest;
import com.qhuyns.ecomweb.dto.response.RoleResponse;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {

    public String uploadImage(MultipartFile file) {
        try {
            // Lấy đường dẫn tuyệt đối tới thư mục uploads (nên dùng ngoài resources)
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs(); // Tạo đầy đủ thư mục cha nếu chưa có
            }
            // Tạo tên file duy nhất
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String uniqueName = java.util.UUID.randomUUID().toString() + extension;
            String filePath = uploadDir + uniqueName;
            File destFile = new File(filePath);
            // Đảm bảo thư mục cha của file tồn tại (phòng trường hợp uploadDir có nhiều cấp)
            destFile.getParentFile().mkdirs();
            file.transferTo(destFile);
            return ImagePrefix.IMAGE_PREFIX+uniqueName; // hoặc trả về đường dẫn truy cập ảnh nếu cần
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.CANT_NOT_UPLOAD);
        }
    }

    public void deleteImage(String url) {
        try {
            // Nếu bạn lưu tên file, tách tên file từ url
            String fileName = Paths.get(new URI(url).getPath()).getFileName().toString();
            File file = new File("uploads/" + fileName);
            if (file.exists()) {
                file.delete();
                // Nếu có lưu DB, xóa record liên quan ở đây

            } else {

            }
        } catch (Exception e) {

        }
    }
}
