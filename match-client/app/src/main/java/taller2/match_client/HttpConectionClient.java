package taller2.match_client;

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
    HttpURLConnection httpConnection;

    /* Send GET request to Server. Throws ConectionExeption in case error */
    public String GETRequest(String urlString, String uriString) { //throws ConectionException{
        try{
            url = new URL(urlString + uriString);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            if (httpConnection.getResponseCode() != 200) {
                httpConnection.disconnect(); // TODO: Chequear si hace falta desconectarlo.
                // Error
            }
            InputStream inputStream = httpConnection.getInputStream();
            return InputStreamToString(inputStream);

        } catch (IOException e) {
            //throw new ConectionException("Failed to connect");
            //Log.e("Error", "Failed to conect to Server", e);
            return null;

        } finally {
            httpConnection.disconnect();
        }
    }

    /* Send POST request to Server. Throws ConectionExeption in case error */
    public String POSTRequest(String urlString, String uriString, String data) { //throws ConectionException{
        try{
            url = new URL(urlString + uriString);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Accept-Language", "UTF-8");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.connect();

            OutputStreamWriter httpWritter = new OutputStreamWriter(httpConnection.getOutputStream());
            httpWritter.write(data);
            httpWritter.flush();

            if (httpConnection.getResponseCode() != 200) {
                httpConnection.disconnect(); // TODO: Chequear si hace falta desconectarlo.
                // Error
                // Log
            }

            InputStream inputStream = httpConnection.getInputStream();
            return InputStreamToString(inputStream);

        } catch (IOException e) {
            //throw new ConectionException("Failed to connect");
            //Log.e("Error", "Failed to conect to Server", e);
            return null;

        } finally {
            httpConnection.disconnect();
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
