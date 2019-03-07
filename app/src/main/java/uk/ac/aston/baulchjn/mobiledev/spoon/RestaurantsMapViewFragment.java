package uk.ac.aston.baulchjn.mobiledev.spoon;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.here.android.mpa.cluster.ClusterLayer;
import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.MapEngine;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapView;
import com.here.android.mpa.mapping.SupportMapFragment;

import java.io.File;
import java.util.List;

import uk.ac.aston.baulchjn.mobiledev.spoon.home.RestaurantContent;
import uk.ac.aston.baulchjn.mobiledev.spoon.home.RestaurantItem;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class RestaurantsMapViewFragment extends Fragment {

    // map embedded in the map fragment
    private Map map = null;
    private MapView mapView = null;

    private static View view;

    private MapGesture.OnGestureListener tapGestureListener = new MapGesture.OnGestureListener.OnGestureListenerAdapter()
    {
        @Override
        public boolean onTapEvent(PointF p) {
            GeoCoordinate c = map.pixelToGeo(p);
            // c is your geoccordinate on the map, where you clicked on the screen
            // [...]
            Log.i("spoonlogcat: ", c.toString());

            RestaurantItem closestTap = null;
            double nearestDistance = Double.MAX_VALUE;

            for(int i = 0; i < RestaurantContent.restaurantItems.size(); i++){
                GeoCoordinate restaurantLocation = new GeoCoordinate(Double.parseDouble(RestaurantContent.restaurantItems.get(i).getLatitude()), Double.parseDouble(RestaurantContent.restaurantItems.get(i).getLongitude()));

                double theDistance = c.distanceTo(restaurantLocation);
                if(theDistance < nearestDistance){
                    closestTap = RestaurantContent.restaurantItems.get(i);
                    nearestDistance = theDistance;
                }
            }

            // generate a heuristic based on whether or not the tap was close enough
            final double HEURISTIC_TAP = 50; // increase this to increase the range of false taps allowed
            Log.i("spoonlogcat: ", "The nearest Restaurant is... " + closestTap.getName() + ", and the distance to here was: " + nearestDistance);


            return true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.simpleFrameLayout, mapFragment).commit();

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_restaurants_map_view, container, false);

            ApplicationContext appCtx = new ApplicationContext(getContext());

            boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                    getContext().getExternalFilesDir(null) + File.separator + ".here-maps",
                    "android.intent.action.MAIN"); /* ATTENTION! Do not forget to update {YOUR_INTENT_NAME} */

            mapFragment.init(appCtx, new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {

                        // retrieve a reference of the map from the map fragment
                        map = mapFragment.getMap();

                        mapFragment.getMapGesture().addOnGestureListener(tapGestureListener, 10, true);

                        restaurantsWereRefreshed();
                    } else {
                        Log.e("spoonlogcat:", "ERROR: Cannot initialize Map Fragment: " + error.getStackTrace());
                    }
                }
            });
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }


        return view;
    }

    // Update the map when we get new fresh data
    public void restaurantsWereRefreshed(){
        centreMapOnUserLocation();

        ClusterLayer cl = new ClusterLayer();

        // add the restaurants to the cluster
        for(int i = 0; i < RestaurantContent.restaurantItems.size(); i++){
            final MapMarker mapMarker = new MapMarker();
            final double latitude = Double.parseDouble(RestaurantContent.restaurantItems.get(i).getLatitude());
            final double longitude = Double.parseDouble(RestaurantContent.restaurantItems.get(i).getLongitude());

            final GeoCoordinate geoCoordinate = new GeoCoordinate(latitude, longitude);

            mapMarker.setCoordinate(geoCoordinate);
            cl.addMarker(mapMarker);
        }
        map.addClusterLayer(cl);


    }

    private void centreMapOnFirstRestaurant(){
        RestaurantItem firstRestaurant = RestaurantContent.restaurantItems.get(0);

        double latitude = Double.parseDouble(firstRestaurant.getLatitude());
        double longitude = Double.parseDouble(firstRestaurant.getLongitude());
        GeoCoordinate coord = new GeoCoordinate(latitude, longitude);
        map.setCenter(coord, Map.Animation.NONE);
    }

    private void centreMapOnUserLocation(){
        if(ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(), "We need your location, or the map will not work! :(", Toast.LENGTH_SHORT).show();
            return;
        }

        LocationManager manager = ((MainActivity)getActivity()).getLocationManager();
        Location locationGPS = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location coarseGPS = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location passiveGPS = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        // Get the best location
        Location bestLocation = null;

        if(locationGPS != null){
            bestLocation = locationGPS;
        } else if(coarseGPS != null){
            bestLocation = coarseGPS;
        } else if(passiveGPS != null){
            bestLocation = passiveGPS;
        } else {
            // location unavailable, default to Aston University
            bestLocation = new Location("");
            bestLocation.setLatitude(52.486208);
            bestLocation.setLongitude(-1.888499);
            Toast.makeText(getContext(), "We don't have any location data for the user!", Toast.LENGTH_LONG).show();
        }

        GeoCoordinate coord = new GeoCoordinate(bestLocation.getLatitude(), bestLocation.getLongitude());

        map.setCenter(coord, Map.Animation.NONE);
    }

    public RestaurantsMapViewFragment(){
        // Required empty constructor
    }

//    private OnEngineInitListener engineInitHandler = new OnEngineInitListener() {
//        @Override
//        public void onEngineInitializationCompleted(Error error) {
//            if (error == Error.NONE) {
//
//                // more map initial settings
//            } else {
//                Log.e("spoonlogcat: ", "ERROR: Cannot initialize MapEngine " + error);
//                Log.e("spoonlogcat: ", error.getDetails());
//                Log.e("spoonlogcat: ", error.getStackTrace());
//            }
//        }
//    };

//    @Override
//    public void onResume() {
//        super.onResume();
//        MapEngine.getInstance().onResume();
//        if (mapView != null) {
//            mapView.onResume();
//        }
//    }

//    @Override
//    public void onPause() {
//        if (mapView != null) {
//            mapView.onPause();
//        }
//        MapEngine.getInstance().onPause();
//        super.onPause();
//    }
}
