package com.sinosoft.transformdata.main;

import com.sinosoft.transformdata.excutor.GetLocation;
import com.sinosoft.transformdata.excutor.impl.GetLocationImpl;

import java.io.IOException;
import java.util.Scanner;

import static com.sinosoft.transformdata.staticresource.MagicMethods.logExecutor;
import static com.sinosoft.transformdata.staticresource.MagicValues.AK_CODE;

/**
 * Created by Jay on 2017/12/10.
 *
 * @author Jay
 * @version 1.0
 * @since 1.0
 * Copyright (C) 2017. SinoSoft All Rights Received
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请拖入需要处理的文件");
        String path = scanner.nextLine();
        GetLocation getLocation = new GetLocationImpl();
        try {
            getLocation.getAllPoint(AK_CODE, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("请等待程序结束！");
//        logExecutor.shutdown();
//        while (!logExecutor.isShutdown()) {}
        System.out.println("程序已结束，请确认文件位置，退出请按Enter");
        scanner.nextLine();
        scanner.close();
        System.out.println("程序结束，执行shutdown");
        logExecutor.shutdown();
    }
}