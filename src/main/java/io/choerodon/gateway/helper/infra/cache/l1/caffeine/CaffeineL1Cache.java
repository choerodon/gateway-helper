package io.choerodon.gateway.helper.infra.cache.l1.caffeine;

import io.choerodon.gateway.helper.infra.cache.l1.L1Cache;

public class CaffeineL1Cache extends L1Cache {

    public CaffeineL1Cache(org.springframework.cache.Cache cache) {
        super(cache);
    }


}
