package io.choerodon.gateway.helper.permission.mapper;

import io.choerodon.gateway.helper.permission.domain.PermissionDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限mapper
 * @author flyleft
 */
public interface PermissionMapper extends BaseMapper<PermissionDO> {

    /**
     * 获取用户在某服务中所有权限
     * @param userId 用户id
     * @param serviceName 服务名
     * @return 权限列表
     */
    List<PermissionDO> selectByUserIdAndServiceName(@Param("userId") Long userId,
                                                    @Param("serviceName") String serviceName);

    /**
     * 获取服务名下的public和login权限
     * @param serviceName 服务名
     * @return 权限列表
     */
    List<PermissionDO> selectPublicOrLoginAccessPermissionsByServiceName(@Param("serviceName") String serviceName);

}
