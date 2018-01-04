package com.sinosoft.transformdata.entity;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by Jay on 2017/12/10.
 *
 * @author Jay
 * @version 1.0
 * @since 1.0
 * Copyright (C) 2017. SinoSoft All Rights Received
 */
public class JsonTemplate {
    /**
     * json文件的类型
     */
    private String type;
    /**
     * 本文件的版本
     */
    private String version;
    /**
     * data的数据格式
     */
    private List<Map<String, Type>> description;
    /**
     * 数据
     */
    private List<AddressInfo> data;
    /**
     * 批次
     */
    private Integer batch;
    /**
     * 编码
     */
    private String charset;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Map<String, Type>> getDescription() {
        return description;
    }

    public void setDescription(List<Map<String, Type>> description) {
        this.description = description;
    }

    public List<AddressInfo> getData() {
        return data;
    }

    public void setData(List<AddressInfo> data) {
        this.data = data;
    }

    public Integer getBatch() {
        return batch;
    }

    public void setBatch(Integer batch) {
        this.batch = batch;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
