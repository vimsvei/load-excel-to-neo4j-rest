package org.vimsvei.sfs.backlog.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.entity.StringEntity;
import org.apache.poi.ss.usermodel.Row;
import org.vimsvei.sfs.backlog.common.Setting;
import org.vimsvei.sfs.backlog.http.HttpConnector;

import java.util.ArrayList;

public class Node extends BaseClass {

    public String code;

    public String description;

    public String comment;

    public String parent;

    public String duplicate;

    public ArrayList<String> streams;

    transient public Integer id;

    transient public String labels;

    transient public String create_relationship;

    transient public String self;

    public Node(Row row) {

        this.code = row.getCell(1).getStringCellValue();

        this.description = row.getCell(6).getStringCellValue();

        this.comment = row.getCell(9).getStringCellValue();

        ArrayList<String> list = new ArrayList<>();
        list.add(row.getCell(7).getStringCellValue());
        this.streams = list;

        this.parent = addParent(row.getCell(1).getStringCellValue()+".");

        this.duplicate = row.getCell(8).getStringCellValue();

    }

    public void addStream(String stream){
        this.streams.add(stream);
    }

    public void create() {
        try {
            System.out.println(this.toJSON());
            StringEntity params = new StringEntity(this.toJSON(), "UTF-8");
            String response = new HttpConnector(true).post(new Setting().getBaseUri().concat("node"), params);
            if(!response.isEmpty()){
                addResponse(response);
            }
        } catch (Exception e) {
            System.out.println("Node.create");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void addLabel(String label){
        try {
            System.out.println("add label \'" + label + " \' to " + this.code);
            StringEntity params = new StringEntity(new Gson().toJson(label), "UTF-8");
            String response = new HttpConnector(true).post(this.labels, params);
        } catch (Exception e) {
            System.out.println("Node.addLabel");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private String addParent(String code){
        String parent = "*";
        String[] array = code.replace(".", ";").split( ";");
        switch (array.length) {
            case 2:{
                break;
            }

            case 3: {
                parent = array[0] + "." + array[1];
                break;
            }

            case 4: {
                parent = array[0] + "." + array[1] + "." + array[2];
                break;
            }

            case 5: {
                parent = array[0] + "." + array[1] + "." + array[2] + "." + array[3];
                break;
            }
        }

        return parent;
    }

    private void addResponse(String response) {
        JsonElement root = new JsonParser().parse(response);

        this.id = root.getAsJsonObject().get("metadata").getAsJsonObject().get("id").getAsInt();
        this.labels = root.getAsJsonObject().get("labels").getAsString();
        this.create_relationship = root.getAsJsonObject().get("create_relationship").getAsString();
        this.self = root.getAsJsonObject().get("self").getAsString();
    }
}
