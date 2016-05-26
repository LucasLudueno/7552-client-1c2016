package taller2.match_client;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/* This class read and write content in files */
public class FileManager {

    /* Open a file and return its content */
    public static String readFile(String fileName, Context cntx) throws IOException {
        String result = "";
        FileInputStream file = cntx.openFileInput(fileName);
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

    /* Open a file and write the content */
    public static void writeFile(String fileName, String content, Context cntx) throws IOException {
            FileOutputStream outputStream = cntx.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
    }
}
