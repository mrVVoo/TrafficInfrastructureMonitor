package de.tu_dresden.vlp.trafficinfrastructuremonitor;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class MainActivityFragment extends Fragment implements LocationListener {

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context ctx = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        LocationManager locationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        Location currentLocation = null;
        for (String provider : locationManager.getProviders(true)) {
            Location tmp = locationManager.getLastKnownLocation(provider);
            if (tmp != null) {
                currentLocation = tmp;
            }
        }

        IMapController mapController = mapView.getController();
        if (currentLocation != null) {
            mapController.setZoom(15);
            mapController.setCenter(new GeoPoint(currentLocation));
        }

        List<GeoPoint> geoPoints = new ArrayList<>();
        geoPoints.add(new GeoPoint(51.0290, 13.7311));
        geoPoints.add(new GeoPoint(51.02990, 13.73091));
        geoPoints.add(new GeoPoint(51.0309, 13.73061));
        Polyline line = new Polyline();   //see note below!
        line.setPoints(geoPoints);
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                Toast.makeText(mapView.getContext(), "polyline with " + polyline.getPoints().size() + "pts was tapped", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        mapView.getOverlayManager().add(line);
        mapView.getController().animateTo(geoPoints.get(0));

        return view;
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/xml".equals(type) || "application/xml".equals(type)) {

                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mapView.getZoomLevel() < 9) {
            mapView.getController().setZoom(9);
        }
        mapView.getController().animateTo(new GeoPoint(location));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
