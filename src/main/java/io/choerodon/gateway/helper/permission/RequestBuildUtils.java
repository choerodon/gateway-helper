//package io.choerodon.gateway.helper.permission;
//
//import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.REQUEST_URI_KEY;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.net.URLDecoder;
//import java.util.*;
//import javax.servlet.http.HttpServletRequest;
//
//import com.netflix.hystrix.exception.HystrixRuntimeException;
//import com.netflix.zuul.context.RequestContext;
//import com.netflix.zuul.exception.ZuulException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer;
//import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommand;
//import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandContext;
//import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.util.StringUtils;
//import org.springframework.web.util.UriUtils;
//import org.springframework.web.util.WebUtils;
//
///**
// * @author flyleft
// * @date 2018/4/26
// */
//public class RequestBuildUtils {
//
//    private RequestBuildUtils() {
//    }
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBuildUtils.class);
//
//    private static final String ENCODING_UTF8 = "UTF-8";
//
//    public static RibbonCommandContext buildCommandContext(final List<RibbonRequestCustomizer> requestCustomizers,
//                                                           final HttpServletRequest req,
//                                                           final Boolean retryable,
//                                                           final String forwardServiceId) {
//        String verb = getVerb(req);
//        String uri = buildZuulRequestUri(req);
//        MultiValueMap<String, String> headers = buildZuulRequestHeaders(req);
//        MultiValueMap<String, String> params = buildZuulRequestQueryParams(req);
//        InputStream requestEntity;
//        long contentLength;
//        requestEntity = new ByteArrayInputStream("".getBytes());
//        contentLength = 0L;
//        return new RibbonCommandContext(forwardServiceId, verb, uri, retryable, headers, params,
//                requestEntity, requestCustomizers, contentLength);
//    }
//
//    public static ClientHttpResponse forward(RibbonCommandFactory<?> ribbonCommandFactory,
//                                             RibbonCommandContext context) throws ZuulException {
//        RibbonCommand command = ribbonCommandFactory.create(context);
//        try {
//            return command.execute();
//        } catch (HystrixRuntimeException ex) {
//            throw new ZuulException(ex, "Forwarding service error", 500, ex.getMessage());
//        }
//    }
//
//    private static String getVerb(HttpServletRequest request) {
//        String method = request.getMethod();
//        if (method == null) {
//            return "GET";
//        }
//        return method;
//    }
//
//
//    private static MultiValueMap<String, String> buildZuulRequestHeaders(HttpServletRequest request) {
//        MultiValueMap<String, String> headers = new HttpHeaders();
//        Enumeration<String> headerNames = request.getHeaderNames();
//        if (headerNames != null) {
//            while (headerNames.hasMoreElements()) {
//                String name = headerNames.nextElement();
//                if (isIncludedHeader(name)) {
//                    Enumeration<String> values = request.getHeaders(name);
//                    while (values.hasMoreElements()) {
//                        String value = values.nextElement();
//                        headers.add(name, value);
//                    }
//                }
//            }
//        }
//        headers.set(HttpHeaders.ACCEPT_ENCODING, "gzip");
//        return headers;
//    }
//
//    private static boolean isIncludedHeader(String headerName) {
//        String name = headerName.toLowerCase();
//        switch (name) {
//            case "host":
//            case "connection":
//            case "content-length":
//            case "content-encoding":
//            case "server":
//            case "transfer-encoding":
//            case "x-application-context":
//                return false;
//            default:
//                return true;
//        }
//    }
//
//    private static MultiValueMap<String, String> buildZuulRequestQueryParams(HttpServletRequest request) {
//        Map<String, List<String>> map = new HashMap<>(1 << 4);
//        String queryString = request.getQueryString();
//        if (StringUtils.isEmpty(queryString)) {
//            queryString = "";
//        }
//        StringTokenizer st = new StringTokenizer(queryString, "&");
//        int i;
//        while (st.hasMoreTokens()) {
//            String s = st.nextToken();
//            i = s.indexOf('=');
//            if (i > 0 && s.length() >= i + 1) {
//                String name = s.substring(0, i);
//                String value = s.substring(i + 1);
//                try {
//                    name = URLDecoder.decode(name, ENCODING_UTF8);
//                    value = URLDecoder.decode(value, ENCODING_UTF8);
//                } catch (Exception e) {
//                    LOGGER.info("Uri decode error, {}", e.getMessage());
//                }
//                map.computeIfAbsent(name, k -> new LinkedList<>()).add(value);
//            } else if (i == -1) {
//                String name = s;
//                String value = "";
//                try {
//                    name = URLDecoder.decode(name, ENCODING_UTF8);
//                } catch (Exception e) {
//                    LOGGER.info("Uri decode error, {}", e.getMessage());
//                }
//                map.computeIfAbsent(name, k -> new LinkedList<>()).add(value);
//            }
//        }
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//
//        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
//            for (String value : entry.getValue()) {
//                params.add(entry.getKey(), value);
//            }
//        }
//        return params;
//    }
//
//    private static String buildZuulRequestUri(HttpServletRequest request) {
//        RequestContext context = RequestContext.getCurrentContext();
//        String uri = request.getRequestURI();
//        String contextUri = (String) context.get(REQUEST_URI_KEY);
//        if (contextUri != null) {
//            try {
//                String encoding = request.getCharacterEncoding() != null ? request.getCharacterEncoding()
//                        : WebUtils.DEFAULT_CHARACTER_ENCODING;
//                uri = UriUtils.encodePath(contextUri, encoding).replace("//", "/");
//            } catch (Exception e) {
//                LOGGER.info("buildZuulRequestUri error, {}", e.getMessage());
//            }
//        }
//        return uri;
//    }
//}
