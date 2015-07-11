package com.mycompany.futuretext;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.content.Context;

public class MessageLog {

    public final static String STORED_MESSAGES = "storedmessages.txt";
    //public static MessageLog messageLog = new MessageLog();

    //writes a to a text file to store archived messages
    public static void toFile(Context context, String fileName, String messagePost) {

        FileOutputStream messageOutput;
        try {
            messageOutput = context.openFileOutput(fileName, Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(messageOutput);
            osw.write("\n");
            osw.write(messagePost);
            osw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //reads archived messages from text file into a string to be posted in an Activity
    public static String readFile (Context context, String fileName) {

        StringBuilder fileContent = new StringBuilder("");
        try {
            FileInputStream fIn = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader buffReader = new BufferedReader(isr);
            String readString = buffReader.readLine();

            while (readString != null) {
                if (readString.contains("To:")) {
                    fileContent.append("\n");
                }
                fileContent.append(readString);
                fileContent.append("\n");
                readString = buffReader.readLine();
            }

            isr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileContent.toString();
    }

}
