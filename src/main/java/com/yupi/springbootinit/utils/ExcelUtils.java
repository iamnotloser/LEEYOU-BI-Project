package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
public class ExcelUtils {
    public static String excelToCsv(MultipartFile multipartFile) {
//        File file = null;
//        try {
//            file = ResourceUtils.getFile("classpath:网站数据.xlsx");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }

        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("excelToCsv error", e);

        }
        if(CollUtil.isEmpty(list)){
            return "";
        }
        //转换为csv格式
        StringBuilder csvContent = new StringBuilder();
        //读取表头
        LinkedHashMap<Integer, String> headMap =(LinkedHashMap) list.get(0);
        List<String> headList = headMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        csvContent.append(StringUtils.join(headList, ",")).append("\n");
        System.out.println(StringUtils.join(headList, ","));
        //读取数据
        for(int i = 1;i<list.size();i++){
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap)list.get(i);
            List<String> datalist = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            csvContent.append(StringUtils.join(datalist, ",")).append("\n");
            System.out.println(StringUtils.join(datalist, ","));
        }


        return csvContent.toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(excelToCsv(null));
    }
}
