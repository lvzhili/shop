package com.dongyimai.cart.contoller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dongyimai.cart.service.CartService;
import com.dongyimai.entity.Result;
import com.dongyimai.group.Cart;
import com.dongyimai.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequestMapping("/cart")
@RestController
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private HttpServletRequest request;

    /**
     *
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num){

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录名为：" + name);

        try {
            //查询购物车ﳵ
            List<Cart> cartList = findCartList();

            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            if (name.equals("anonymousUser")){
                //未登录，添加到cookie中
                //存入cookie
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"UTF-8");
            }else {
                //已登录，添加到redis中
                cartService.saveRedisCartList(cartList,name);
            }


            return new Result(true,"购物车添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"购物车添加失败");
        }

    }

    /**
     *查询购物车（cookie、redis）
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //用户名为空，未登录，存入cookie
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");

        if (cartListString == null || cartListString.length() == 0){
            cartListString="[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString,Cart.class);

        if (username.equals("anonymousUser")){

            return cartList_cookie;
        }else{

            //已登录，从redis中获取
            List<Cart> redisCartList = cartService.findRedisCartList(username);

            if (cartList_cookie.size() > 0){
                //合并购物车
                redisCartList = cartService.mergeCartList(cartList_cookie,redisCartList);
                //清除cookie
                CookieUtil.deleteCookie(request,response,"cartList");
                //合并后的数据存入redis
                cartService.saveRedisCartList(redisCartList,username);
            }

            return redisCartList;
        }

    }
}
