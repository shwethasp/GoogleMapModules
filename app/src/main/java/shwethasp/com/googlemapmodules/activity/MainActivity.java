package shwethasp.com.googlemapmodules.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import shwethasp.com.googlemapmodules.R;
import shwethasp.com.googlemapmodules.model.MarkerModelClass;

public class MainActivity extends AppCompatActivity  {

    protected GoogleMap map;
    protected GoogleApiClient mGoogleApiClient;
    protected LatLng start = new LatLng(12.954647, 77.698599);
    protected LatLng end = new LatLng(12.923176, 77.650505);
    ArrayList<MarkerModelClass> mList;
    private Button mFindDistance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFindDistance=(Button)findViewById(R.id.finddistance);
        //Initializing the google map
        MapsInitializer.initialize(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        }

        map = mapFragment.getMap();



        //Arraylist of MarkerModelClass

        mList = new ArrayList<MarkerModelClass>();
        mList.add(new MarkerModelClass(12.959591, 77.701393,"Kala Mandir Marathalli"));
        mList.add(new MarkerModelClass(12.956494, 77.701240,"Chandra Layout"));
        mList.add(new MarkerModelClass(12.952243, 77.700059,"Innovative Multiplex Bus Stop"));
        mList.add(new MarkerModelClass(12.950045, 77.699662,"Kala Mandir Marathalli"));
        mList.add(new MarkerModelClass(12.942244, 77.697097,"Multiplex Marathahalli"));
        mList.add(new MarkerModelClass(12.939566, 77.695558,"JP Morgen bus stop"));
        mList.add(new MarkerModelClass(12.935246, 77.691134,"City Light Apartment"));
        mList.add(new MarkerModelClass(12.931629, 77.687345,"New Horizon College Stop"));
        mList.add(new MarkerModelClass(12.924472, 77.674145,"Devarabisanahalli, Bellandur"));
        mList.add(new MarkerModelClass(12.921576, 77.667804,"NH7, Bellandur"));
        mList.add(new MarkerModelClass(12.921293, 77.661721,"Sarjapura Cross,"));
        mList.add(new MarkerModelClass(12.919997, 77.651507,"Iblur Bus Stop"));

        plotsMarkerPoints();





    }



    //Plaot the marker point
    private void plotsMarkerPoints(){

        //aray list of makeroption
        ArrayList<MarkerOptions> optionPoint = new ArrayList<MarkerOptions>();
        MarkerOptions options = new MarkerOptions();

        //Setting the title for source
        options.position(start).title("Marathalli bus stop");

        //setting the image for source
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_pin));
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end).title("Agra Bus Stop");

        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_pin));
        map.addMarker(options);

        //pickup drop point marker
        options = new MarkerOptions();

        for (int i = 0; i < mList.size(); i++) {
            options.position(new LatLng(mList.get(i).getLatValue(), mList.get(i).getLongValue())).title(mList.get(i).getTitle());
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.inbetween_stops_pin));
            map.addMarker(options);
        }


        //Zoom the map in desired location
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(12.935246, 77.691134));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
        map.moveCamera(center);
        map.animateCamera(zoom);

    }

}
