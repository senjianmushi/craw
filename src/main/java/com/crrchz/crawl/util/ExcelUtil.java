package com.crrchz.crawl.util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * @author lhj
 * @Description:
 * @Date: 2019/1/30 8:57
 */
public class ExcelUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class.getName());

    public static void main(String[] args) {
        String path = "d://webmagic//webmagic.xlsx";
        String name = "test";
        List<String> titles =Lists.newArrayList();
        titles.add("name");
        titles.add("time");
        titles.add("content");
        List<Map<String, Object>> values = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("name", "test_" + i);
            map.put("time", new Date());
            map.put("content", "man");
            values.add(map);
        }
        System.out.println(writerExcel(path, name, titles, values));
    }

    /**
     * 数据写入Excel文件{追加数据，创建数据}
     *
     * @param path 文件路径，包含文件全名，例如：D://file//demo.xls
     * @param name sheet名称
     * @param titles 行标题列
     * @param values 数据集合，key为标题，value为数据
     * @return True\False
     */
    public static boolean writerExcel(String path, String name, List<String> titles, List<Map<String, Object>> values) {
        LOGGER.info("path : {}", path);
        String style = path.substring(path.lastIndexOf("."), path.length()).toUpperCase(); // 从文件路径中获取文件的类型
        File file = new File(path);
        if(file.exists()){
            return appendWorkbook(path, name, style, titles, values);
        }
        return generateWorkbook(path, name, style, titles, values);
    }

    /**
     * 将数据写入指定path下的Excel文件中
     *
     * @param path   文件存储路径
     * @param name   sheet名
     * @param style  Excel类型
     * @param titles 标题串
     * @param values 内容集
     * @return True\False
     */
    private static boolean generateWorkbook(String path, String name, String style, List<String> titles, List<Map<String, Object>> values) {
        LOGGER.info("file style : {}", style);
        Workbook workbook;
        if ("XLS".equals(style.toUpperCase())) {
            workbook = new HSSFWorkbook();
        } else {
            workbook = new XSSFWorkbook();
        }
        // 生成一个表格
        Sheet sheet;
        if (null == name || "".equals(name)) {
            sheet = workbook.createSheet(); // name 为空则使用默认值
        } else {
            sheet = workbook.createSheet(name);
        }
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 创建标题行
        Row row = sheet.createRow(0);
        // 存储标题在Excel文件中的序号
        Map<String, Integer> titleOrder = Maps.newHashMap();
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            String title = titles.get(i);
            cell.setCellValue(title);
            titleOrder.put(title, i);
        }
        // 写入正文
        Iterator<Map<String, Object>> iterator = values.iterator();
        int index = 0; // 行号
        writeExcel(iterator,index ,sheet ,titleOrder);
        // 写入到文件中
        boolean isCorrect = false;
        try {
            File file = new File(path);
            OutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            isCorrect = true;
        } catch (IOException e) {
            isCorrect = false;
            LOGGER.error("write Excel file error : {}", e.getMessage());
        }
        try {
            workbook.close();
        } catch (IOException e) {
            isCorrect = false;
            LOGGER.error("workbook closed error : {}", e.getMessage());
        }
        return isCorrect;
    }

    /**
     * 追加数据到指定path下的Excel文件中
     *
     * @param path   文件存储路径
     * @param name   sheet名
     * @param style  Excel类型
     * @param titles 标题串
     * @param values 内容集
     * @return True\False
     */
    private static boolean appendWorkbook(String path, String name, String style, List<String> titles, List<Map<String, Object>> values) {
        LOGGER.info("file style : {}", style);
        try {
            Workbook workbook;
            File excel=new File(path);
            FileInputStream fs;
            fs=new FileInputStream(excel);
            Sheet sheet;
            if ("XLS".equals(style.toUpperCase())) {
                fs = new FileInputStream(path);
                POIFSFileSystem ps=new POIFSFileSystem(fs);  //使用POI提供的方法得到excel的信息
                workbook = new HSSFWorkbook(ps);
            } else {
                workbook = new XSSFWorkbook(fs);
            }
            FileOutputStream out=new FileOutputStream(excel);
            sheet = workbook.getSheetAt(0);  //根据name获取sheet表
            // 存储标题在Excel文件中的序号
            Map<String, Integer> titleOrder = Maps.newHashMap();
            for (int i = 0; i < titles.size(); i++) {
                String title = titles.get(i);
                titleOrder.put(title, i);
            }
            //写入正文
            Iterator<Map<String, Object>> iterator = values.iterator();
            int index = sheet.getLastRowNum(); // 行号
            Row row = null;
            writeExcel(iterator,index ,sheet ,titleOrder);
            // 写入到文件中
            boolean isCorrect = false;
            workbook.write(out);
            out.close();
            isCorrect = true;
            workbook.close();
            return isCorrect;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 写入正文
     * @param iterator
     * @param index
     * @param sheet
     * @param titleOrder
     */
    public static void writeExcel(Iterator<Map<String, Object>> iterator, int index,Sheet sheet,Map<String, Integer> titleOrder){
        Row row = null;
        while (iterator.hasNext()) {
            index++;    //从接下来的一行写起
            row = sheet.createRow(index);
            Map<String, Object> value = iterator.next();
            for (Map.Entry<String, Object> map : value.entrySet()) {
                // 获取列名
                String title = map.getKey();
                // 根据列名获取序号
                int i = titleOrder.get(title);
                // 在指定序号处创建cell
                Cell cell = row.createCell(i);
                // 获取列的值
                Object object = map.getValue();
                // 判断object的类型
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (object instanceof Double) {
                    cell.setCellValue((Double) object);
                } else if (object instanceof Date) {
                    String time = simpleDateFormat.format((Date) object);
                    cell.setCellValue(time);
                } else if (object instanceof Calendar) {
                    Calendar calendar = (Calendar) object;
                    String time = simpleDateFormat.format(calendar.getTime());
                    cell.setCellValue(time);
                } else if (object instanceof Boolean) {
                    cell.setCellValue((Boolean) object);
                } else {
                    cell.setCellValue(object.toString());
                }
            }
        }
    }
}
