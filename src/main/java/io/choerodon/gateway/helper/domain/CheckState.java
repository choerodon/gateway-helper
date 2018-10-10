
package io.choerodon.gateway.helper.domain;

public enum CheckState {

    SUCCESS_PASS_SITE(201, "success.permission.sitePass"),

    SUCCESS_PASS_PROJECT(202, "success.permission.projectPass"),

    SUCCESS_PASS_ORG(203, "success.permission.organizationPass"),

    SUCCESS_SKIP_PATH(204, "success.permission.skipPath"),

    SUCCESS_PUBLIC_ACCESS(205, "success.permission.publicAccess"),

    SUCCESS_LOGIN_ACCESS(206, "success.permission.loginAccess"),

    SUCCESS_ADMIN(207, "success.permission.adminUser"),

    SUCCESS_PERMISSION_DISABLED(208, "success.permission.disabled"),

    PERMISSION_SERVICE_ROUTE(401, "error.permission.routeNotFound"),

    PERMISSION_WITH_IN(402, "error.permission.withinForbidden"),

    PERMISSION_MISMATCH(403, "error.permission.mismatch"),

    PERMISSION_NOT_PASS(404, "error.permission.notPass"),

    PERMISSION_NOT_PASS_PROJECT(405, "error.permission.projectNotPass"),

    PERMISSION_NOT_PASS_ORG(406, "error.permission.organizationNotPass"),

    RATE_LIMIT_NOT_PASS(301, "error.visit.frequent"),

    EXCEPTION_GATEWAY_HELPER(501, "error.gatewayHelper.exception"),

    EXCEPTION_OAUTH_SERVER(502, "error.oauthServer.exception"),

    API_ERROR_PROJECT_ID(503, "error.api.projectId"),

    API_ERROR_ORG_ID(504, "error.api.orgId"),

    API_ERROR_MATCH_MULTIPLY(505, "error.api.matchMultiplyPermission");


    private final int value;

    private final String code;

    CheckState(int value, String code) {
        this.value = value;
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }
}

