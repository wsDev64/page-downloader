package com.oracle.techtask.service.jmeter;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.protocol.http.parser.BaseParser;
import org.apache.jmeter.protocol.http.parser.LinkExtractorParseException;
import org.apache.jmeter.protocol.http.parser.LinkExtractorParser;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.util.ConversionUtils;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.util.JOrphanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class HtmlDownloaderClient extends HTTPSampler {

    private static final Logger log = LoggerFactory.getLogger(HtmlDownloaderClient.class);

    private HTTPSampleResult httpSampleResult;
    private Map<String, String> parsersForContentType = new HashMap<>();
    private final transient Environment env;

    public HtmlDownloaderClient(Environment env) {
        this.env = env;
        init();
    }

    private void init(){
        setImageParser(true);
        String[] parsers = JOrphanUtils.split("htmlParser wmlParser cssParser", " ", true);// returns empty array for null
        for (final String parser : parsers) {
            String classname = env.getProperty(parser + ".className");//$NON-NLS-1$
            if (classname == null) {
                continue;
            }
            String typeList = env.getProperty(parser + ".types");//$NON-NLS-1$
            if (typeList != null) {
                String[] types = JOrphanUtils.split(typeList, " ", true);
                for (final String type : types) {
                    parsersForContentType.put(type, classname);
                }
            }
        }
    }

    @Override
    public HTTPSampleResult sample(URL u, String method, boolean areFollowingRedirect, int depth) {
        httpSampleResult = super.sample(u, method, true, depth);
        return httpSampleResult;
    }

    public HTTPSampleResult getHttpSampleResult(){
        return httpSampleResult;
    }

    public Long getTotalLengthAsLong() {
        return httpSampleResult.getBytesAsLong();
    }

    private static final String USER_AGENT = "User-Agent";

    private LinkExtractorParser getParser(HTTPSampleResult res) throws LinkExtractorParseException {
        String parserClassName =
                parsersForContentType.get(res.getMediaType());
        if (!StringUtils.isEmpty(parserClassName)) {
            return BaseParser.getParser(parserClassName);
        }
        return null;
    }

    private String getUserAgent(HTTPSampleResult sampleResult) {
        String res = sampleResult.getRequestHeaders();
        int index = res.indexOf(USER_AGENT);
        if (index >= 0) {

            final String userAgentPrefix = USER_AGENT + ": ";
            String userAgentHdr = res.substring(
                    index + userAgentPrefix.length(),
                    res.indexOf('\n',
                            index + userAgentPrefix.length() + 1));
            return userAgentHdr.trim();
        } else {
            if (log.isInfoEnabled()) {
                log.info("No user agent extracted from requestHeaders:{}", res);
            }
            return null;
        }
    }

    @Override
    protected HTTPSampleResult downloadPageResources(final HTTPSampleResult pRes, final HTTPSampleResult container, final int frameDepth) {
        HTTPSampleResult res = pRes;
        Iterator<URL> urls = new ArrayList<URL>().iterator();
        try {
            final byte[] responseData = res.getResponseData();
            if (responseData.length > 0) {
                final LinkExtractorParser parser = getParser(res);
                if (parser != null) {
                    String userAgent = getUserAgent(res);
                    urls = parser.getEmbeddedResourceURLs(userAgent, responseData, res.getURL(), res.getDataEncodingWithDefault());
                }
            }
        } catch (LinkExtractorParseException e) {
            setParentSampleSuccess(res, false);
        }
        HTTPSampleResult lContainer = container;
        if (urls.hasNext()) {
            if (lContainer == null) {
                lContainer = new HTTPSampleResult(res);
                lContainer.addRawSubResult(res);
            }
            res = lContainer;
        }

        while (urls.hasNext()) {
            Object binURL = urls.next();
            URL url = (URL) binURL;
            if (url == null) {
                log.warn("Null URL detected (should not happen)");
            } else {
                try {
                    url = escapeIllegalURLCharacters(url);
                } catch (Exception e) {
                    continue;
                }

                try {
                    url = url.toURI().normalize().toURL();
                } catch (MalformedURLException | URISyntaxException e) {
                    setParentSampleSuccess(res, false);
                    continue;
                }

                HTTPSampleResult binRes = sample(url, HTTPConstants.GET, false, frameDepth + 1);
                res.addSubResult(binRes);
                setParentSampleSuccess(res, res.isSuccessful() && (binRes == null || binRes.isSuccessful()));
            }
        }

        return res;
    }

    private URL escapeIllegalURLCharacters(java.net.URL url) {
        if (url == null || "file".equals(url.getProtocol())) {
            return url;
        }
        try {
            return ConversionUtils.sanitizeUrl(url).toURL();
        } catch (Exception e1) { // NOSONAR
            log.error("Error escaping URL:'{}', message:{}", url, e1.getMessage());
            return url;
        }
    }

    private void setParentSampleSuccess(HTTPSampleResult res, boolean initialValue) {
        res.setSuccessful(initialValue);
        if (!initialValue) {
            StringBuilder detailedMessage = new StringBuilder(80);
            detailedMessage.append("Embedded resource download error:"); //$NON-NLS-1$
            for (SampleResult subResult : res.getSubResults()) {
                HTTPSampleResult httpSampleResult = (HTTPSampleResult) subResult;
                if (!httpSampleResult.isSuccessful()) {
                    detailedMessage.append(httpSampleResult.getURL())
                            .append(" code:") //$NON-NLS-1$
                            .append(httpSampleResult.getResponseCode())
                            .append(" message:") //$NON-NLS-1$
                            .append(httpSampleResult.getResponseMessage())
                            .append(", "); //$NON-NLS-1$
                }
            }
            res.setResponseMessage(detailedMessage.toString()); //$NON-NLS-1$
        }
    }
}
