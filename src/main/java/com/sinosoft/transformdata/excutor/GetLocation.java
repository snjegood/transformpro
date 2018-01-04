package com.sinosoft.transformdata.excutor;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Jay on 2017/12/10.
 *
 * @author Jay
 * @version 1.0
 * @since 1.0
 * Copyright (C) 2017. SinoSoft All Rights Received
 */
public interface GetLocation {
    /**
     * 获取指定的点位
     * @param ak_code 百度开发者ak码
     * @param address 地址信息
     * @param name 姓名
     * @return 点位经纬度信息
     */
    Map<String, BigDecimal> getPoint(String ak_code,String address,String name) throws IOException;

    /**
     * 获取所有点位信息
     * @param akCode 开发者ak码
     * @param path 文件路径
     * @throws IOException
     */
    void getAllPoint(String akCode, String path) throws IOException;
}
