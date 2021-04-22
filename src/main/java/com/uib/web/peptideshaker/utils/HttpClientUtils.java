package com.uib.web.peptideshaker.utils;

import io.vertx.core.json.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import static junit.framework.Assert.assertEquals;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

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
            client = ClientBuilder.newClient(config).register(MultiPartFeature.class);
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

    public Response doDelete(String uri) {
        return getClient().target(uri)
                .request(MediaType.APPLICATION_JSON).delete();

    }

    public Response doPost(String uri, JsonObject body) {
        return getClient().target(uri)
                .request(MediaType.APPLICATION_JSON).post(Entity.json(body.encode()));

    }

    public Response doPost(String uri, String body) {
        return getClient().target(uri)
                .request(MediaType.APPLICATION_JSON).post(Entity.text(body));

    }

    public Response doUploadPost(String uri, FormDataMultiPart multipart) {

        return getClient().target(uri).request().post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));
    }

    public void downloadMultipleFilesFromZipFolder(String uri, TreeMap<Integer, File> entryToFilesMap) {
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
                if (entryToFilesMap.containsKey(counter)) {
                    try (ReadableByteChannel rbc = Channels.newChannel(Zis)) {
                        fos = new FileOutputStream(entryToFilesMap.get(counter));
                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    }
                    counter++;

                }
                entry = Zis.getNextEntry();

            }
        } catch (MalformedURLException ex) {
            System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
        } catch (IOException ex) {
            System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
                }
            }

        }

    }

    public File downloadFileFromZipFolder(String uri, String entryName, File file) {
        FileOutputStream fos = null;
        try {

            URL downloadableFile = new URL(uri);
            URLConnection conn = downloadableFile.openConnection();
            conn.addRequestProperty("Accept", "*/*");
            conn.setDoInput(true);
            ZipInputStream Zis = new ZipInputStream(conn.getInputStream());
            ZipEntry entry = Zis.getNextEntry();

            while (entry != null) {
                if (entry.getName().contains(entryName)) {
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
            }
        } catch (MalformedURLException ex) {
            System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
        } catch (IOException ex) {
            System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
                }
            }

        }
        return file;

    }

    public File downloadFile(String uri, File file) {
        FileOutputStream fos = null;
        try {

            URL downloadableFile = new URL(uri);
            URLConnection conn = downloadableFile.openConnection();
            conn.addRequestProperty("Accept", "*/*");
            conn.setDoInput(true);
            InputStream in = conn.getInputStream();
            try (ReadableByteChannel rbc = Channels.newChannel(in)) {
                fos = new FileOutputStream(file);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();
                rbc.close();
                in.close();

            } catch (MalformedURLException ex) {
                System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ex) {
                        System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
                    }
                }

            }

        } catch (MalformedURLException ex) {
            System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
        } catch (IOException ex) {
            System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    System.err.println("at Error: " + this.getClass().getName() + " : " + ex);
                }
            }

        }
        return file;

    }

    public boolean testURL(String url) {

        try {
            URL linkToCheck = new URL(url);
            HttpURLConnection urlConn = (HttpURLConnection) linkToCheck.openConnection();
            urlConn.setConnectTimeout(10000);
            urlConn.connect();
            assertEquals(HttpURLConnection.HTTP_OK, urlConn.getResponseCode());
        } catch (IOException e) {
            System.err.println("Error creating HTTP connection");
            return false;
        }
        return true;
    }

    public BufferedReader streamFile(String fileurl) {
        try {
            URL downloadableFile = new URL(fileurl);
            URLConnection urlConn = downloadableFile.openConnection();
            urlConn.setConnectTimeout(30000);
            urlConn.addRequestProperty("Connection", "keep-alive");
            InputStream in = urlConn.getInputStream();
            InputStreamReader r = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(r);
            return br;
        } catch (MalformedURLException ex) {
            Logger.getLogger(HttpClientUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpClientUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BufferedReader byteServingStreamFile(String fileurl, String startIndex, String endEndex) {
        try {

            URL streamableFileUrl = new URL(fileurl);
            URLConnection urlConn = streamableFileUrl.openConnection();
            urlConn.setConnectTimeout(30000);
            urlConn.addRequestProperty("Connection", "keep-alive");
            urlConn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            urlConn.addRequestProperty("Accept-Range", "bytes");
            urlConn.addRequestProperty("Range", "bytes=" + startIndex + "-" + endEndex);//+(startIndex+1000)
            InputStream in = urlConn.getInputStream();
            InputStreamReader r = new InputStreamReader(in, "UTF-8");
            BufferedReader br = new BufferedReader(r);
            return br;
        } catch (MalformedURLException ex) {
            Logger.getLogger(HttpClientUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpClientUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
