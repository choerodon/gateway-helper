package io.choerodon.gateway.helper.api.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.gateway.helper.domain.PermissionDO;
import io.choerodon.gateway.helper.domain.RequestContext;
import io.choerodon.gateway.helper.domain.TranceSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.config.client.ZuulRoute;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author superlee
 */
@Component
public class CollectSpanFilter implements HelperFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectSpanFilter.class);

    private StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CollectSpanFilter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public int filterOrder() {
        return 25;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        ZuulRoute zuulRoute = context.getRoute();
        PermissionDO permissionDO = context.getPermission();
        String serviceId = zuulRoute.getServiceId();
        String method = context.request.method;
        TranceSpan tranceSpan = new TranceSpan(permissionDO.getPath(), serviceId, method, System.currentTimeMillis());
        Observable
                .just(tranceSpan)
                .subscribeOn(Schedulers.io())
                .subscribe(this::tranceSpanSubscriber);
        return true;
    }

    private void tranceSpanSubscriber(final TranceSpan tranceSpan) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(new Date(tranceSpan.getCurrentTimeMillis()));
        String service = tranceSpan.getService();
        staticInvokeCount(date, service);
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(date);
        keyBuilder.append(":");
        keyBuilder.append(tranceSpan.getService());
        String serviceKey = keyBuilder.toString();
        StringBuilder builder = new StringBuilder();
        builder.append(tranceSpan.getUrl()).append(":").append(tranceSpan.getMethod());
        String apiKey = builder.toString();
        staticInvokeCount(serviceKey, apiKey);
    }

    private void staticInvokeCount(String redisKey, String mapKey) {
        if (stringRedisTemplate.hasKey(redisKey)) {
            String value = stringRedisTemplate.opsForValue().get(redisKey);
            try {
                Map<String, Integer> map = objectMapper.readValue(value, new TypeReference<Map<String, Integer>>() {});
                if (map.get(mapKey) != null) {
                    map.put(mapKey, map.get(mapKey) + 1);
                } else {
                    map.put(mapKey, 1);
                }
                stringRedisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(map));
            } catch (IOException e) {
                LOGGER.error("object mapper read value to map error, redis key {}, value {}, exception :: {}", redisKey, value, e);
            }
        } else {
            Map<String, Integer> map = new HashMap<>();
            map.put(mapKey, 1);
            try {
                stringRedisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(map), 31, TimeUnit.DAYS);
            } catch (JsonProcessingException e) {
                LOGGER.error("object mapper write value as string error, map key {}, value 1, exception :: {}", mapKey, e);
            }
        }
    }
}
