package com.uib.web.peptideshaker.utils;

import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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
public class HttpClientUtils implements Serializable {

    private final int CONNECT_TIMEOUT = 30000;
    private final int READ_TIMEOUT = 30000;
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

    public Response doPut(String uri, JsonObject body) {
        return getClient().target(uri)
                .request(MediaType.APPLICATION_JSON).put(Entity.json(body.encode()));

    }

    public Response doPost(String uri, JsonObject body) {
        return getClient().target(uri)
                .request(MediaType.APPLICATION_JSON).post(Entity.json(body.encode()));

    }

//    public File downloadFile(String uri){}
    public File downloadFileFromZipFolder(String uri, int entryIndex, File file) {
        FileOutputStream fos = null;
        try {

            URL downloadableFile = new URL(uri);
            URLConnection conn = downloadableFile.openConnection();
            conn.addRequestProperty("Accept", "*/*");
            conn.setDoInput(true);
            ZipInputStream Zis = new ZipInputStream(conn.getInputStream());
            int counter = 0;
            ZipEntry entry = Zis.getNextEntry();
            while (entry != null && counter < 10) {
                if (entryIndex == counter) {
                    try (ReadableByteChannel rbc = Channels.newChannel(Zis)) {
                        fos = new FileOutputStream(file);
                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                        fos.close();
                        rbc.close();
                        Zis.close();
                        break;
                    }
                }

                entry = Zis.getNextEntry();
                counter++;
            }
        } catch (MalformedURLException ex) {
            System.err.println("Error : HttpClientUti - " + ex);
        } catch (IOException ex) {
            System.err.println("Error : HttpClientUti - " + ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                }
            }

        }
        return file;

    }

}
