package taller2.match_client;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/*  */
public class FileManager {
    /* Atributes */
    Context androidContext;

    FileManager(Context context) {
        androidContext = context;
    }

    /*  */
    public String readFile(String fileName) throws IOException {
        String result = "";
        FileInputStream file = androidContext.openFileInput(fileName);
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while ((line = bufferReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        result = stringBuilder.toString();
        file.close();
        return result;
    }

    /* */
    public void writeFile(String fileName, String content) throws IOException {
            FileOutputStream outputStream = androidContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
    }
}
