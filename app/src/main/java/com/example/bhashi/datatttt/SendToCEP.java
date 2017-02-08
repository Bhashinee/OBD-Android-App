package com.example.bhashi.datatttt;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Bhashi on 1/28/2017.
 */
public class SendToCEP implements Runnable {
    int id = 1;
    // int speed = 20;
    boolean train = true;
    private Thread t2;
    int speedConverted;
    long time;

    public SendToCEP(int speedConverted, long time) {

        this.speedConverted = speedConverted;
        this.time = time;

    }


    public void run() {
        sendPost();
    }

    public void start() {
        t2 = new Thread(this, "thread 2");
        t2.start();
    }

    public String sendPost() {
        try {

            URL url = new URL("http://192.168.1.2:9763/endpoints/receiver"); // here is your URL path

            JSONObject postDataParams = new JSONObject();
            Log.e("params", postDataParams.toString());
            String jsonString = "{\n" +
                    "    \"event\": {\n" +
                    "        \"payloadData\": {\n" +
                    "            \"id\": \"data5\",\n" +
                    "            \"state\": \"" + speedConverted + "\",\n" +
                    "            \"train\": true,\n" +
                    "            \"timeStamp\": \"" + time + "\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            //    String jsonString = "{\"id\" : \""+id+"\",\n" +
            //            "\"state\":\""+speed+"\",\n" + "\"train\":\""+train+"\"}";

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonString);

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            } else {
                return new String("false : " + responseCode);
            }
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}

