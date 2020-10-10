package com.dongyimai.pay.service;

import java.util.Map;

public interface AliPayService {
    /**
     * 生成支付宝二维码
     * 订单号和金额
     */
    public Map createNative(String out_trade_no,String total_fee);
    /**
     * 查询支付状态
     */
    public Map queryPayStatus(String out_trade_no);
}
