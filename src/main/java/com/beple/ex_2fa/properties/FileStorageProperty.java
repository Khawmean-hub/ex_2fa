package com.beple.ex_2fa.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
@Data
public class FileStorageProperty {
    private String uploadDir;
    private String serverPath;
}
