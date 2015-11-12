package music.misensayosrs;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.net.URI;
import java.net.URISyntaxException;
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
        Button rte = (Button)findViewById(R.id.button2);
        rte.setEnabled(false);
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
        Spinner lv = (Spinner)findViewById(R.id.spinner);
        ArrayList<String> localidades = new ArrayList<String>();
        localidades.add("Usaquen");
        localidades.add("Chapinero");
        localidades.add("Santa Fe");
        localidades.add("San Cristobal");
        localidades.add("Usme");localidades.add("Tunjuelito");
        localidades.add("Bosa");
        localidades.add("Kennedy");localidades.add("Fontibon");
        localidades.add("Engativa");
        localidades.add("Suba");
        localidades.add("Barrios Unidos");
        localidades.add("Teusaquillo");
        localidades.add("Puente Aranda");
        localidades.add("La Candelaria");
        localidades.add("Rafael Uribe Uribe");
        localidades.add("Ciudad Bolivar");
        localidades.add("Sumapaz");
        ArrayAdapter<String> aa=new ArrayAdapter<String>(this, R.layout.abc_simple_dropdown_hint, localidades);
        lv.setAdapter(aa);
    }

    /**
     *
     * @return
     */
    public AsyncTask<URI, Integer, String> hiloLocalidades(){

        return new AsyncTask<URI, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(URI... urls) {

                StringBuilder builder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(urls[0]);

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
    public void consultaEstablecimientos(View v) throws ExecutionException, InterruptedException, JSONException, URISyntaxException {
        AsyncTask<URI, Integer, String> at = hiloLocalidades();
        Spinner spin = (Spinner)findViewById(R.id.spinner);
        String dr="https://damp-mesa-1375.herokuapp.com/rest/establecimientos/localidad/"+spin.getSelectedItem().toString();
        URI uri = new URI(dr.replace(" ", "%20"));

        at.execute(uri);

        EditText ad = (EditText)findViewById(R.id.editText);
        String dr2=ad.getText().toString().trim();
        AsyncTask<URI, Integer, String> at2=hiloLocalidades();
        String dir2="";
        if(dr2.length()>0){
            dir2 = "https://damp-mesa-1375.herokuapp.com/rest/establecimientos/nombre/"+dr2;
        }else{
            dir2="https://damp-mesa-1375.herokuapp.com/rest/establecimientos/todos";
        }
        uri = new URI(dir2.replace(" ", "%20"));
        at2.execute(uri);

        Spinner lv = (Spinner)findViewById(R.id.spinner2);
        ArrayList<String> productos = new ArrayList<String>();
        ArrayList<String> nits = new ArrayList<String>();

        String st = at.get();
        JSONArray ja = new JSONArray(st);
        for(int i=0; i<ja.length(); i++){
            JSONObject jo=ja.getJSONObject(i);
            String sd =jo.getString("idEstablecimiento")+". "+jo.getString("nombre");
            String sd2 =jo.getString("nit");
            productos.add(sd);
            nits.add(sd2);
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

        ArrayList<String> productos3 = new ArrayList<String>();
        for(int i=0; i<nits.size(); i++){
            if(nits2.contains(nits.get(i))){
                productos3.add(productos.get(i));
            }
        }

        ArrayAdapter<String> aa=new ArrayAdapter<String>(this, R.layout.abc_simple_dropdown_hint, productos3);
        lv.setAdapter(aa);
        Button abi = (Button)findViewById(R.id.button2);
        abi.setEnabled(true);
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
        String ner =spin.getSelectedItem().toString();
        Integer dr=Integer.parseInt(ner.substring(0, ner.indexOf(".")));
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
