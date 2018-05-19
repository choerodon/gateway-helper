package io.choerodon.gateway.helper.common.mapper;

import java.util.List;

import io.choerodon.gateway.helper.common.domain.GroupDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface GroupMapper extends BaseMapper<GroupDO> {

    List<String> selectGroupsByUser(@Param("userId") Long userId);

}
