package org.vimsvei.sfs.backlog.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.vimsvei.sfs.backlog.common.Setting;
import org.vimsvei.sfs.backlog.http.HttpConnector;

public class Stream extends BaseClass {

    public String name;

    transient public Integer id;

    transient public String labels;

    transient public String create_relationship;

    transient public String self;

    transient private static final Logger log = LogManager.getLogger(Stream.class.getName());

    public Stream(String name){
        this.name = name;
    }

    public Stream(Row row){
        this.name = row.getCell(0).getStringCellValue();
    }

    public void create() {
        try {
            System.out.println(this.toJSON());
            StringEntity params = new StringEntity(this.toJSON(),"UTF-8");
            String response = new HttpConnector(true).post(new Setting().getBaseUri().concat("node"), params);
            if(!response.isEmpty()){
                addResponse(response);
            }
        } catch (Exception e) {
            System.out.println("Stream.create");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void addLabel(String label){
        try {
            StringEntity params = new StringEntity(new Gson().toJson(label), "UTF-8");
            String createNodeResponse = new HttpConnector(true).post(this.labels, params);
        } catch (Exception e) {
            System.out.println("Stream.addLabel");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void addResponse(String response) {
        JsonElement root = new JsonParser().parse(response);

        this.id = root.getAsJsonObject().get("metadata").getAsJsonObject().get("id").getAsInt();
        this.labels = root.getAsJsonObject().get("labels").getAsString();
        this.create_relationship = root.getAsJsonObject().get("create_relationship").getAsString();
        this.self = root.getAsJsonObject().get("self").getAsString();
    }

}
