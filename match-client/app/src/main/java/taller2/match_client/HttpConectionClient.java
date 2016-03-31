package taller2.match_client;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/* This class send GET and POST request to Server by an URL */
public class HttpConectionClient {

    /* Attributes */
    URL url;
    HttpURLConnection httpConection;

    /* Send GET request to Server. Throws ConectionExeption in case error */
    public String GETRequest(String urlString, String uriString) { //throws ConectionException{
        try{
            url = new URL(urlString + uriString);
            httpConection = (HttpURLConnection) url.openConnection();
            httpConection.setRequestMethod("GET");
            if (httpConection.getResponseCode() != 200) {
                httpConection.disconnect(); // TODO: Chequear si hace falta desconectarlo.
                // Error
            }
            InputStream inputStream = httpConection.getInputStream();
            return InputStreamToString(inputStream);

        } catch (IOException e) {
            //throw new ConectionException("Failed to connect");
            //Log.e("Error", "Failed to conect to Server", e);
            return null;

        } finally {
            httpConection.disconnect();
        }
    }

    /* Send POST request to Server. Throws ConectionExeption in case error */
    public String POSTRequest(String urlString, String data) { //throws ConectionException{
        try{
            url = new URL(urlString);
            httpConection = (HttpURLConnection) url.openConnection();
            httpConection.setRequestMethod("POST");
            if (httpConection.getResponseCode() != 200) {
                httpConection.disconnect(); // TODO: Chequear si hace falta desconectarlo.
                // Error
                // Log
            }
            httpConection.setDoInput(true);
            httpConection.setDoOutput(true);
            httpConection.connect();

            OutputStreamWriter httpWritter = new OutputStreamWriter(httpConection.getOutputStream());
            httpWritter.write(data);
            httpWritter.flush();

            InputStream inputStream = httpConection.getInputStream();
            return InputStreamToString(inputStream);

        } catch (IOException e) {
            //throw new ConectionException("Failed to connect");
            //Log.e("Error", "Failed to conect to Server", e);
            return null;

        } finally {
            httpConection.disconnect();
        }
    }

    /* Covert the content of InputStream to String */
    private String InputStreamToString(InputStream stream) {
        InputStreamReader reader = new InputStreamReader(stream);
        StringBuilder builder = new StringBuilder();
        BufferedReader buffer = new BufferedReader(reader);
        try {
            String read = buffer.readLine();

            while(read !=null){
                builder.append(read);
                read = buffer.readLine();
            }
        } catch (IOException e) {
            return null;    // ERROR
        }
        return builder.toString();
    }

}