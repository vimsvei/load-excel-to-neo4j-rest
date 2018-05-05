package org.vimsvei.sfs.backlog.model;

import org.apache.http.entity.StringEntity;
import org.vimsvei.sfs.backlog.http.HttpConnector;

public class Relationship extends BaseClass {
    public String to;
    public String type;

    public Relationship(String to, String type){
        this.to = to;
        this.type = type;
    }

    public void create(String url) {
        try {
            System.out.println("Created relationship for " + this.toJSON());
            StringEntity params = new StringEntity(this.toJSON(), "UTF-8");
            String createNodeResponse = new HttpConnector(true).post(url, params);

        } catch (Exception e) {
            System.out.println("Relationship.createRelationship");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
