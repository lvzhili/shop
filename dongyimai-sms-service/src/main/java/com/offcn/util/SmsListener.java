package com.offcn.util;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class SmsListener implements MessageListener {

    @Autowired
    private SmsUtil smsUtil;

    @Override
    public void onMessage(Message message) {


        try {

            if(message instanceof MapMessage){
                MapMessage mapMessage = (MapMessage) message;

                System.out.println("发送短信");

                String mobile = mapMessage.getString("mobile");
                String code = mapMessage.getString("code");
                HttpResponse response = smsUtil.sendCode(mobile, code);

                System.out.println("响应状态码 ： " + response.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
