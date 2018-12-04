package pl.zsp.safeway;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.zsp.safeway.RoadAlert;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;

public class Infos extends AppCompatActivity {
    GETTING g;
    String url = "http://serwer1727017.home.pl/2ti/safeway/index.php";

    double szer, wys;
    //TextView nr_dg= (TextView)findViewById(R.id.nr_drg);
    TextView nr_dg;
    TextView nazwadr;
    TextView maxprd;
    TextView dzien;
    TextView godz;
    TextView mies;
    TextView count;
    TextView anettiontext;
    TextView addit;
    String nr = "";
    String dd;
    String hh = "";
    String mm = "";
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected boolean gps_enabled, network_enabled;

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Nie mozna wrocic do menu", Toast.LENGTH_LONG);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);
        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        nr_dg = (TextView) findViewById(R.id.nr_drg);
        nazwadr = (TextView) findViewById(R.id.ulica);
        maxprd = (TextView) findViewById(R.id.maxprd);
        dzien = (TextView) findViewById(R.id.dzien);
        mies = (TextView) findViewById(R.id.Miesiac);
        godz = (TextView) findViewById(R.id.Godzina);
        count = (TextView) findViewById(R.id.count);
        addit = (TextView) findViewById(R.id.additional);
        anettiontext = (TextView) findViewById(R.id.anettiontext);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.appbar);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (toggleButton.isChecked()) {
                    g = new GETTING();
                    g.execute("");
                } else {
                    g.cancel(true);
                }
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                wys = location.getLongitude();
                szer = location.getLatitude();
                Log.e("d", String.valueOf(szer));
                Log.e("d", String.valueOf(wys));
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Latitude", "disable");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Latitude", "enable");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Latitude", "status");
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
        }
    }

    private class GETTING extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            for (; ; ) {
                readXML("", "");
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            anettiontext.setText(jsonObject.getString("anettiontext"));
                            count.setText(jsonObject.getString("count"));
                            if(!jsonObject.getString("addit").isEmpty()){
                                addit.setText(jsonObject.getString("addit"));
                            }else{
                                addit.setText("");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("d", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parms = new HashMap<String, String>();
                        SimpleDateFormat godzz = new SimpleDateFormat("HH");
                        SimpleDateFormat miess = new SimpleDateFormat("MM");
                        Calendar calendar = Calendar.getInstance();
                        hh = godzz.format(calendar.getTime());
                        mm = miess.format(calendar.getTime());
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            LocalDate localDate = LocalDate.now();
                            dd= localDate.getDayOfWeek().toString();

                        }else{
                            dd = "--";
                        }

                        parms.put("nr_drogi", nr);
                        parms.put("godzina", hh);
                        parms.put("dzien", String.valueOf(dd));
                        parms.put("miesiac", mm);
                        parms.put("szer",String.valueOf(szer));
                        parms.put("wys",String.valueOf(wys));

                        return parms;
                    }
                };

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                    switch (mm) {
                                        case "01":
                                            mies.setText("Sty.");
                                            break;
                                        case "02":
                                            mies.setText("Lut.");
                                            break;
                                        case "03":
                                            mies.setText("Mar.");
                                            break;
                                        case "04":
                                            mies.setText("Kwi.");
                                            break;
                                        case "05":
                                            mies.setText("Maj");
                                            break;
                                        case "06":
                                            mies.setText("Cze.");
                                            break;
                                        case "07":
                                            mies.setText("Lip.");
                                            break;
                                        case "08":
                                            mies.setText("Sie.");
                                            break;
                                        case "09":
                                            mies.setText("Wrz.");
                                            break;
                                        case "10":
                                            mies.setText("Paź");
                                            break;
                                        case "11":
                                            mies.setText("Lis.");
                                            break;
                                        case "12":
                                            mies.setText("Gru.");
                                            break;


                                    }
                                try {
                                switch (dd) {
                                    case "MONDAY":
                                        dzien.setText("Pon");
                                        break;
                                    case "TUESDAY":
                                        dzien.setText("Wt");
                                        break;
                                    case "WEDNESDAY":
                                        dzien.setText("Śr");
                                        break;
                                    case "THURSDAY":
                                        dzien.setText("Czw");
                                        break;
                                    case "FRIDAY":
                                        dzien.setText("Pt");
                                        break;
                                    case "SATURDAY":
                                        dzien.setText("So");
                                        break;
                                    case "SUNDAY":
                                        dzien.setText("Nd");
                                        break;
                                        default:
                                            dzien.setText("--");
                                }
                                godz.setText(hh);
                                }catch (Exception e){
                                    Log.e("exeption", e.toString());
                                }
                        }
                        });
                    }
                };
                t.start();
                RequestQueue q = new RequestQueue(new DiskBasedCache(getCacheDir(), 1024 * 1024), new BasicNetwork(new HurlStack()));
                q.start();
                q.add(request);
                if (g.isCancelled()) {

                    break;
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            try {
                this.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            //super.onCancelled();
        }


    }

    private void readXML(String lat, String lon) {
        try {

            List<String> b = locationManager.getAllProviders();
            Log.e("read", String.valueOf(wys));
            Log.e("read", String.valueOf(szer));
            String url = "https://www.overpass-api.de/api/interpreter?data=[out:json];way(" + (szer) + "," + (wys) + "," + (szer + 0.0001) + "," + (wys + 0.0001) + ")[%22highway%22];(._;>;);out%20meta;";
            Log.e("d", url);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    JSONObject arr = response;
                    try {
                        JSONArray jObj = new JSONArray(arr.getString("elements"));
                        JSONObject jObjs;
                        JSONObject jObjss = null;
                        for (int i = 0; i < jObj.length(); i++) {
                            jObjs = new JSONObject(jObj.getString(i));

                            if (jObjs.has("tags")) {
                                jObjss = new JSONObject(jObjs.getString("tags"));
                                if (jObjss.has("surface")) {

                                    if (jObjss.has("ref")) {
                                        if (jObjss.getString("ref").toString() != nr) {
                                            RoadAlert roadAlert = new RoadAlert();
                                            roadAlert.notify(getApplicationContext(), "Dane dla " + jObjss.getString("ref").toString(), 0);
                                        }
                                    }
                                    break;
                                } else continue;
                            }
                        }

                        if (jObjss.has("ref")) {
                            nr = jObjss.getString("ref").toString();
                            nr_dg.setText(jObjss.getString("ref").toString());
                        } else {
                            nr_dg.setText("brak");
                        }
                        if (jObjss.has("name")) {
                            nazwadr.setText(jObjss.getString("name").toString());
                        } else {
                            nazwadr.setText("brak");
                        }
                        if (jObjss.has("maxspeed")) {
                            maxprd.setText(jObjss.getString("maxspeed").toString() + "km/h");
                        } else {
                            maxprd.setText("Brak informacji");
                        }
                        ;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            // Now you can use any deserializer to make sense of data
                            JSONObject obj = new JSONObject(res);
                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        } catch (JSONException e2) {
                            // returned data is not JSONObject?
                            e2.printStackTrace();
                        }
                    }
                }
            });

            RequestQueue q = new RequestQueue(new DiskBasedCache(getCacheDir(), 1024 * 1024), new BasicNetwork(new HurlStack()));
            q.add(request);
            q.start();
        } catch (Exception e) {

        }


    }
}
