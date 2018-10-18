package io.choerodon.gateway.helper.cache.bridge;

import io.choerodon.gateway.helper.cache.l2.L2Cache;

public interface L1ToL2Bridge {

    L2Cache next();

}
