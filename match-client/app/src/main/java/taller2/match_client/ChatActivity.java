package taller2.match_client;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.send_server_msg);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMensageToServer();
            }
        });
    }

    public void sendMensageToServer() {

        TextView chatWindows = (TextView) findViewById(R.id.chatWindows);

        ConnectivityManager connectManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {         // I check status of conection

            chatWindows.setText("Conexion posible");
            InternetConectionThread conectionThread = new InternetConectionThread();
            conectionThread.execute("http://192.168.0.5:8000");
        } else {
            chatWindows.setText("Conexion imposible");
        }

    }

    private class  InternetConectionThread extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute(){
            //Setup is done here
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                URL url = new URL(params[0]);
                HttpURLConnection httpConection = (HttpURLConnection) url.openConnection();

                if(httpConection.getResponseCode() != 200) {
                    throw new Exception("Failed to connect");
                }
                InputStream inputStream = httpConection.getInputStream();
                return letsDoThisAgain(inputStream);

            }catch(Exception e){
                Log.e("Image", "Failed to conect to Server", e);

            } finally {
               // httpConection.disconnect();
            }
            return "Error";
        }

        @Override
        protected void onProgressUpdate(Integer... params) {
            //Update a progress bar here, or ignore it, it's up to you
        }

        @Override
        protected void onPostExecute(String dataGetFromServer){
            TextView chatWindows = (TextView) findViewById(R.id.chatWindows);
            chatWindows.setText(dataGetFromServer);
        }

        @Override
        protected void onCancelled(){
            // Handle what you want to do if you cancel this task
        }

        public String letsDoThisAgain(InputStream streamFromServer){

            InputStreamReader reader = new InputStreamReader(streamFromServer);
            StringBuilder builder = new StringBuilder();
            BufferedReader buffer = new BufferedReader(reader);
            try {
                String read = buffer.readLine();

                while(read !=null){
                    builder.append(read);
                    read = buffer.readLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return builder.toString();
        }

    }
}