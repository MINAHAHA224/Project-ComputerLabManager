package com.example.computerweb.services;

import jakarta.servlet.ServletContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class UploadService {
//    private final ServletContext servletContext;
//
//    public UploadService(ServletContext servletContext) {
//        this.servletContext = servletContext;
//    }
//
//    public String handleUploadFile(MultipartFile file, String targetFolder) {
//        if (file.isEmpty()) {
//            return "";
//        }
//        String rootPath = "D:/ThucTapCoSo_Lop/computerweb/src/main/resources/images";
//        String finalName = "";
//        try {
//            byte[] bytes;
//            bytes = file.getBytes();
//
//            File dir = new File(rootPath + File.separator + targetFolder);
//            if (!dir.exists())
//                dir.mkdirs();
//            // Create the file on server
//            finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
//            File serverFile = new File(dir.getAbsolutePath() + File.separator + finalName);
//
//            BufferedOutputStream stream = new BufferedOutputStream(
//                    new FileOutputStream(serverFile));
//            stream.write(bytes);
//            stream.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return finalName;
//    }
private final String UPLOAD_DIRECTORY = "D:/computerweb_uploads/";

    public String handleUploadFile(MultipartFile file, String targetFolder) {
        if (file.isEmpty()) return "";
        try {
            File uploadDir = new File(UPLOAD_DIRECTORY + targetFolder);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            File serverFile = new File(uploadDir, finalName);

            file.transferTo(serverFile);

            return finalName;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
