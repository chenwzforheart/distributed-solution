package com.cwzsmile.distributed.gateway;

import com.google.common.eventbus.Subscribe;

/**
 * @author csh9016
 * @date 2021/1/28
 */
public class OrderEventListener {

    /**
     * 如果发送了OrderMessage消息，会进入到该函数的处理
     *
     * @param event 消息
     */
    @Subscribe
    public void dealWithEvent(OrderMessage event) {
        System.out.println(Thread.currentThread().getName() + "::我收到了您的命令，命令内容为：" + event.getOrderContent());
    }
}
