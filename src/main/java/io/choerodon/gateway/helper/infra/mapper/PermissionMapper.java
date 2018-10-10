package io.choerodon.gateway.helper.infra.mapper;

import io.choerodon.gateway.helper.domain.PermissionDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限mapper
 *
 * @author flyleft
 */
public interface PermissionMapper extends BaseMapper<PermissionDO> {


    List<PermissionDO> selectPermissionByMethodAndService(@Param("method") String method,
                                                          @Param("service") String service);

    List<Long> selectSourceIdsByUserIdAndPermission(@Param("userId") long userId,
                                                    @Param("permissionId") long permissionId,
                                                    @Param("sourceType") String sourceType);

}
