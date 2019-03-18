package com.oracle.techtask.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileSize {

    public FileSize(Long size) {
        this.size = size;
    }

    @JsonProperty("size")
    private Long size;



}
