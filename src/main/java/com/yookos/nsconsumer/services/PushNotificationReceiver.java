package com.yookos.nsconsumer.services;

import com.google.android.gcm.server.Result;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.yookos.nsconsumer.models.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by jome on 2016/02/08.
 */
public class PushNotificationReceiver implements ChannelAwareMessageListener {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Gson gson;
    @Autowired
    PushAndroid pushAndroid;
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        NotificationEvent item = gson.fromJson(msg, NotificationEvent.class);

//        log.info(item.toString());
        List<Result> results = pushAndroid.sendAndroidPushNotification(item);
    }
}
