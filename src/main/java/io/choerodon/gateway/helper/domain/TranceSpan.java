package io.choerodon.gateway.helper.domain;

/**
 * @author superlee
 */
public class TranceSpan {

    private String url;

    private String service;

    private String method;

    private Long currentTimeMillis;

    public TranceSpan() {}

    public TranceSpan(String url, String service, String method, Long currentTimeMillis) {
        this.url = url;
        this.service = service;
        this.method = method;
        this.currentTimeMillis = currentTimeMillis;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(Long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    @Override
    public String toString() {
        return "TranceSpan{" +
                "url='" + url + '\'' +
                ", service='" + service + '\'' +
                ", method='" + method + '\'' +
                ", currentTimeMillis=" + currentTimeMillis +
                '}';
    }
}
