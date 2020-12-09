package com.uib.web.peptideshaker.utils;

import io.vertx.core.json.JsonObject;
import java.io.Serializable;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

/**
 * This class responsible for handling http-client calls
 *
 * @author Yehia Mokhtar Farag
 */
public class HttpClientUtils implements Serializable{

    private int CONNECT_TIMEOUT = 30000;
    private int READ_TIMEOUT = 30000;
    private Client client;
    

    public HttpClientUtils() {
    }

    private Client getClient() {
        if (client == null) {
            ClientConfig config = new ClientConfig();
            config.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
            config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
            client = ClientBuilder.newClient(config);
        }
        return client;

    }

    public Response doGet(String uri) {
        return getClient().target(uri)
                .request(MediaType.APPLICATION_JSON).get();

    }
    public Response doPost(String uri,JsonObject body) {
        return getClient().target(uri)
                .request(MediaType.APPLICATION_JSON).post(Entity.json(body.encode()));

    }

}
