package shwethasp.com.googlemapmodules.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import shwethasp.com.googlemapmodules.R;

public class GotoMapActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mPlotMarker,mDrawRoute,mFindDistanceTime;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goto_map);

        initializeUi();

        //TO plot the marker
        mPlotMarker.setOnClickListener(this);
        //To Draw the route path
        mDrawRoute.setOnClickListener(this);
        //To find the distance between source and destination
        mFindDistanceTime.setOnClickListener(this);
    }

    private void initializeUi(){

        mPlotMarker= (Button) findViewById(R.id.plotting_markerbtn);
        mDrawRoute= (Button) findViewById(R.id.draw_route);
        mFindDistanceTime=(Button) findViewById(R.id.find_distance);




    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.plotting_markerbtn:
                intent=new Intent(GotoMapActivity.this,MainActivity.class);
                startActivity(intent);
                break;

            case R.id.draw_route:

              intent=new Intent(GotoMapActivity.this,DrawRoute.class);
                startActivity(intent);
                break;

            case R.id.find_distance:

             intent=new Intent(GotoMapActivity.this,DistanceAndTimeActivity.class);
                startActivity(intent);
                break;
        }
    }
}
