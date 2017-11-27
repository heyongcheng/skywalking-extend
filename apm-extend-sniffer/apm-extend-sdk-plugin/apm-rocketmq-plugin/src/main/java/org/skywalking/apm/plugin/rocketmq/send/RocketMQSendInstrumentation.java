package org.skywalking.apm.plugin.rocketmq.send;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.skywalking.apm.agent.core.plugin.match.NameMatch;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author heyc
 * @date 2017/11/27 15:11
 */
public class RocketMQSendInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "org.apache.rocketmq.client.producer.DefaultMQProducer";
    private static final String INTERCEPT_CLASS = "org.skywalking.apm.plugin.rocketmq.send.RocketMQSendInterceptor";

    /**
     * Constructor methods intercept point. See {@link ConstructorInterceptPoint}
     *
     * @return collections of {@link ConstructorInterceptPoint}
     */
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return null;
    }

    /**
     * Instance methods intercept point. See {@link InstanceMethodsInterceptPoint}
     *
     * @return collections of {@link InstanceMethodsInterceptPoint}
     */
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                /**
                 * class instance methods matcher.
                 *
                 * @return methods matcher
                 */
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return named("send").or(named("sendOneway"));
                }

                /**
                 * @return represents a class name, the class instance must instanceof InstanceMethodsAroundInterceptor.
                 */
                public String getMethodsInterceptor() {
                    return INTERCEPT_CLASS;
                }

                public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }

    /**
     * Define the {@link ClassMatch} for filtering class.
     *
     * @return {@link ClassMatch}
     */
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }
}
