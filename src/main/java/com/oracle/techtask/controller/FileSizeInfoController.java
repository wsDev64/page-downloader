package com.oracle.techtask.controller;

import com.oracle.techtask.dto.api.FileSize;
import com.oracle.techtask.service.file.FileInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/file")
@RestController
public class FileSizeInfoController {

    private static final Logger log = LoggerFactory.getLogger(FileSizeInfoController.class);

    private FileInfoService fileInfoService;

    public FileSizeInfoController(@Autowired FileInfoService fileInfoService) {
        this.fileInfoService = fileInfoService;
    }

    @RequestMapping(path = "/size")
    public ResponseEntity<FileSize> getSize(String url) {
        if (log.isTraceEnabled()) {
            log.trace("Receive ulr ", url);
        }
        return ResponseEntity
                .ok(new FileSize(fileInfoService.getFileSize(url)));
    }

}
