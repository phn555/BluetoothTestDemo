package com.bluetoothtest1.excel;

import android.os.Environment;

import java.io.File;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Created by Administrator on 2016/9/18.
 */
public class ExcelUtils {

    WritableWorkbook wwb;

    public  ExcelUtils(){}


    /********************
     * //创建保存路径
     * fileName :存储数据的文件名
    * 返回 文件
    * */
    public  File setPath(String fileName){
        String excelPath = getExcelDir() + File.separator +fileName+ ".xls";
        File file = new File(excelPath);
        return  file ;
    }

    /**************** 创建excel表.
     *
     *
     * @param file ：创建的文件
     */
    public void createExcel(File file) {

        WritableSheet ws = null;
        try {
            if (!file.exists()) {
                wwb = Workbook.createWorkbook(file);
                ws = wwb.createSheet("sheet2", 1);
                // 在指定单元格插入数据
                Label lbl1 = new Label(0, 1, "序号");
                Label bll2 = new Label(1, 1, "数据");

                ws.addCell(lbl1);
                ws.addCell(bll2);

                // 从内存中写入文件中
                wwb.write();
                wwb.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToExcel(File excelFile ,String name, String gender) {

        try {
            Workbook oldWwb = Workbook.getWorkbook(excelFile);
            wwb = Workbook.createWorkbook(excelFile,
                    oldWwb);
            WritableSheet ws = wwb.getSheet(0);
            // 当前行数
            int row = ws.getRows();
            Label lbl1 = new Label(0, row, name);
            Label bll2 = new Label(1, row, gender);

            ws.addCell(lbl1);
            ws.addCell(bll2);

            // 从内存中写入文件中,只能刷一次.
            wwb.write();
            wwb.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 获取Excel文件夹
    public String getExcelDir() {
        // SD卡指定文件夹
        String sdcardPath = Environment.getExternalStorageDirectory()
                .toString();
        File dir = new File(sdcardPath + File.separator + "Excel"
                + File.separator + "Person");
        if (dir.exists()) {
            return dir.toString();

        } else {
            dir.mkdirs();
            return dir.toString();
        }
    }
}
