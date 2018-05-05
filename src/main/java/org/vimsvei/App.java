package org.vimsvei;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.vimsvei.sfs.backlog.model.Node;
import org.vimsvei.sfs.backlog.model.Relationship;
import org.vimsvei.sfs.backlog.model.Stream;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class App
{
    private final static String FILE_XLSX = "/Users/vimsvei/workspace/load-sfs-backlog-neo4j/src/main/resources/ЕФР.xlsx";

    public static void main( String[] args )
    {
        Map< String, Node> nodes;
        Map< String, Stream> streames;

        try {

            nodes = createNodeHash();
            streames = createStreamHash();
            nodes = createNodeResponseHash(nodes);
            streames = createStreamResponseHash(streames);

            createParentRelationship(nodes);
            createStreamRelationship(nodes, streames);
            createDuplicateRelationship(nodes);

        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
    }

    private static XSSFSheet getSheetWorkbook(String fileName, String sheetName) throws Exception {
        File excelFile = new File(fileName);
        FileInputStream fis = new FileInputStream(excelFile);

        XSSFWorkbook workBook = new XSSFWorkbook(fis);

        return workBook.getSheet(sheetName);
    }

    private static Map<String, Node> createNodeHash() throws Exception {

        HashMap<String, Node> nodes = new HashMap<>();
        System.out.println("Reading requipments");
        XSSFSheet sheet = getSheetWorkbook(FILE_XLSX, "BR");

        for (int i = 2; i < 682; i++){
            Row row = sheet.getRow(i-1);
            Node node = new Node(row);
            Node oldNode = nodes.get(node.code);
            if(oldNode == null){
                nodes.put(node.code, node);
            } else {
                oldNode.streams.add(row.getCell(7).getStringCellValue());
                nodes.remove(oldNode.code);
                nodes.put(oldNode.code, oldNode);
            }
        }
        System.out.println("Ok");

        return nodes;
    }

    private static Map<String, Stream> createStreamHash() throws Exception {
        HashMap<String, Stream> streames = new HashMap<>();
        System.out.println("Reading streams");
        XSSFSheet sheet = getSheetWorkbook(FILE_XLSX, "Stream");
        for (int i = 1; i < 17; i++){
            Row row = sheet.getRow(i-1);
            Stream stream = new Stream(row);
            streames.put(stream.name, stream);
//            System.out.print(".");
        }
        System.out.println("Ok");
        return streames;
    }

    private static Map<String, Node> createNodeResponseHash(Map< String, Node> nodes) {

        HashMap<String, Node> responses = new HashMap<>();
        try {
            System.out.println("Creating nodes for requirements");

            nodes.forEach((key, node) -> {
                node.create();
                node.addLabel("Requirement");
                responses.put(node.code, node);
            });
            System.out.println("Ok");
        } catch (Exception e) {
            System.out.println("createStreamResponseHash");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return responses;
    }

    private static Map< String, Stream> createStreamResponseHash(Map< String, Stream> streames) {

        HashMap<String, Stream> responses = new HashMap<>();

        try {
            System.out.println("Creating nodes for requirements");

            streames.forEach((key,stream) -> {
                stream.create();
                stream.addLabel("Stream");
                responses.put(stream.name, stream);
            });
            System.out.println("Ok");

        } catch (Exception e) {
            System.out.println("createStreamResponseHash");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return responses;
    }

    private static void createParentRelationship(Map< String, Node> nodes) {
        try {
            System.out.println("Creating parent relationships");
            nodes.forEach((key, node) -> {
                System.out.println(node.toJSON());
                if (!node.parent.equals("*")) {
                    Node parent = nodes.get(node.parent);
                    System.out.println(parent.toFullJSON());
                    Relationship relationship = new Relationship(parent.self, "Parent");
                    relationship.create(node.create_relationship);
                }
            });
            System.out.println("Ok");
        } catch (Exception e) {
            System.out.println("createParentRelationship");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createStreamRelationship(Map< String, Node> nodes,
                                                 Map< String, Stream> streames) {
        try{
            System.out.println("Creating streams relationships");

            nodes.forEach((key, node) -> {
                System.out.println("streams relationships for ");
                System.out.println("node" + node.toJSON());

                node.streams.forEach(stream -> {
                    if(!stream.equals("")){
                        Relationship relationship = new Relationship(streames.get(stream).self, "Include");
                        relationship.create(node.create_relationship);
                    }
                });
            });
            System.out.println("Ok");
        } catch (Exception e) {
            System.out.println("createStreamRelationship");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createDuplicateRelationship(Map< String, Node> nodes) {
        try {
            System.out.println("Creating duplicate relationships");

            nodes.forEach((key, node) -> {
                if(!node.duplicate.equals("")){
                    System.out.println("duplicate relationships for " + node.toJSON());

                    Relationship relationship = new Relationship(nodes.get(node.duplicate).self, "Duplicate");
                    relationship.create(node.create_relationship);
                }
            });
            System.out.println("Ok");
        } catch (Exception e) {
            System.out.println("createDuplicateRelationship");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
