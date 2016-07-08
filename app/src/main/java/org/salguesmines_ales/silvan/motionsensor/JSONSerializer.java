package org.salguesmines_ales.silvan.motionsensor;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Psycus34 on 29/06/2016.
 */
public class JSONSerializer {

    private String mFilename;
    private Context mContexte;

    public JSONSerializer(String fn, Context con){
        mFilename = fn;
        mContexte = con;
    }

    public void save(List<Results> listResults) throws IOException, JSONException{

        JSONArray jArray = new JSONArray();

        for (Results res : listResults)

            jArray.put(res.convertToJSON());
            Writer writer = null;

            try{
                OutputStream out = mContexte.openFileOutput(mFilename, mContexte.MODE_PRIVATE);
                writer = new OutputStreamWriter(out);
                writer.write(jArray.toString());
            }finally {

                if (writer != null){
                    writer.close();
                }
            }
    }

    public ArrayList<Results> load() throws IOException,JSONException{

        ArrayList<Results> listResults = new ArrayList<Results>();
        BufferedReader reader = null;

        try {
            InputStream in = mContexte.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null){
                jsonString.append(line);
            }

            JSONArray jArray = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            for (int i = 0; i < jArray.length(); i++) {
                listResults.add(new Results(jArray.getJSONObject(i)));
            }
        } catch (FileNotFoundException e){

        } finally {
            if (reader != null){
                reader.close();
            }
        }
        return listResults;
    }
}
