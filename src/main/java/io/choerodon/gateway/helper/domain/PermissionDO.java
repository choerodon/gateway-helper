package io.choerodon.gateway.helper.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@ModifyAudit
@VersionAudit
@Table(name = "iam_permission")
public class PermissionDO extends AuditDomain implements Serializable {

    private static final long serialVersionUID = -4108102602163313984L;

    @Id
    @GeneratedValue
    private Long id;

    private String path;

    private String method;

    private Boolean publicAccess;

    private Boolean loginAccess;

    private Boolean within;

    private String fdLevel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public Boolean getLoginAccess() {
        return loginAccess;
    }

    public void setLoginAccess(Boolean loginAccess) {
        this.loginAccess = loginAccess;
    }

    public Boolean getWithin() {
        return within;
    }

    public void setWithin(Boolean within) {
        this.within = within;
    }

    public String getFdLevel() {
        return fdLevel;
    }

    public void setFdLevel(String fdLevel) {
        this.fdLevel = fdLevel;
    }

    public PermissionDO() {
    }

    public PermissionDO(String path, String method, Boolean publicAccess, Boolean loginAccess, Boolean within, String fdLevel) {
        this.path = path;
        this.method = method;
        this.publicAccess = publicAccess;
        this.loginAccess = loginAccess;
        this.within = within;
        this.fdLevel = fdLevel;
    }

    @Override
    public String toString() {
        return "PermissionDO{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", publicAccess=" + publicAccess +
                ", loginAccess=" + loginAccess +
                ", within=" + within +
                ", level='" + fdLevel + '\'' +
                '}';
    }
}