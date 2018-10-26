package io.choerodon.gateway.helper.api.filter;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.gateway.helper.domain.CheckState;
import io.choerodon.gateway.helper.domain.PermissionDO;
import io.choerodon.gateway.helper.domain.RequestContext;
import io.choerodon.gateway.helper.infra.mapper.PermissionMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Map;

/**
 * 普通接口(除公共接口，loginAccess接口，内部接口以外的接口)
 * 普通用户(超级管理员之外用户)的权限校验
 */
@Component
public class CommonRequestCheckFilter implements HelperFilter {

    private static final String PROJECT_PATH_ID = "project_id";

    private static final String ORG_PATH_ID = "organization_id";


    private final AntPathMatcher matcher = new AntPathMatcher();

    private PermissionMapper permissionMapper;

    public CommonRequestCheckFilter(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    public void setPermissionMapper(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public int filterOrder() {
        return 80;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        PermissionDO permission = context.getPermission();
        List<Long> sourceIds = permissionMapper.selectSourceIdsByUserIdAndPermission(
                context.getCustomUserDetails().getUserId(),
                context.getPermission().getId(), context.getPermission().getFdLevel());
        if (sourceIds.isEmpty()) {
            context.response.setStatus(CheckState.PERMISSION_NOT_PASS);
            context.response.setMessage("No access to this interface");
        } else if (ResourceLevel.SITE.value().equals(permission.getFdLevel())) {
            context.response.setStatus(CheckState.SUCCESS_PASS_SITE);
            context.response.setMessage("Have access to this 'site-level' interface, permission: " + context.getPermission());
        } else if (ResourceLevel.PROJECT.value().equals(permission.getFdLevel())) {
            checkProjectPermission(context, sourceIds, permission.getPath());
        } else if (ResourceLevel.ORGANIZATION.value().equals(permission.getFdLevel())) {
            checkOrgPermission(context, sourceIds, permission.getPath());
        }
        return true;
    }

    private void checkProjectPermission(final RequestContext context,
                                        final List<Long> sourceIds,
                                        final String matchPath) {
        Long projectId = parseProjectOrOrgIdFromUri(context.getTrueUri(), matchPath, PROJECT_PATH_ID);
        if (projectId == null) {
            context.response.setStatus(CheckState.API_ERROR_PROJECT_ID);
            context.response.setMessage("Project interface must have 'project_id' in path");
        } else {
            if (sourceIds.stream().anyMatch(t -> t.equals(projectId))) {
                context.response.setStatus(CheckState.SUCCESS_PASS_PROJECT);
                context.response.setMessage("Have access to this 'project-level' interface, permission: " + context.getPermission());
            } else {
                context.response.setStatus(CheckState.PERMISSION_NOT_PASS_PROJECT);
                context.response.setMessage("No access to this this project, projectId: " + projectId);
            }
        }
    }

    private void checkOrgPermission(final RequestContext context,
                                    final List<Long> sourceIds,
                                    final String matchPath) {
        Long orgId = parseProjectOrOrgIdFromUri(context.getTrueUri(), matchPath, ORG_PATH_ID);
        if (orgId == null) {
            context.response.setStatus(CheckState.API_ERROR_ORG_ID);
            context.response.setMessage("Organization interface must have 'organization_id' in path");
        } else {
            if (sourceIds.stream().anyMatch(t -> t.equals(orgId))) {
                context.response.setStatus(CheckState.SUCCESS_PASS_ORG);
                context.response.setMessage("Have access to this 'organization-level' interface, permission: " + context.getPermission());
            } else {
                context.response.setStatus(CheckState.PERMISSION_NOT_PASS_ORG);
                context.response.setMessage("No access to this this organization, organizationId: " + orgId);
            }
        }
    }

    private Long parseProjectOrOrgIdFromUri(final String uri, final String matchPath, String id) {
        Map<String, String> map = matcher.extractUriTemplateVariables(matchPath, uri);
        if (map.size() < 1) {
            return null;
        }
        String value = map.get(id);
        if (value != null) {
            return Long.parseLong(value);
        }
        return null;
    }

}
