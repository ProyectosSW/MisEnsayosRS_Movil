package music.misensayosrs;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity {

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            consultaLocalidades();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws JSONException
     */
    public void consultaLocalidades() throws ExecutionException, InterruptedException, JSONException {
        AsyncTask<JSONObject, Integer, String> at = new AsyncTask<JSONObject, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(JSONObject... urls) {
                StringBuilder builder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("https://damp-mesa-1375.herokuapp.com/rest/establecimientos/localidad/nombres");
                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();

                }
                return builder.toString();

            }


            @Override
            protected void onProgressUpdate(Integer... progress) {

            }

            @Override
            protected void onPostExecute(String result) {

            }
        };

        JSONObject b =  new JSONObject();
        at.execute(b);
        Spinner lv = (Spinner)findViewById(R.id.spinner);
        ArrayList<String> asd = new ArrayList<String>(Arrays.asList(at.get().split(",")));
        ArrayList<String> productos = new ArrayList<String>();
        for(String a : asd){
            productos.add(a.replace('"', ' ').replace('[', ' ').replace(']', ' ').trim());
        }
        ArrayAdapter<String> aa=new ArrayAdapter<String>(this, R.layout.abc_simple_dropdown_hint, productos);
        lv.setAdapter(aa);
    }

    /**
     *
     * @param direccion
     * @return
     */
    public AsyncTask<String, Integer, String> hiloLocalidades(String direccion){
        final String  direccion2 = direccion;
        return new AsyncTask<String, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... urls) {

                StringBuilder builder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(direccion2+urls[0]);

                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();

                }
                return builder.toString();

            }

            @Override
            protected void onProgressUpdate(Integer... progress) {

            }

            @Override
            protected void onPostExecute(String result) {

            }
        };
    }

    /**
     *
     * @param v
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws JSONException
     */
    public void consultaEstablecimientos(View v) throws ExecutionException, InterruptedException, JSONException {
        String dir1="https://damp-mesa-1375.herokuapp.com/rest/establecimientos/localidad/";
        AsyncTask<String, Integer, String> at = hiloLocalidades(dir1);
        Spinner spin = (Spinner)findViewById(R.id.spinner);
        String dr=spin.getSelectedItem().toString();
        at.execute(dr);

        String dir2="https://damp-mesa-1375.herokuapp.com/rest/establecimientos/nombre/";
        AsyncTask<String, Integer, String> at2 = hiloLocalidades(dir2);
        EditText ad = (EditText)findViewById(R.id.editText);
        String dr2=ad.getText().toString().trim();
        at2.execute(dr2);

        Spinner lv = (Spinner)findViewById(R.id.spinner2);
        ArrayList<String> productos = new ArrayList<String>();
        ArrayList<String> nits = new ArrayList<String>();

        String st = at.get();
        JSONArray ja = new JSONArray(st);
        for(int i=0; i<ja.length(); i++){
            JSONObject jo=ja.getJSONObject(i);
            String sd =jo.getString("idEstablecimiento")+". "+jo.getString("nombre");
            String sd2 =jo.getString("nit");
            if(!nits.contains(sd2)) {
                productos.add(sd);
                nits.add(sd2);
            }
        }

        st = at2.get();
        ja = new JSONArray(st);
        ArrayList<String> productos2 = new ArrayList<String>();
        ArrayList<String> nits2 = new ArrayList<String>();
        for(int i=0; i<ja.length(); i++){
            JSONObject jo=ja.getJSONObject(i);
            String sd =jo.getString("idEstablecimiento")+". "+jo.getString("nombre");
            String sd2 =jo.getString("nit");
            productos2.add(sd);
            nits2.add(sd2);
        }

        for(String bb:nits){
            if(!nits2.contains(bb)){
                int us=nits.indexOf(bb);
                productos.remove(us);
            }
        }

        ArrayAdapter<String> aa=new ArrayAdapter<String>(this, R.layout.abc_simple_dropdown_hint, productos);
        lv.setAdapter(aa);

    }

    public void informacionEstablecimiento(View v) throws ExecutionException, InterruptedException, JSONException {
        AsyncTask<Integer, Integer, String> at = new AsyncTask<Integer, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(Integer... urls) {
                StringBuilder builder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("https://damp-mesa-1375.herokuapp.com/rest/establecimientos/"+urls[0]);
                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();

                }
                return builder.toString();

            }

            @Override
            protected void onProgressUpdate(Integer... progress) {

            }

            @Override
            protected void onPostExecute(String result) {

            }
        };
        Spinner spin = (Spinner)findViewById(R.id.spinner2);
        Integer dr=Integer.parseInt(spin.getSelectedItem().toString().substring(0, 1));
        at.execute(dr);

        ArrayList<String> productos = new ArrayList<>();
        ListView lv = (ListView)findViewById(R.id.listView);
        String st = at.get();
        JSONObject jo = new JSONObject(st);
        String sd ="Nombre:      "+jo.getString("nombre");
        productos.add(sd);
               sd ="Descripcion: "+jo.getString("descripcion");
        productos.add(sd);
               sd ="Direccion:   "+jo.getString("direccion");
        productos.add(sd);
               sd ="Hora inicio: "+jo.getString("horaInicio");
        productos.add(sd);
               sd ="Hora cierre: "+jo.getString("horaCierre");
        productos.add(sd);
               sd ="Telefono:    "+jo.getString("telefono");
        productos.add(sd);
               sd ="Multa de retraso:       "+jo.getString("multa");
        productos.add(sd);

        ArrayAdapter<String> aa=new ArrayAdapter<String>(this, R.layout.abc_simple_dropdown_hint, productos);
        lv.setAdapter(aa);

    }

}
