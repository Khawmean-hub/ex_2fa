package com.beple.ex_2fa.service.file;

import com.beple.ex_2fa.payload.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    BaseResponse uploadFile(MultipartFile file);
    BaseResponse uploadMultipleFiles(MultipartFile[] files);
    BaseResponse deleteFileByFileName(String fileName);

    Object findByFileName(String fileName) throws IOException;
    Object downloadFile(String fileName);
}
