package ccthomas.com.spamrejecter.file;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import ccthomas.com.spamrejecter.object.IncomingCallReceiver;
import ccthomas.com.spamrejecter.object.Subset;

/**
 * Created by CCThomasMac on 3/7/18.
 */

public class FileManagement {

    /**
     * Load the Phone Directory (List of Subsets) from the Save File
     * @param context
     * @param fileName
     */
    public static void setPhoneDirectory(Context context, String fileName) {
        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String receiveString = "";
                String[] lineSplit;
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    lineSplit = receiveString.split(",");
                    if (lineSplit.length == 3) {
                        Subset subset = new Subset(lineSplit[0], Integer.parseInt(lineSplit[1]), Boolean.parseBoolean(lineSplit[2]));
                        IncomingCallReceiver.addSubset(subset);
                    }
                }

                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    /**
     * Save Subsets to the Phone Directory (List of Subsets)
     * @param context
     * @param fileName
     */
    public static void savePhoneDirectory(Context context, String fileName) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            ArrayList<Subset> subsets = IncomingCallReceiver.getSubsets();
            for (Subset subset: subsets) {
                outputStreamWriter.write(subset.getCSVFormat());
            }
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
