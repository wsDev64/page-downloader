package com.oracle.techtask.controller;


import com.oracle.techtask.dto.api.FileSize;
import com.oracle.techtask.dto.api.TotalResourceInfo;
import com.oracle.techtask.service.file.FileInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/page")
@RestController
public class PageSizeController {

    private static final Logger log = LoggerFactory.getLogger(FileSizeInfoController.class);

    private FileInfoService fileInfoService;

    public PageSizeController(@Autowired FileInfoService fileInfoService) {
        this.fileInfoService = fileInfoService;
    }

    @RequestMapping(path = "/size")
    public ResponseEntity<TotalResourceInfo> getSize(String url) {
        return ResponseEntity
                .ok(fileInfoService.getPageSize(url));
    }
}
