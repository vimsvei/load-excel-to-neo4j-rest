package org.vimsvei.sfs.backlog.common;

public class Setting {

    public final String host = "localhost";

    public final Integer port = 7474;

    private final String NEO4J_BASE_URI = "http://localhost:7474/db/data/";

    private final String NEO4J_USER = "neo4j";

    private final String NEO4J_PASSWORD = "o441yk190rus";

    public String getBaseUri(){
        return this.NEO4J_BASE_URI;
    }

    public String getUser(){
        return this.NEO4J_USER;
    }

    public String getPassword(){
        return this.NEO4J_PASSWORD;
    }
}
