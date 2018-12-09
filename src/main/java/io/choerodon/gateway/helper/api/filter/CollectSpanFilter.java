package io.choerodon.gateway.helper.api.filter;

import io.choerodon.gateway.helper.domain.RequestContext;
import io.choerodon.gateway.helper.domain.TranceSpan;
import org.springframework.cloud.config.client.ZuulRoute;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * @author superlee
 */
@Component
public class CollectSpanFilter implements HelperFilter {

    private StringRedisTemplate stringRedisTemplate;

    public CollectSpanFilter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public int filterOrder() {
        return 15;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        ZuulRoute zuulRoute = context.getRoute();
        String trueUrl = context.getTrueUri();
        String serviceId = zuulRoute.getServiceId();
        String method = context.request.method;
        TranceSpan tranceSpan = new TranceSpan(trueUrl, serviceId, method, System.currentTimeMillis());
        Observable
                .just(tranceSpan)
                .subscribeOn(Schedulers.io())
                .subscribe(this::tranceSpanSubscriber);
        return true;
    }

    private void tranceSpanSubscriber(final TranceSpan tranceSpan) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(new Date(tranceSpan.getCurrentTimeMillis()));
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(date);
        keyBuilder.append(":");
        keyBuilder.append(tranceSpan.getService());
        String serviceKey = keyBuilder.toString();
        setCountByKey(serviceKey);
        keyBuilder.append(":");
        keyBuilder.append(tranceSpan.getUrl());
        keyBuilder.append(":");
        keyBuilder.append(tranceSpan.getMethod());
        String apiKey = keyBuilder.toString();
        setCountByKey(apiKey);
    }

    private void setCountByKey(String serviceKey) {
        if (stringRedisTemplate.hasKey(serviceKey)) {
            stringRedisTemplate.opsForValue().increment(serviceKey, 1);
        } else {
            //只展示30天数据设置采样记录过期时间为31天
            stringRedisTemplate.opsForValue().set(serviceKey, "1", 31, TimeUnit.DAYS);
        }
    }
}
