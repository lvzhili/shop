package com.dongyimai.shop.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login/")
@SuppressWarnings("unchecked")
public class loginController {

   @RequestMapping("name")
    public Map name(){
       String name = SecurityContextHolder.getContext().getAuthentication().getName();
       Map map = new HashMap<>();
       map.put("loginName",name);
       return map;
   }
}
