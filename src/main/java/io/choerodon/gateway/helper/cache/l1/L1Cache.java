package io.choerodon.gateway.helper.cache.l1;

import io.choerodon.gateway.helper.cache.bridge.L1ToL2Bridge;

public interface L1Cache {

    String type();

    L1ToL2Bridge bridge();

}
