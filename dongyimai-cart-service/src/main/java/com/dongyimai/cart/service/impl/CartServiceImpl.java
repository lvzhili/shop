package com.dongyimai.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dongyimai.cart.service.CartService;
import com.dongyimai.group.Cart;
import com.dongyimai.mapper.TbItemMapper;
import com.dongyimai.pojo.TbItem;
import com.dongyimai.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 判断该购物车是否存在该商家
     * @param cartList
     * @param sellerId
     * @return
     */
    public Cart searchCartBySellerId(List<Cart> cartList, String sellerId){

        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    /**
     * 判断商家是否存在该商品
     * @param cart
     * @param itemId
     * @return
     */
    public TbOrderItem searchOrderItemById(Cart cart,Long itemId){

        List<TbOrderItem> orderItemList = cart.getOrderItemList();
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    public TbOrderItem createOrderItem(TbItem item,Integer num){
        if (num <= 0){
            throw new RuntimeException("非法数量");
        }

        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));

        return orderItem;
    }

    /**
     * 合并购物车
     * @param cartList1 cookie中数据
     * @param cartList2 redis中数据
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList1) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                cartList2 = addGoodsToCartList(cartList2,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList2;
    }

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1查询商品sku
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null){
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态未审核");
        }
        //2查询商家id
        String sellerId = item.getSellerId();
        //3判断该购物车是否存在该商家
        Cart cart = searchCartBySellerId(cartList, sellerId);
        //3.1如果不存在，新增商品
        if (cart == null){
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());

            TbOrderItem orderItem = createOrderItem(item, num);
            List<TbOrderItem> list = new ArrayList<>();
            list.add(orderItem);

            cart.setOrderItemList(list);

            cartList.add(cart);
        }else {
            //3.2如果存在判断该商家是否存在该商品
            //3.2.1
            TbOrderItem orderItem = searchOrderItemById(cart, itemId);
            //=如果不存在，该商品，直接添加
            if (orderItem == null){

                orderItem = createOrderItem(item, num);

                cart.getOrderItemList().add(orderItem);
            }else {
                //如果存在，增数量，增价格
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().longValue()*orderItem.getNum()));
                //如果该商品数量为0.移除该商品
                if (orderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果该商家内没有商品，移除该商家
                if (cart.getOrderItemList().size() == 0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    @Override
    public void saveRedisCartList(List<Cart> cartList,String username) {
        System.out.println("将数据存入redis中");
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    @Override
    public List<Cart> findRedisCartList(String username) {
        System.out.println("从redis中获取数据");
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);

        if (cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

}
