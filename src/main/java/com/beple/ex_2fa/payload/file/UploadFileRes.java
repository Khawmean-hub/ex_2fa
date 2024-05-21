package com.beple.ex_2fa.payload.file;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UploadFileRes {
    private String fileName;
    private String fileDownloadUri;
    private String fileView;
    private String fileType;
    private long size;
}
