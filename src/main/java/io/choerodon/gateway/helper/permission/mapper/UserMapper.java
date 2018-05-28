package io.choerodon.gateway.helper.permission.mapper;

import io.choerodon.gateway.helper.permission.domain.UserDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by xausky on 3/17/17.
 */
public interface UserMapper extends BaseMapper<UserDO> {

    @Select({"select is_admin from iam_user where id = #{id}"})
    Boolean isAdmin(@Param("id") long id);
}