package org.skywalking.apm.plugin.rocketmq.consume;

import org.apache.rocketmq.common.message.MessageExt;
import org.skywalking.apm.agent.core.context.CarrierItem;
import org.skywalking.apm.agent.core.context.ContextCarrier;
import org.skywalking.apm.agent.core.context.ContextManager;
import org.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author heyc
 * @date 2017/11/27 16:44
 */
public class RocketMQConsumeInterceptor implements InstanceMethodsAroundInterceptor {

    private static final String ROCKETMQ_COMSUME_OP_PERFIX = "ROCKETMQ_COMSUME/";
    /**
     * getMessageExts
     * @param allArguments
     * @return
     */
    private List<MessageExt> getMessageExts(Object[] allArguments) {
        if (allArguments != null && allArguments.length > 0) {
            return (List<MessageExt>)allArguments[0];
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
        List<MessageExt> messageExts = getMessageExts(allArguments);
        ContextCarrier contextCarrier = new ContextCarrier();
        if (messageExts != null && !messageExts.isEmpty()) {
            CarrierItem next = contextCarrier.items();
            while (next.hasNext()) {
                next = next.next();
                next.setHeadValue(messageExts.get(0).getUserProperty(next.getHeadKey()));
            }
        }
        AbstractSpan span = ContextManager.createEntrySpan(ROCKETMQ_COMSUME_OP_PERFIX + method.getName(), contextCarrier);
        span.setComponent("ROCKETMQ");
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
