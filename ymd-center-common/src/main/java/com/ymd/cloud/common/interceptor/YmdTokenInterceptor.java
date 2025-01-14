package com.ymd.cloud.common.interceptor;

import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.AES;
import com.ymd.cloud.common.utils.DateUtil;
import com.ymd.cloud.common.utils.TokenRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *  YMD 云门道平台 拦截器 <br>
 */
@Slf4j
@Component
public class YmdTokenInterceptor extends TokenInterceptor {
    private static final String SWAGGER_PATH1 = "/swagger-resources";
    private static final String SWAGGER_PATH2 = "/v1.0.0/api";
    @Value("${ymd.token.secret:'ymd56771010'}")
    private String tokenSecret;
    @Value("${ymd.token.effectiveTime:'30000'}")
    private String tokenTime;

    //前置拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String uri = request.getRequestURI();
        log.info("<== YmdTokenInterceptor  preHandle - 权限拦截器.  url={}", uri);
        if (uri.contains(SWAGGER_PATH1) || uri.contains(SWAGGER_PATH2) ) {
            log.info("<== YmdTokenInterceptor  preHandle - 配置URL不走认证.  url={}", uri);
            return true;
        }
        ParameterRequestWrapper requestWrapper = new ParameterRequestWrapper(request);
        Map<String,String> paramsMap = getParameterMap(requestWrapper,response);
        if(!checkToken(paramsMap,response,handler)){//检验token是否有效
            this.handleException(response, ErrorCodeEnum.GL99990018.code(), ErrorCodeEnum.GL99990018.msg());
            return false;
        }
        return true;
    } 
    /**
     * Token 校验；
     * @param paramsMap
     * @return
     */
    private boolean checkToken(Map<String,String> paramsMap, HttpServletResponse response, Object handler) {
        if (isHaveTokenRequired(handler)) {//是否需要进行Token检测
            if(!checkParams(paramsMap,response)){
                return false;
            }
            String token = paramsMap.get("token");
            String decrptToken = AES.aesYmdDecryptParam(tokenSecret,token);
            String time = decrptToken.split("#")[1];
            String time2 = DateUtil.getTimeStamp();
            long n = Long.parseLong(tokenTime) - (Long.parseLong(time2) - Long.parseLong(time));
            if(n>=0){
                return true;
            }
            return false;
        }
        return true;
    }
    protected boolean isHaveTokenRequired(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        TokenRequired responseBody = AnnotationUtils.findAnnotation(method, TokenRequired.class);
        return responseBody != null;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception ex) throws Exception {
        if (ex != null) {
            log.error("<== afterCompletion - 解析token失败. ex={}", ex.getMessage(), ex);
        }
    }
}
