package org.skywalking.apm.plugin.rocketmq.config;

import org.apache.rocketmq.common.message.MessageExt;
import org.skywalking.apm.agent.core.context.CarrierItem;
import org.skywalking.apm.agent.core.context.ContextCarrier;
import org.skywalking.apm.agent.core.context.ContextManager;
import org.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.skywalking.apm.plugin.rocketmq.config.RocketMQClientConfig;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author heyc
 * @date 2017/11/27 16:44
 */
public class RocketMQClientConfigInterceptor implements InstanceMethodsAroundInterceptor {

    /**
     * called before target method invocation.
     *
     * @param objInst
     * @param method
     * @param allArguments
     * @param argumentsTypes
     * @param result         change this result, if you want to truncate the method.  @throws Throwable
     */
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        if (allArguments != null && allArguments.length > 0) {
            RocketMQClientConfig.setNamesrvAddr(allArguments[0].toString());
        }
    }

    /**
     * called after target method invocation. Even method's invocation triggers an exception.
     *
     * @param objInst
     * @param method
     * @param allArguments
     * @param argumentsTypes
     * @param ret            the method's original return value.  @return the method's actual return value.
     * @throws Throwable
     */
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        return ret;
    }

    /**
     * called when occur exception.
     *
     * @param objInst
     * @param method
     * @param allArguments
     * @param argumentsTypes
     * @param t              the exception occur.
     */
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

    }
}
