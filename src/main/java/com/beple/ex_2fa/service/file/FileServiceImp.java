package com.beple.ex_2fa.service.file;

import com.beple.ex_2fa.exception.CustomException;
import com.beple.ex_2fa.payload.BaseResponse;
import com.beple.ex_2fa.payload.file.UploadFileRes;
import com.beple.ex_2fa.properties.FileStorageProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileServiceImp implements FileService{
    private final String serverPath;
    private final Path fileStorageLocation;

    @Autowired
    public FileServiceImp(FileStorageProperty properties) {
        if(properties.getUploadDir() ==null || properties.getUploadDir().isBlank()){
            throw new CustomException("File upload location can not be Empty.");
        }
        this.fileStorageLocation = Paths.get(properties.getUploadDir());
        this.serverPath = properties.getServerPath();
    }
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new CustomException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new CustomException("Could not store file " + fileName + ". Please try again!");
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new CustomException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new CustomException("File not found " + fileName);
        }
    }

    @Override
    public BaseResponse uploadFile(MultipartFile file){
        String fileName = storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/file/").path(fileName).toUriString();
        String viewFile = ServletUriComponentsBuilder.fromCurrentContextPath().path("/image/").path(fileName).toUriString();

        return BaseResponse.builder()
                .rec(UploadFileRes.builder()
                        .fileName(fileName)
                        .fileDownloadUri(fileDownloadUri)
                        .fileView(viewFile)
                        .fileType(file.getContentType())
                        .size(file.getSize())
                        .build())
                .build();
    }

    @Override
    public BaseResponse uploadMultipleFiles(MultipartFile[] files){
        List<UploadFileRes> res = new ArrayList<>();
        Arrays.stream(files).forEach(file -> {
            res.add((UploadFileRes) uploadFile(file).getRec());
        });
        return BaseResponse.builder()
                .rec(res)
                .build();
    }

    @Override
    public BaseResponse deleteFileByFileName(String fileName){
        try (Stream<Path> walk = Files.walk(Paths.get(serverPath))) {
            List<String> result = walk.filter(Files::isRegularFile).map(Path::toString).toList();
            for (String file : result) {
                File file2 = new File(file);
                if (file2.getName().equals(fileName)) {
                    file2.delete();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return BaseResponse.builder().build();
    }

    @Override
    public Object findByFileName(String fileName) throws IOException {
        Resource file = loadFileAsResource(fileName);
        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(file.getFile().toPath())))
                .body(file.getContentAsByteArray());
    }

    @Override
    public Object downloadFile(String fileName) {
        Resource file = loadFileAsResource(fileName);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}

