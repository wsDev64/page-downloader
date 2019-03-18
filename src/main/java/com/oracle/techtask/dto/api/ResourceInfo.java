package com.oracle.techtask.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceInfo {
    @JsonProperty("size")
    private Long size;
    @JsonProperty("url")
    private String url;


    public ResourceInfo(Long size, String url) {
        this.size = size;
        this.url = url;
    }

    public ResourceInfo() {
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
