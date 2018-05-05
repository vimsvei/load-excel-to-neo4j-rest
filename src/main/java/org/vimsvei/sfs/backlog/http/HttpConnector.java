package org.vimsvei.sfs.backlog.http;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.vimsvei.sfs.backlog.common.Setting;


public class HttpConnector {

    private final Setting setting;
    private Boolean silent;

    public HttpConnector(Boolean silent) {
        this.setting = new Setting();
        this.silent = silent;
    }

    public HttpConnector() {
        this.setting = new Setting();
        this.silent = false;
    }

    private void get() throws Exception {

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(getCredentials(setting))
                .build();

        try {
            HttpGet request = new HttpGet(this.setting.getBaseUri());
            request.setHeader("Accept","application/json; charset=UTF-8");

            System.out.println("Executing request " + request.getRequestLine());

            CloseableHttpResponse response = httpClient.execute(request);
            try {
                if(!this.silent){
                    System.out.println(response.getStatusLine());
                    System.out.println(EntityUtils.toString(response.getEntity()));
                }
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }

    public String post(String url, StringEntity params) throws Exception {

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(getCredentials(setting))
                .build();
        try {
            HttpPost request = new HttpPost(url);
            request.setHeader("Accept","application/json; charset=UTF-8");
            params.setContentType("application/json");
            request.setEntity(params);
            System.out.println("Executing request " + request.getRequestLine());
            CloseableHttpResponse response = httpClient.execute(request);
            try {
                HttpEntity entity = response.getEntity();
                String responseString = "* response.getEntity() is null *";
                if (entity != null) {
                    responseString = EntityUtils.toString(entity, "UTF-8");
                }
                if(!this.silent){
                    System.out.println(response.getStatusLine());
                    System.out.println(responseString);
                }
                return responseString;
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }

    private CredentialsProvider getCredentials(Setting setting) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(setting.host, setting.port),
                new UsernamePasswordCredentials(setting.getUser(), setting.getPassword())
        );
        return credsProvider;
    }
}
