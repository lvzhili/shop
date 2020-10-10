package com.dongyimai.cart.service;

import com.dongyimai.group.Cart;

import java.util.List;

public interface CartService {

    /**
     * 购物车合并
     *
     */
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);
    /**
     * 添加购物车商品
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);
    /**
     * 保存redis中商品
     */
    public void saveRedisCartList(List<Cart> cartList,String username);

    /**
     * 从redis中获取商品信息
     * @return
     */
    public List<Cart> findRedisCartList(String username);
}
