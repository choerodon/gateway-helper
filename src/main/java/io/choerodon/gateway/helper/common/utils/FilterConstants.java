package io.choerodon.gateway.helper.common.utils;

/**
 * filter的一些配置
 * @author flyleft
 * @date 17-12-29
 */
public interface FilterConstants {

    String ACCESS_TOKEN = "ACCESS_TOKEN";

    String EUREKA_LABEL = "EUREKA_LABEL";

    String ANONYMOUS_PATTERN = "/**/public/**";

    String ANONYMOUS_PATTERN_OTHER = "/**/v1/**";

    int ACCESS_TOKEN_VERIFY_FILTER = 0;

}
