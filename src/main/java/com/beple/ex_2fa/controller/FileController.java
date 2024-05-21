package com.beple.ex_2fa.controller;

import com.beple.ex_2fa.payload.BaseResponse;
import com.beple.ex_2fa.service.file.FileService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@ApiResponse(description = "manage files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @RequestMapping(
            path = "/uploadFile",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @RequestMapping(
            path = "/uploadMultiFile",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return fileService.uploadMultipleFiles(files);
    }

    @PostMapping("/delete/{name}")
    public BaseResponse delete(@PathVariable String name) {
        return fileService.deleteFileByFileName(name);
    }

    @GetMapping("/image/{filename:.+}")
    public Object serveFile(@PathVariable String filename) throws IOException {
        return fileService.findByFileName(filename);
    }

    @GetMapping("/file/{filename:.+}")
    @ResponseBody
    public Object downloadFile(@PathVariable String filename) {
        return fileService.downloadFile(filename);
    }
}
