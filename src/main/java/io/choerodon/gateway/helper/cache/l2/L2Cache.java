package io.choerodon.gateway.helper.cache.l2;

import io.choerodon.gateway.helper.cache.bridge.L2ToSourceBridge;

public interface L2Cache {

    String type();

    L2ToSourceBridge bridge();

}
