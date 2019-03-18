package com.oracle.techtask.service.file;

import com.oracle.techtask.dto.api.ResourceInfo;
import com.oracle.techtask.dto.api.TotalResourceInfo;
import com.oracle.techtask.exception.BadPathException;
import com.oracle.techtask.exception.SizeUndefined;
import com.oracle.techtask.service.jmeter.HtmlDownloaderClient;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


@Component
public class FileInfoService implements FileSize {

    private static Logger log = LoggerFactory.getLogger(FileInfoService.class);

    @Autowired
    private Environment environment;

    @Override
    public Long getFileSize(String path) {
        URLConnection conn = null;
        try {
            URL url = new URL(path);
            conn = url.openConnection();

            if (conn instanceof HttpURLConnection) {
                ((HttpURLConnection) conn).setInstanceFollowRedirects(true);
                ((HttpURLConnection) conn).setRequestMethod("HEAD");
            }


            Long contentLength = conn.getContentLengthLong();

            if (contentLength.equals(-1L)) throw new SizeUndefined(MessageFormat.format("Size for {0}, is undefined", path));
            return contentLength;
        } catch (IOException e) {
            throw new BadPathException(e.getMessage());
        } finally {
            if (conn != null && conn instanceof HttpURLConnection) {
                ((HttpURLConnection) conn).disconnect();
            }
        }
    }

    @Override
    public TotalResourceInfo getPageSize(String path) {
        try {
            URL url = new URL(path);
            HtmlDownloaderClient client = new HtmlDownloaderClient(environment);
            client.sample(url, "GET", true, 0);
            SampleResult httpSampleResult = client.getHttpSampleResult();
            String redirectLocation = client.getHttpSampleResult().getRedirectLocation();
            if (redirectLocation != null && !path.equals(redirectLocation)){
                 return getPageSize(client.getHttpSampleResult().getRedirectLocation());
            }

            TotalResourceInfo totalResourceInfo = new TotalResourceInfo(client.getTotalLengthAsLong(), path);
            List<ResourceInfo> resourcesInfo = getResourcesInfo(httpSampleResult, new ArrayList<>());

            totalResourceInfo.setResourcesInfo(resourcesInfo);
            return totalResourceInfo;
        } catch (MalformedURLException e) {
           throw new BadPathException(e.getMessage());
        }
    }

    private List<ResourceInfo> getResourcesInfo(SampleResult result, List<ResourceInfo> resourcesInfo){
        for (SampleResult subResult : result.getSubResults()) {
            resourcesInfo.add(new ResourceInfo(subResult.getBytesAsLong(), subResult.getUrlAsString()));
            getResourcesInfo(subResult, resourcesInfo);
        }

        return resourcesInfo;
    }
}
