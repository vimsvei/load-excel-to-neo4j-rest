package org.vimsvei.sfs.backlog.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.vimsvei.sfs.backlog.model.Node;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelReader {

    private static XSSFWorkbook workBook;

    public ExcelReader(String fileName) {
        try {
            File excelFile = new File(fileName);
            FileInputStream fis = new FileInputStream(excelFile);

            this.workBook = new XSSFWorkbook(fis);;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Node> readToHach(){
        Map<String, Node> nodes = new HashMap< String, Node>();
        System.out.println("Reading...");
        XSSFSheet sheet = getSheetWorkbook("BR");

        for (int i = 2; i < 682; i++){
            Row row = sheet.getRow(i-1);
            Node node = new Node(row);
            Node oldNode = (Node) nodes.get(node.code);
            if(oldNode == null){
                nodes.put(node.code, node);
            } else {
                oldNode.streams.add(row.getCell(7).getStringCellValue());
                nodes.remove(oldNode.code);
                nodes.put(oldNode.code, oldNode);
            }
            System.out.println(node.code);
        }
        System.out.println();

        return nodes;
    }

    private static XSSFSheet getSheetWorkbook(String sheetName) {
        return workBook.getSheet(sheetName);
    }

}
