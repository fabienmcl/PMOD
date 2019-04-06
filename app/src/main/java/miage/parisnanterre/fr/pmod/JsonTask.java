package miage.parisnanterre.fr.pmod;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public class JsonTask extends AsyncTask<String, Void, JSONObject> {

    private Film f;
    private CinemaAdapter adapter;

    public JsonTask(Film f, CinemaAdapter adapter) {

        this.f = f;
        this.adapter = adapter;

    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        InputStream is = null;
        try {
            is = new URL("http://www.omdbapi.com/?i=tt3896198&apikey=20de11f6").openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }

            String jsonText = sb.toString();
            JSONObject json = null;
            try {
                json = new JSONObject(jsonText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);

        //f.setImage(new SerialBitmap(result));

        adapter.notifyDataSetChanged();
    }

}
