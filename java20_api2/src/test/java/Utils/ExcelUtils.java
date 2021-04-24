package Utils;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.apache.poi.ss.usermodel.*;
import pojo.WriteBackData;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {


    /**
     * 读取excel数据并封装到指定对象中
     *
     * @param sheetIndex 开始sheet索引
     * @param sheetNum   sheet个数
     * @param clazz      excel映射类
     * @return
     * @throws Exception
     */
    public static List read(int sheetIndex, int sheetNum, Class clazz) throws Exception {
        //easypoi 、
        /*
        1、excel文件流
        2、easypoi导入参数
        3、导入
        */
        //文件流
        FileInputStream fileInputStream = new FileInputStream(Constants.EXCEL_PATH);
        //导入参数
        ImportParams importParams = new ImportParams();
        //从第0个开始读
        importParams.setStartSheetIndex(sheetIndex);
        //每次读一个
        importParams.setSheetNum(sheetNum);
        //(excel文件流、映射关系、导入参数)
        //ExcelImportUtil.importExcel(fileInputStream, clazz, importParams);返回的是集合
        List<Object> objects = ExcelImportUtil.importExcel(fileInputStream, clazz, importParams);
        fileInputStream.close();
        return objects;
    }

    //批量回写存储list集合
    public static List<WriteBackData> writeBackDataList = new ArrayList<>();


    public static void batchWrite() throws Exception {
        //回写的逻辑遍历writeBackDataList集合，取出sheetIndex rowNum cellNum content
        FileInputStream fileInputStream = new FileInputStream(Constants.EXCEL_PATH);
        //获取所有sheet
        Workbook sheets = WorkbookFactory.create(fileInputStream);
        //循环
        for (WriteBackData writeBackData : writeBackDataList) {
            int sheetIndex = writeBackData.getSheetIndex(), rowNum = writeBackData.getRowNum(), cellNum = writeBackData.getCellNum();
            String content = writeBackData.getContent();
            //获取对应的sheet对象
            Sheet sheetAt = sheets.getSheetAt(sheetIndex);
            //获取对应的Row对象
            Row row = sheetAt.getRow(rowNum);
            //获取对应的cell对象
            Cell cell = row.getCell(cellNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            //回写内容
            cell.setCellValue(content);
        }
        //回写到excel文件荣
        FileOutputStream fileOutputStream = new FileOutputStream(Constants.EXCEL_PATH);
        sheets.write(fileOutputStream);
        fileInputStream.close();
        fileOutputStream.close();
    }
}
