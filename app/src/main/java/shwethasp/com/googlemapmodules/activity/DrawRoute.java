package shwethasp.com.googlemapmodules.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import shwethasp.com.googlemapmodules.R;
import shwethasp.com.googlemapmodules.model.MarkerModelClass;
import shwethasp.com.googlemapmodules.util.Util;

public class DrawRoute extends AppCompatActivity {
    protected GoogleMap map;
    protected GoogleApiClient mGoogleApiClient;
    protected LatLng start = new LatLng(12.954647, 77.698599);
    protected LatLng end = new LatLng(12.923176, 77.650505);
    ArrayList<MarkerModelClass> mList;
    String distance, duration;
    Button distance_and_timemap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_route);
       /* distance_and_timemap= (Button) findViewById(R.id.distance_and_timemap);
        distance_and_timemap.setOnClickListener(this);*/

        MapsInitializer.initialize(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.distance_and_timemap);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.distance_and_timemap, mapFragment).commit();

        }

        map = mapFragment.getMap();

        // Array list of MarkerModelClass
        mList = new ArrayList<MarkerModelClass>();
        mList.add(new MarkerModelClass(12.959591, 77.701393, "Kala Mandir Marathalli"));
        mList.add(new MarkerModelClass(12.956494, 77.701240, "Chandra Layout"));
        mList.add(new MarkerModelClass(12.952243, 77.700059, "Innovative Multiplex Bus Stop"));
        mList.add(new MarkerModelClass(12.950045, 77.699662, "Kala Mandir Marathalli"));
        mList.add(new MarkerModelClass(12.942244, 77.697097, "Multiplex Marathahalli"));
        mList.add(new MarkerModelClass(12.939566, 77.695558, "JP Morgen bus stop"));
        mList.add(new MarkerModelClass(12.935246, 77.691134, "City Light Apartment"));
        mList.add(new MarkerModelClass(12.931629, 77.687345, "New Horizon College Stop"));
        mList.add(new MarkerModelClass(12.924472, 77.674145, "Devarabisanahalli, Bellandur"));
        mList.add(new MarkerModelClass(12.921576, 77.667804, "NH7, Bellandur"));
        mList.add(new MarkerModelClass(12.921293, 77.661721, "Sarjapura Cross,"));
        mList.add(new MarkerModelClass(12.919997, 77.651507, "Iblur Bus Stop"));

        //Check the internet connectivity
        if (Util.Operations.isOnline(this)) {
//            routemap();
            drawRouteMap();
        } else {
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }

        //Set the camera position
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(12.935246, 77.691134));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
        map.moveCamera(center);
        map.animateCamera(zoom);


    }



    private void drawRouteMap() {
       /* progressDialog = ProgressDialog.show(this, "Please wait.",
                "Fetching route information.", true);*/
     /*   if (progressHUD == null || !progressHUD.isShowing())
            progressHUD = ProgressHUD.show(DashBoardActivityNew.this, "", true, true, DashBoardActivityNew.this);*/


        for (int i = 0; i < mList.size(); i++) {
            MarkerOptions markerOptions;
            if (i == 0)
                markerOptions = new MarkerOptions().position(start).draggable(false).title(mList.get(i).getTitle()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.start_pin_big));
            else if (i == mList.size() - 1)
                markerOptions = new MarkerOptions().position(end).draggable(false).title(mList.get(i).getTitle()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.stop_pin_big));
            else {
                LatLng latLng = new LatLng(mList.get(i).getLatValue(), mList.get(i).getLongValue());
                markerOptions = new MarkerOptions().position(latLng).draggable(false).title(mList.get(i).getTitle()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.inbetween_stops_pin_big));
            }

            map.addMarker(markerOptions);
        }

         // Setting a custom info window adapter for the google map

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                //Set the text marker title in multiple lines
                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                TextView tvLat = (TextView) v.findViewById(R.id.text);
                tvLat.setText(arg0.getTitle());
                return v;

            }
        });


        int size = mList.size();
        if (size >= 2) {

            int start = 0;
            int end = 9;

            if (size >= 10) {
                float h = mList.size() / (float) 10;
                double a = Math.ceil(h);
                for (int i = 0; i < a; i++) {
                    LatLng origin = new LatLng(mList.get(start).getLatValue(), mList.get(start).getLongValue());
                    LatLng dest = new LatLng(mList.get(end).getLatValue(), mList.get(end).getLongValue());

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest, start, end);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                    size = size - 10;
                    if (size > 0) {
                        if (size >= 10) {
                            start = end;
                            end = end + 9;

                        } else {
                            start = end;
                            end = mList.size() - 1;
                        }
                    }
                }
            } else {
                LatLng origin = new LatLng(mList.get(start).getLatValue(), mList.get(start).getLongValue());
                LatLng dest = new LatLng(mList.get(mList.size() - 1).getLatValue(), mList.get(mList.size() - 1).getLongValue());

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest, start, mList.size());

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

            }


        }


    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);



            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest, int start, int end) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=true";

        // Waypoints
        String waypoints = "";
        for (int i = start; i < end; i++) {
            LatLng point = new LatLng(mList.get(i).getLatValue(), mList.get(i).getLongValue());
            if (i == start + 1)
                waypoints = "waypoints=";
            if (dest.latitude != point.latitude)
                waypoints += point.latitude + "," + point.longitude + "|";
        }


        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&mode=driving" + "&" + waypoints + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.e("URL", url);


        return url;
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            // progressDialog.dismiss();
           /* if (progressHUD.isShowing()) {
                progressHUD.dismiss();
            }*/


            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }



                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(6);
                // polyOptions.color(getResources().getColor(R.color.marker_line));
                lineOptions.color(getResources().getColor(R.color.marker_line));
                // lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            // Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


}

