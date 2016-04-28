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
    private URL url;
    private HttpURLConnection httpConnection;
    private int responseOK = 201;

    /* Send GET request to Server. Throws ConectionExeption in case error */    // TODO: CREAR EXCEPCION...
    public String GETRequest(String urlString, String uriString) { //throws ConectionException{
        try {
            url = new URL(urlString + uriString);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            int responseCode = httpConnection.getResponseCode();
            if ( responseCode != responseOK)  {
                // Error
                return String.valueOf(responseCode) + ":" + "Error";
            }
            InputStream inputStream = httpConnection.getInputStream();
            return String.valueOf(responseCode )+ ":" + InputStreamToString(inputStream);

        } catch (Exception e) {
            //throw new ConectionException("Failed to connect");
            //Log.e("Error", "Failed to conect to Server", e);
            return "404:Error"; // TODO: POR AHORA

        } finally {
            httpConnection.disconnect();
        }
    }

    /* Send POST request to Server. Throws ConectionExeption in case error */
    public String POSTRequest(String urlString, String uriString, String data) { //throws ConectionException{
        try {
            url = new URL(urlString + uriString);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Accept-Language", "UTF-8");
            httpConnection.setDoInput(true);

            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");

            httpConnection.setDoOutput(true);
            httpConnection.connect();

            OutputStreamWriter httpWritter = new OutputStreamWriter(httpConnection.getOutputStream());
            httpWritter.write(data);
            httpWritter.flush();
            httpWritter.close();

            int responseCode = httpConnection.getResponseCode();
            if ( responseCode != responseOK)  {
                // Error
                return String.valueOf(responseCode) + ":" + "Error";
            }

            InputStream inputStream = httpConnection.getInputStream();
            return String.valueOf(responseCode )+ ":" + InputStreamToString(inputStream);

        } catch (Exception e) {
            //throw new ConectionException("Failed to connect");
            //Log.e("Error", "Failed to conect to Server", e);
            return "404:Error";

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

            while(read !=null) {
                builder.append(read);
                read = buffer.readLine();
            }
        } catch (IOException e) {
            return null;    // ERROR - LOG
        }
        return builder.toString();
    }
}
