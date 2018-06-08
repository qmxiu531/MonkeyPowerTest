package autotest.gionee.automonkeypowertest.util;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import autotest.gionee.automonkeypowertest.bean.PowerInfoResult;
import autotest.gionee.automonkeypowertest.bean.ReportBean;
import autotest.gionee.automonkeypowertest.bean.ReportResultBean;
import autotest.gionee.automonkeypowertest.util.sqlite.DBManager;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * 创建表格，添加字段
 *
 * @author pbl
 */
public class JExcelUtil {
    public static boolean exportExcel(File file) {
        ArrayList<ReportResultBean> excelData = get();
        return createExcel(file, excelData, new SheetBuilder<ReportResultBean>() {
            @Override
            String setSheetName(int sheetIndex) {
                return "第" + String.valueOf(sheetIndex + 1) + "批次";
            }

            @Override
            String[] setHeadline(int sheetIndex) {
                return new String[]{"应用名", "应用版本", "应用运行时耗电(mAh)", "应用运行时平均电流(mA)", "应用运行时整机平均电流(mA)", "应用运行时平均电压(V)", "灭屏应用耗电(mAh)", "灭屏应用平均电流(mA)", "灭屏整机平均电流(mA)", "灭屏平均电压(V)", "覆盖率(%)", "软件版本", "时间"};
            }

            @Override
            int[] setColumnViewsWidth(int sheetIndex) {
                return new int[]{25, 25, 30, 30, 30, 30, 30, 30, 30, 30, 30, 25, 25};
            }

            @Override
            String[][] setRowsData(ReportResultBean bean, int sheetIndex) {
                ArrayList<ReportBean> rows    = bean.reportBeans;
                String[][]            strings = new String[rows.size()][];
                for (int i = 0; i < strings.length; i++) {
                    ReportBean reportBean = rows.get(i);
                    strings[i] = new String[]{reportBean.appName, reportBean.appVersion, reportBean.power_front + "",
                            reportBean.power_front_avg + "", reportBean.wholePower_front + "", (float) reportBean.voltageBean_F.voltageAvg / 1000 + "", reportBean.power_back + "",
                            reportBean.power_back_avg + "", reportBean.wholePower_back + "", (float) reportBean.voltageBean_B.voltageAvg / 1000 + "",
                            reportBean.coverage_front, reportBean.softVersion, reportBean.time};
                }
                return strings;
            }

            @Override
            void other(int row, int column, ReportResultBean sheetData, WritableSheet sheet) throws WriteException {
                ArrayList<PowerInfoResult> highPowerResults = sheetData.highPowerResults;
                int                        newRow           = row + 2;
                sheet.addCell(new Label(0, newRow, "应用名"));
                sheet.addCell(new Label(1, newRow, "电流"));
                sheet.addCell(new Label(2, newRow, "时间"));
                for (int r = 0; r < highPowerResults.size(); r++) {
                    PowerInfoResult info = highPowerResults.get(r);
                    sheet.addCell(new Label(0, newRow + r, info.getName()));
                    sheet.addCell(new Label(1, newRow + r, info.getPower()));
                    sheet.addCell(new Label(2, newRow + r, info.getTime()));
                }
            }
        });
    }

    private static boolean createExcel(File file, ArrayList<ReportResultBean> d, SheetBuilder<ReportResultBean> builder) {
        try {
            if (!file.exists()) {
                boolean isCreated = file.createNewFile();
                if (!isCreated) {
                    Util.i("excel表创建失败");
                    return false;
                }
            }
            WritableWorkbook book = Workbook.createWorkbook(file);
            for (int j = 0; j < d.size(); j++) {
                createSheet(book, d.get(j), j, builder);
            }
            book.write();
            book.close();
        } catch (Exception e) {
            Util.i(e.toString());
            Util.i("excel表写入失败");
            return false;
        }
        return true;
    }

    private static void createSheet(WritableWorkbook book, ReportResultBean data, int index, SheetBuilder<ReportResultBean> builder) throws WriteException, IOException {
        WritableSheet      sheet         = book.createSheet(builder.setSheetName(index), index);
        WritableFont       bold          = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);// 设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
        WritableCellFormat contentFormat = getContentFormat(bold);
        WritableCellFormat titleFormat   = getTitleFormat(bold);

        WritableFont       nf   = new WritableFont(WritableFont.createFont("宋体"), 10);
        WritableCellFormat wcfN = new WritableCellFormat(nf);
        wcfN.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
        wcfN.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
        wcfN.setAlignment(Alignment.CENTRE);

        // 水平对齐
        wcfN.setWrap(true);
        // 生成名为“第一页”的工作表，参数0表示这是第一页
        // 在Label对象的构造子中指名单元格位置是第一列第一行(0,0)
        String[] titles = builder.setHeadline(index);
        int[]    ints   = builder.setColumnViewsWidth(index);
        if (ints.length == 0) {
            ints = new int[titles.length];
            Arrays.fill(ints, 20);
        }
        for (int i = 0; i < titles.length; i++) {
            Label label = new Label(i, 0, titles[i], titleFormat);
            sheet.addCell(label);
            sheet.setColumnView(i, ints[i]);
        }
        String[][] rows = builder.setRowsData(data, index);
        for (int i = 0; i < rows.length; i++) {
            int nextLine = i + 1;
            for (int j = 0; j < rows[i].length; j++) {
                Label appName = new Label(j, nextLine, rows[i][j], contentFormat);
                sheet.addCell(appName);
            }
        }
        int row    = rows.length;
        int column = row > 0 ? rows[0].length : 0;
        if (column != 0 || row != 0) {
            builder.other(rows.length, column, data, sheet);
        }
    }

    @NonNull
    private static WritableCellFormat getContentFormat(WritableFont bold) throws WriteException {
        WritableCellFormat contentFormat = new WritableCellFormat(bold);// 生成一个单元格样式控制对象
        contentFormat.setAlignment(Alignment.CENTRE);// 单元格中的内容水平方向居中
        contentFormat.setVerticalAlignment(VerticalAlignment.CENTRE);// 单元格的内容垂直方向居中
        contentFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        return contentFormat;
    }

    @NonNull
    private static WritableCellFormat getTitleFormat(WritableFont bold) throws WriteException {
        WritableCellFormat titleFormat = new WritableCellFormat(bold);// 生成一个单元格样式控制对象
        titleFormat.setAlignment(Alignment.CENTRE);// 单元格中的内容水平方向居中
        titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);// 单元格的内容垂直方向居中
        titleFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        titleFormat.setBackground(Colour.GREEN);
        return titleFormat;
    }

    public static ArrayList<ReportResultBean> get() {
        ArrayList<ReportResultBean> data   = new ArrayList<>();
        ArrayList<Long>             batchs = DBManager.getBatchs();
        for (long id : batchs) {
            data.add(DBManager.getReportBean(id));
        }
        return data;
    }

    static abstract class SheetBuilder<T> {
        abstract String setSheetName(int sheetIndex);

        abstract String[] setHeadline(int sheetIndex);

        int[] setColumnViewsWidth(int sheetIndex) {
            return new int[0];
        }

        abstract String[][] setRowsData(T sheetData, int sheetIndex);

        abstract void other(int row, int column, T sheetData, WritableSheet sheet) throws WriteException;
    }
}
