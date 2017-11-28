package org.skywalking.apm.plugin.rocketmq.config;

import org.apache.rocketmq.remoting.common.RemotingUtil;

/**
 * @author heyc
 * @date 2017/11/28 14:03
 */
public class RocketMQClientConfig {

    private static String namesrvAddr = RemotingUtil.getLocalAddress();

    public static String getNamesrvAddr() {
        return namesrvAddr;
    }

    public static void setNamesrvAddr(String namesrvAddr) {
        RocketMQClientConfig.namesrvAddr = namesrvAddr;
    }
}
