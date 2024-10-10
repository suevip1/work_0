package com.dtstack.engine.master.router.login;

import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.RoleService;
import com.dtstack.engine.master.router.permission.Authenticate;
import com.dtstack.engine.master.router.util.CookieUtil;
import com.dtstack.pubsvc.sdk.authcenter.AuthCenterAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.authcenter.AuthPermissionParam;
import com.dtstack.pubsvc.sdk.dto.result.authcenter.AuthPermissionVO;
import com.dtstack.sdk.core.common.ApiResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/4/13 4:32 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class AuthenticateHandlerInterceptor extends HandlerInterceptorAdapter {

    private static Logger LOGGER = LoggerFactory.getLogger(AuthenticateHandlerInterceptor.class);

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthCenterAPIClient authCenterAPIClient;

    @Autowired
    private EnvironmentContext environmentContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!environmentContext.openAuth()) {
            return true;
        }

        String token = CookieUtil.getDtUicToken(request.getCookies());
        if (StringUtils.isBlank(token)) {
            throw new RdosDefineException(ErrorCode.NOT_LOGIN);
        }
        UserDTO user = sessionUtil.getUser(token, UserDTO.class);
        if (user == null) {
            throw new RdosDefineException(ErrorCode.NOT_LOGIN);
        }

        Authenticate methodAnnotation = null;
        if (handler instanceof HandlerMethod) {
            HandlerMethod h = (HandlerMethod) handler;
            methodAnnotation = h.getMethodAnnotation(Authenticate.class);
        }

        if (methodAnnotation == null) {
            // 找不到Authenticate 直接放行。
            return true;
        }

        if (null != user.getRootUser() && 1 == user.getRootUser()) {
            // admin直接放行
            return true;
        }

        String all = methodAnnotation.all();
        String tenant = methodAnnotation.tenant();

        AuthPermissionParam param = new AuthPermissionParam();
        param.setAppType(AppType.CONSOLE.getType());
        param.setDtuicUserId(user.getDtuicUserId());
        ApiResponse<List<AuthPermissionVO>> permissionVO = authCenterAPIClient.findPermissionListByUserId(param);
        List<AuthPermissionVO> data = permissionVO.getData();

        if (CollectionUtils.isEmpty(data)) {
            throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
        }
        Set<String> permissions = data.stream().map(AuthPermissionVO::getCode).collect(Collectors.toSet());

        if (permissions.contains(all)) {
            return true;
        }

        if (permissions.contains(tenant)) {
            request.setAttribute("isAllAuth", false);
            return true;
        }

        throw new RdosDefineException(ErrorCode.PERMISSION_LIMIT);
    }
}
