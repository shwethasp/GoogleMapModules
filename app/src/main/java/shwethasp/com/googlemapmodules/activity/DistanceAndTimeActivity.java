package shwethasp.com.googlemapmodules.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import shwethasp.com.googlemapmodules.R;

public class DistanceAndTimeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    AutoCompleteTextView atvSourcePlaces, atvDestinationPlaces;
    LinearLayout mDistanceLinear, mTimeLinear;
    TextView mDistance_value, mTime_value, mTime_Text, mDistance_text;

    GoogleMap googleMap;

    final int PLACES = 0;
    final int PLACES_DETAILS = 1;
    ParserTask parserTask;
    PlacesTask placesTask;
    SimpleAdapter adapter;
    protected GoogleApiClient mGoogleApiClient;
    private LatLng start, end;
    private Button mButtonGetDistance;
    String distance;
    String time;
    JSONObject jObject = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_and_time);

        // Getting a reference to the AutoCompleteTextView
        atvSourcePlaces = (AutoCompleteTextView) findViewById(R.id.sourcePlace);
        atvDestinationPlaces = (AutoCompleteTextView) findViewById(R.id.destinationPlace);
        mButtonGetDistance = (Button) findViewById(R.id.getDistance);
        mDistanceLinear = (LinearLayout) findViewById(R.id.distanceLinear);
        mDistance_value = (TextView) findViewById(R.id.distance_value);
        mTime_value = (TextView) findViewById(R.id.time_value);
        mTime_Text = (TextView) findViewById(R.id.time_text);
        mDistance_text = (TextView) findViewById(R.id.distance_text);
        mTime_Text.setVisibility(View.GONE);
        mDistance_text.setVisibility(View.GONE);

        mTimeLinear = (LinearLayout) findViewById(R.id.timeLinear);
        mDistanceLinear.setVisibility(View.GONE);
        mTimeLinear.setVisibility(View.GONE);

         //Initilaize the google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        atvSourcePlaces.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                atvSourcePlaces.showDropDown();
                return false;
            }
        });
        // Adding textchange listener
        atvSourcePlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Creating a DownloadTask to download Google Places matching "s"
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        //TODO:souce text on click listener

        atvSourcePlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final HashMap<String, String> item = (HashMap<String, String>) adapter.getItem(position);
                String placeId = item.get("place_id");
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e("", "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        start = place.getLatLng();

                        Log.e("start longitude ", "" + start.longitude);
                        Log.e("start latitude ", "" + start.latitude);

                    }
                });

            }
        });

        atvDestinationPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final HashMap<String, String> item = (HashMap<String, String>) adapter.getItem(position);
                String placeId = item.get("place_id");
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e("", "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        end = place.getLatLng();

                        Log.e("end longitude ", "" + end.longitude);
                        Log.e("end latitude ", "" + end.latitude);

                    }
                });

            }
        });


        //TODO:Destination autotext

        atvDestinationPlaces.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                atvDestinationPlaces.showDropDown();
                return false;
            }
        });
        // Adding textchange listener
        atvDestinationPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Creating a DownloadTask to download Google Places matching "s"
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        mButtonGetDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(start, end);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        });

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
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=AIzaSyCDGxzFS-bcUBUO-DpeQ0FDNwu5SGIPG4A";

            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

            try {
                // Fetching the data from we service
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
            adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            atvSourcePlaces.setAdapter(adapter);
            atvDestinationPlaces.setAdapter(adapter);
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
           /* if (progressHUD.isShowing()) {
                progressHUD.dismiss();
            }*/


            GetDistanceTask parserTask = new GetDistanceTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);


        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=true";


        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&mode=driving" + "&" + sensor;

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
    public class GetDistanceTask extends AsyncTask<String, Integer, JSONObject> {

        // Parsing the data in non-ui thread
        @Override
        protected JSONObject doInBackground(String... jsonData) {

            /*JSONObject jObject=null;*/
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jObject;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(JSONObject result) {


            try {
                JSONArray routeJsonArray = result.getJSONArray("routes");
                if (routeJsonArray != null) {
                    for (int i = 0; i <= routeJsonArray.length(); i++) {
                        JSONObject jsonObject = routeJsonArray.getJSONObject(i);

                        JSONArray jLegs = jsonObject.getJSONArray("legs");
                        for (int j = 0; i <= jLegs.length(); j++) {
                            JSONObject jsonObject1 = jLegs.getJSONObject(j);
                            JSONObject distanceObject = jsonObject1.getJSONObject("distance");
                            distance = distanceObject.getString("text");
                            Log.e("distance", distance);
                            JSONObject timeObject = jsonObject1.getJSONObject("duration");
                            time = timeObject.getString("text");
                            Log.e("time", time);

                            mDistanceLinear.setVisibility(View.VISIBLE);
                            mTimeLinear.setVisibility(View.VISIBLE);
                            mTime_Text.setVisibility(View.VISIBLE);
                            mDistance_text.setVisibility(View.VISIBLE);
                            mDistance_value.setText(distance);
                            mTime_value.setText(time);

                            // Log.e("distance",s);
                        }


                    }
                } else {

                }
            } catch (Exception e) {

            }


        }


    }

}
