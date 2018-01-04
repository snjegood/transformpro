package com.sinosoft.transformdata.excutor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sinosoft.transformdata.entity.AddressInfo;
import com.sinosoft.transformdata.entity.BaiduApiModel;
import com.sinosoft.transformdata.entity.JsonTemplate;
import com.sinosoft.transformdata.excutor.GetLocation;
import com.sinosoft.transformdata.staticresource.MagicValues;

import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sinosoft.transformdata.staticresource.MagicMethods.*;
import static com.sinosoft.transformdata.staticresource.MagicValues.*;

/**
 * Created by Jay on 2017/12/10.
 *
 * @author Jay
 * @version 1.0
 * @since 1.0
 * Copyright (C) 2017. SinoSoft All Rights Received
 */
public class GetLocationImpl implements GetLocation {
    @Override
    public Map<String, BigDecimal> getPoint(String akCode, String addr, String name) throws IOException {
        if (addr == null) {
            addr = "空值";
        }
        addr = addr.replace("贵州省", "");
        addr = addr.replace("贵州", "");
        addr = addr.replace("毕节市", "");
        addr = addr.replace("毕节", "");
        addr = addr.replace("地区", "");
        String address = "";
        BigDecimal lat = new BigDecimal("0.0");
        BigDecimal lng = new BigDecimal("0.0");
        String count = "0.0";
        Map<String, BigDecimal> map = new HashMap<>(10);
        map.put(LAT, lat);
        map.put(LNG, lng);
        map.put(COUNT, new BigDecimal(count));
        try {
            address = java.net.URLEncoder.encode(addr, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            writeLog(ERROR, e.getMessage());
        }
        String url = String.format(
                "http://api.map.baidu.com/place/v2/suggestion?query=%s&region=%s&city_limit=true&output=json&ak=%s&qq-pf-to=pcqq.c2c", address, REGION, akCode);
        URL myURL = null;
        URLConnection httpsConn;
        //进行转码
        try {
            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            if (myURL == null) {
                writeLog(ERROR, "\"" + name + "\"" + "," + "\"" + addr + "\"" + "url转换失败！");
                return map;
            }
            httpsConn = myURL.openConnection();
            if (httpsConn != null) {
                BaiduApiModel o = JSONObject.parseObject(httpsConn.getInputStream(), null, BaiduApiModel.class);
                if (!STATUS.equals(o.getStatus())) {
                    writeLog(ERROR, "\"" + name + "\"" + "," + "\"" + addr + "\"" + "获取该地址信息失败,错误状态码：" + o.getStatus() + "采用备用方案");
                    return getPointBackUp(name, addr, akCode);
                }
                if (o == null || o.getResult().size() == 0) {
                    writeLog(ERROR, "\"" + name + "\"" + "," + "\"" + addr + "\"" + "获取该地址信息失败,返回值为空！采用备用方案");
                    return getPointBackUp(name, addr, akCode);
                }
                Map<String, Object> map1 = o.getResult().get(0);
                Object location = map1.get("location");
                int size = o.getResult().size();
                int skip = 0;
                while (location == null && (skip < (size - 1))) {
                    skip++;
                    location = o.getResult().get(skip).get("location");
                    writeLog(ERROR, "\"" + name + "\"" + "," + "\"" + addr + "\"" + "获取该地址信息失败,第" + skip +
                            "条信息有错误！");
                }
                if (location == null) {
                    writeLog(ERROR, "\"" + name + "\"" + "," + "\"" + addr + "\"" + "获取该地址信息失败,无符合要求的数据！采用备用方案");
                    return getPointBackUp(name, addr, akCode);
                }
                Map<String, BigDecimal> map2 = JSONObject.toJavaObject((JSON) location, Map.class);
                lat = map2.get("lat");
                lng = map2.get("lng");
                writeLog(INFO, "\"" + name + "\"" + "," + "\"" + addr + "\"" + "纬度：" + lat + "," + "经度：" + lng);
                count = "1.0";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put(LAT, lat);
        map.put(LNG, lng);
        map.put(COUNT, new BigDecimal(count));
        return map;
    }

    private Map<String, BigDecimal> getPointBackUp(String name, String addr, String akCode) throws IOException {
        String lat = "0.0";
        String lng = "0.0";
        String count = "0.0";
        Map<String, BigDecimal> map = new HashMap<>(10);
        map.put(LAT, new BigDecimal(lat));
        map.put(LNG, new BigDecimal(lng));
        String address = "";
        map.put(LAT, new BigDecimal(lat));
        map.put(LNG, new BigDecimal(lng));
        map.put(COUNT, new BigDecimal(count));
        try {
            address = java.net.URLEncoder.encode(addr, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            writeLog(ERROR, e.getMessage());
        }
        String urlBackUp = String.format("http://api.map.baidu.com/geocoder/v2/?address=%s&output=json&ak=%s&callback=showLocation&qq-pf-to=pcqq.c2c", address, akCode);
        URL myURLBackUp = null;
        URLConnection httpsConnBackUp;
        InputStreamReader insr = null;
        try {
            myURLBackUp = new URL(urlBackUp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            httpsConnBackUp = myURLBackUp.openConnection();
            if (myURLBackUp == null) {
                writeLog(ERROR, "\"" + name + "\"" + "," + "\"" + addr + "\"" + "urlBackUp转换失败！");
                return map;
            }

            insr = new InputStreamReader(
                    httpsConnBackUp.getInputStream(), charset);
            BufferedReader br = new BufferedReader(insr);
            String data;
            if ((data = br.readLine()) != null) {
                String status = data.substring(data.indexOf("\"status\":") + ("\"status\":").length(), data.indexOf("\"status\":") + ("\"status\":").length() + 1);
                if (STATUS.equals(status)) {
                    if (data.indexOf("\"lat\":") == -1) {
                        writeLog(ERROR, "\"" + name + "\"" + "," + "\"" + addr + "\"" + "获取该地址信息失败,返回值为空！");
                        return map;
                    }
                    lat = data.substring(data.indexOf("\"lat\":")
                            + ("\"lat\":").length(), data.indexOf("},\"precise\""));
                    lng = data.substring(data.indexOf("\"lng\":")
                            + ("\"lng\":").length(), data.indexOf(",\"lat\""));
                    writeLog(INFO, "\"" + name + "\"" + "," + "\"" + addr + "\"" + "纬度：" + lat + "," + "经度：" + lng);
                    count = "1.0";
                } else {
                    writeLog(ERROR, "\"" + name + "\"" + "," + "\"" + addr + "\"" + "获取该地址信息失败,错误状态码：" + status);
                    return map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (insr != null) {
                insr.close();
            }
        }
        map.put(LAT, new BigDecimal(lat));
        map.put(LNG, new BigDecimal(lng));
        map.put(COUNT, new BigDecimal(count));
        return map;
    }

    @Override
    public void getAllPoint(String akCode, String path) throws IOException {
        File read = new File(path);
        String format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        String jsonPath = String.format("%s-point.json", format);
        BufferedWriter writer;
        try {
            writer = getWriter(POINT_DIR, jsonPath);
        } catch (Exception e) {
            writeLog(ERROR, "创建" + jsonPath +
                    "文件失败！");
            System.out.println("创建" + jsonPath +
                    "文件失败！");
            jsonPath = POINT_FILE;
            writer = getWriter(POINT_DIR, jsonPath);
            e.printStackTrace();
        }
        BufferedWriter writerLog = getWriter(LOG_DIR, LOG_FILE);
        writerLog.write("===================================================================");
        writerLog.flush();
        if (!read.exists()) {
            System.out.println("文件不存在，文件路径为：" + path);
            writeLog(ERROR, "文件不存在，文件路径为：" + path);
        }
        JsonTemplate jsonTemplate = null;
        FileInputStream fileInputStream = new FileInputStream(read);
        try {
            jsonTemplate = JSONObject.parseObject(fileInputStream, JsonTemplate.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!verificationFile(jsonTemplate, path)) {
            return;
        }
        String charset = jsonTemplate.getCharset();
        MagicValues.charset = charset;
        List<AddressInfo> data = jsonTemplate.getData();
        int total = data.size();
        final int[] count = {0};
        final int[] num = {0};
        List<AddressInfo> collect = data.stream().peek(e -> logExecutor.execute(() -> {
            try {
                Map<String, BigDecimal> point = getPoint(AK_CODE, e.getAddress(), e.getName());
                e.setLat(point.get(LAT).doubleValue());
                e.setLng(point.get(LNG).doubleValue());
                BigDecimal bigDecimal = point.get(COUNT);
                count[0] += bigDecimal.intValue();
                num[0]++;
                System.out.println(String.format("总共%d个文件，已处理%d个，还剩%d个", total, num[0], total - num[0]));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        })).collect(Collectors.toList());

        jsonTemplate.setData(collect);
        String s = JSONObject.toJSONString(jsonTemplate);
        writer.write(s);
        writer.flush();

        String msg = "总共：" + total + " 成功：" + count[0] + " 失败：" + (total - count[0]);
        writerLog.write(LINE_SEPARATOR);
        writerLog.write(msg);
        System.out.println("获取坐标结束。" + msg);
        System.out.println(LINE_SEPARATOR);
        writerLog.write(LINE_SEPARATOR);
        File point = new File(POINT_DIR, jsonPath);
        File log = new File(LOG_DIR, LOG_FILE);
        String pointPath = "坐标文件位置为：" + point.getAbsoluteFile();
        String logPath = "日志文件位置为：" + log.getAbsoluteFile();
        System.out.println(pointPath);
        writerLog.write(LINE_SEPARATOR);
        System.out.println(logPath);
        writerLog.write(pointPath);
        writerLog.write(LINE_SEPARATOR);
        writerLog.write("===================================================================");
        writerLog.write(LINE_SEPARATOR);
        writerLog.flush();
        closeWriter();
    }
}