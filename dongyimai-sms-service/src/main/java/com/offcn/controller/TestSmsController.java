package com.offcn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TestSmsController {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination smsDestination;

    @RequestMapping("/sendCode")
    public String sendCode(String mobile,String code){
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage map = session.createMapMessage();
                map.setString("mobile",mobile);
                map.setString("code",code);
                return map;
            }
        });

        return "code: 200 OK";
    }

}
