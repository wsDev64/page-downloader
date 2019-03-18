package com.oracle.techtask.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TotalResourceInfo {

    @JsonProperty("resourcesInfo")
    private List<ResourceInfo> resourcesInfo;
    @JsonProperty("totalSize")
    private Long totalBytes;
    @JsonProperty("resource")
    private String resourceName;


    public TotalResourceInfo(Long totalBytes, String resourceName) {
       this.resourceName = resourceName;
       this.totalBytes =totalBytes;
    }

    public TotalResourceInfo() {
    }

    public Long getTotalBytes() {
        return totalBytes;
    }
    public void setTotalBytes(Long totalBytes) {
        this.totalBytes = totalBytes;
    }


    public List<ResourceInfo> getResourcesInfo() {
        return resourcesInfo;
    }

    public void setResourcesInfo(List<ResourceInfo> resourcesInfo) {
        this.resourcesInfo = resourcesInfo;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void addResourceInfo(ResourceInfo resourceInfo){
        if (resourceInfo == null) return;
        resourcesInfo.add(resourceInfo);
    }
}
