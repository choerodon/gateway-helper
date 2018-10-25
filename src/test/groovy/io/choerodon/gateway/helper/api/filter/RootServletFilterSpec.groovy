package io.choerodon.gateway.helper.api.filter

import io.choerodon.core.exception.CommonException
import io.choerodon.gateway.helper.domain.CheckState
import io.choerodon.gateway.helper.domain.RequestContext
import io.choerodon.gateway.helper.infra.exception.PermissionMultiplyMatchException
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification

import static io.choerodon.core.variable.RequestVariableHolder.HEADER_JWT

class RootServletFilterSpec extends Specification {

    def "test do Filter"() {
        given: '准备request和response'
        def req = new MockHttpServletRequest('GET', '/iam/v1/test')
        def res = new MockHttpServletResponse()

        and: '创建RootServletFilter'
        def rootServletFilter = new RootServletFilter(Optional.of(Collections.singletonList(new PmmFilter())))

        when: '抛出PermissionMultiplyMatchException异常时调用'
        rootServletFilter.doFilter(req, res, null)

        then: '判断状态码和header'
        res.status == 500
        res.getHeader('request-status') == 'API_ERROR_MATCH_MULTIPLY'

        when: '抛出Exception异常时调用'
        rootServletFilter.setHelperFilters(Collections.singletonList(new ExceptionFilter()))
        res.setCommitted(false)
        rootServletFilter.doFilter(req, res, null)

        then: '判断状态码和header'
        res.status == 500
        res.getHeader('request-status') == 'EXCEPTION_GATEWAY_HELPER'

        when: '200时调用'
        rootServletFilter.setHelperFilters(Collections.singletonList(new TwoXXFilter()))
        res.setCommitted(false)
        rootServletFilter.doFilter(req, res, null)

        then: '判断状态码和header'
        res.status == 200
        res.getHeader('request-status') == 'SUCCESS_PUBLIC_ACCESS'
        res.getHeader('request-code') != null
        res.getHeader('request-message') != null
        res.getHeader(HEADER_JWT) != null

        when: '403时调用'
        rootServletFilter.setHelperFilters(Collections.singletonList(new ThreeXXFilter()))
        res.setCommitted(false)
        rootServletFilter.doFilter(req, res, null)

        then: '判断状态码和header'
        res.status == 403
        res.getHeader('request-status') == 'PERMISSION_MISMATCH'
        res.getHeader('request-code') != null
        res.getHeader('request-message') != null
        res.getHeader(HEADER_JWT) != null
    }

    class PmmFilter implements HelperFilter {
        @Override
        int filterOrder() {
            return 0
        }

        @Override
        boolean shouldFilter(RequestContext context) {
            return true
        }

        @Override
        boolean run(RequestContext context) {
            throw new PermissionMultiplyMatchException('/iam/v1/test', 'get', Collections.emptyList())
        }
    }

    class ExceptionFilter implements HelperFilter {
        @Override
        int filterOrder() {
            return 0
        }

        @Override
        boolean shouldFilter(RequestContext context) {
            return true
        }

        @Override
        boolean run(RequestContext context) {
            throw new CommonException('')
        }
    }

    class TwoXXFilter implements HelperFilter {
        @Override
        int filterOrder() {
            return 0
        }

        @Override
        boolean shouldFilter(RequestContext context) {
            return true
        }

        @Override
        boolean run(RequestContext context) {
            context.response.setStatus(CheckState.SUCCESS_PUBLIC_ACCESS)
            context.response.setMessage('test')
            context.response.setJwt('test')
            return false
        }
    }

    class ThreeXXFilter implements HelperFilter {
        @Override
        int filterOrder() {
            return 0
        }

        @Override
        boolean shouldFilter(RequestContext context) {
            return true
        }

        @Override
        boolean run(RequestContext context) {
            context.response.setStatus(CheckState.PERMISSION_MISMATCH)
            context.response.setMessage('test')
            context.response.setJwt('test')
            return false
        }
    }
}