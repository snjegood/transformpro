package com.sinosoft.transformdata.staticresource;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sinosoft.transformdata.entity.AddressInfo;
import com.sinosoft.transformdata.entity.JsonTemplate;

import java.io.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.sinosoft.transformdata.staticresource.MagicValues.*;

/**
 * Created by Jay on 2017/12/10.
 *
 * @author Jay
 * @version 1.0
 * @since 1.0
 * Copyright (C) 2017. SinoSoft All Rights Received
 */
public class MagicMethods {
    public static ExecutorService getPool() {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("transform-pool-%d").build();
        return new ThreadPoolExecutor(3, 5, 5L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), factory, new ThreadPoolExecutor.AbortPolicy());
    }

    public static ExecutorService logExecutor;

    static {
        logExecutor = getPool();
    }

    public static void writeLog(String level, String msg) throws IOException {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(date);

        logExecutor.execute(() -> {
            if (ERROR.equals(level)) {
                try {
                    BufferedWriter writer = getWriter(LOG_DIR, LOG_FILE);
                    writer.write(LINE_SEPARATOR);
                    writer.write(format + " ");
                    writer.write("[" + level + "]" + " ");
                    writer.write("[" + Thread.currentThread().getName() + "]" + " ");
                    writer.write(msg);
                    writer.write(LINE_SEPARATOR);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (logExecutor.isShutdown()) {
                        try {
                            closeWriter();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (INFO.equals(level)) {
                System.out.println(format + " " + "[" + level + "]" + " " + "[" + Thread.currentThread().getName() + "]" + " " + "[" + msg + "]");
            }
        });
    }

    private static ThreadLocal<BufferedWriter> thisFileWriter = new ThreadLocal<>();

    public static BufferedWriter getWriter(String fileDir, String file) throws IOException {
        File dir = new File(fileDir);
        boolean mkdir = true;
        if (!dir.exists()) {
            mkdir = dir.mkdir();
        }
        File write;
        BufferedWriter fileWriter = null;
        if (mkdir) {
            write = new File(dir, file);
            boolean exists1 = write.exists();
            boolean newFile = true;
            if (!exists1) {
                newFile = write.createNewFile();
            }
            if (newFile) {
                fileWriter = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (write,true), charset));
//                fileWriter = new FileWriter(write, true);
            } else {
                System.out.println("创建" + write.getAbsolutePath() +
                        "文件失败，请重新尝试！");
            }
        } else {
            System.out.println("创建" + dir.getAbsolutePath() +
                    "文件夹失败，请重新尝试！");
        }
        thisFileWriter.set(fileWriter);
        return fileWriter;
    }

    public static void closeWriter() throws IOException {
        thisFileWriter.get().close();
        thisFileWriter.remove();
    }

    public static boolean verificationFile(JsonTemplate jsonTemplate, String path) throws IOException {
        if (jsonTemplate == null) {
            System.out.println("读取文件失败,文件格式错误,文件路径：" + path);
            writeLog(ERROR, "读取文件失败,文件格式错误，文件路径：" + path);
            return false;
        }
        String version = jsonTemplate.getVersion();
        //type目前没有特殊的要求，只是作为生产文件，待转存数据库的时候区别作用
        String type = jsonTemplate.getType();
        if (!VERSION.equals(version)) {
            System.out.println("文件版本不对,文件路径：" + path);
            writeLog(ERROR, "文件版本不对,文件路径：" + path);
            return false;
        }
        //暂时不使用
        List<Map<String, Type>> description = jsonTemplate.getDescription();
        List<AddressInfo> data = jsonTemplate.getData();
        if (data == null || data.size() == 0) {
            System.out.println("文件中地址信息为空，文件路径：" + path);
            writeLog(ERROR, "文件中地址信息为空,文件路径：" + path);
            return false;
        }
        return true;
    }
}
