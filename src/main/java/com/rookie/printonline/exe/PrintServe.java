package com.rookie.printonline.exe;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *@Title: PrintServe.java
 *@Package: com.rookie.printonline.exe
 *@Description: TODO
 *@Author: mahx 马怀啸
 *@Email: 616107968@qq.com
 *@Date: 2025/5/16 9:30
 *@Version: V1.0.0
 *@Copyright: 菜鸟
 */
public class PrintServe {


    /**
     * 获得所有打印机名称
     * @return
     */
    public static List<String> getAllPrint(){
        // 获取默认打印机
        PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();

        // 获取并排序打印机列表
        List<String> sortedPrinters = getSortedPrinters(defaultPrinter);

        return sortedPrinters;
    }
    public static List<String> getSortedPrinters(PrintService defaultPrinter) {
        return Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
                .sorted(createPrinterComparator(defaultPrinter))
                .map(printer -> formatPrinterName(printer, defaultPrinter))
                .collect(Collectors.toList());
    }

    private static Comparator<PrintService> createPrinterComparator(PrintService defaultPrinter) {
        return Comparator.comparing(printer -> !printer.equals(defaultPrinter));
    }

    private static String formatPrinterName(PrintService printer, PrintService defaultPrinter) {
        return printer.equals(defaultPrinter)
                ? printer.getName() + " (默认)"
                : printer.getName();
    }
}
