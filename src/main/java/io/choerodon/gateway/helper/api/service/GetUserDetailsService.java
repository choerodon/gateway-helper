package io.choerodon.gateway.helper.api.service;

import io.choerodon.gateway.helper.domain.CustomUserDetailsWithResult;

public interface GetUserDetailsService {

    CustomUserDetailsWithResult getUserDetails(String accessToken);

}
