package org.skywalking.apm.plugin.rocketmq.send;

import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingUtil;
import org.skywalking.apm.agent.core.context.CarrierItem;
import org.skywalking.apm.agent.core.context.ContextCarrier;
import org.skywalking.apm.agent.core.context.ContextManager;
import org.skywalking.apm.agent.core.context.tag.Tags;
import org.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.skywalking.apm.network.trace.component.ComponentsDefine;
import org.skywalking.apm.plugin.rocketmq.config.RocketMQClientConfig;

import java.lang.reflect.Method;

/**
 * @author heyc
 * @date 2017/11/27 15:25
 */
public class RocketMQSendInterceptor implements InstanceMethodsAroundInterceptor {

    private static final String ROCKETMQ_SEND_OP_PERFIX = "ROCKETMQ_SEND/";

    /**
     * getMessageArg
     * @param allArguments
     * @return
     */
    private Message getMessageArg(Object[] allArguments) {
        if (allArguments != null && allArguments.length > 0) {
            for (Object object : allArguments) {
                if (object instanceof Message) {
                    return (Message)object;
                }
            }
        }
        return null;
    }

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
        //String remotePeer = System.getProperty(MixAll.NAMESRV_ADDR_PROPERTY, System.getenv(MixAll.NAMESRV_ADDR_ENV));
        Message message = getMessageArg(allArguments);
        final ContextCarrier contextCarrier = new ContextCarrier();
        AbstractSpan span = ContextManager.createExitSpan(ROCKETMQ_SEND_OP_PERFIX + method.getName(), contextCarrier, RemotingUtil.getLocalAddress());
        if (message != null) {
            CarrierItem next = contextCarrier.items();
            while (next.hasNext()) {
                next = next.next();
                if (next.getHeadValue() != null) {
                    message.putUserProperty(next.getHeadKey(),next.getHeadValue());
                }
            }
        }
        Tags.URL.set(span, RocketMQClientConfig.getNamesrvAddr());
        SpanLayer.asMQ(span);
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
        ContextManager.stopSpan();
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
        AbstractSpan span = ContextManager.activeSpan();
        span.errorOccurred();
        span.log(t);
    }
}
