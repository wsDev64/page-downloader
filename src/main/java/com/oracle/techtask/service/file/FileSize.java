package com.oracle.techtask.service.file;

import com.oracle.techtask.dto.api.TotalResourceInfo;

public interface FileSize {
    Long getFileSize(String url);
    TotalResourceInfo getPageSize(String url);
}
