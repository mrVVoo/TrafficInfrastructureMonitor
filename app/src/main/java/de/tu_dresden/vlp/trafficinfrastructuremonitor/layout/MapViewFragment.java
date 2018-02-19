package de.tu_dresden.vlp.trafficinfrastructuremonitor.layout;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.R;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.backend.DataManager;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStream;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.overlays.TrafficStreamOverlay;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * The MapView Fragment.
 */
public class MapViewFragment extends Fragment implements LocationListener, DataManager.DataManagerListener {

    private MapView mapView;

    private CacheManager cacheManager;
    private List<TrafficStreamOverlay> trafficStreamOverlays = new ArrayList<>();
    private List<TrafficStream> streams = new ArrayList<>();
    private DataManager dataManager;

    public CacheManager getCacheManager() {
        if (cacheManager == null && mapView != null) {
            cacheManager = new CacheManager(mapView);
        }
        return cacheManager;
    }

    public List<TrafficStreamOverlay> getTrafficStreamOverlays() {
        return trafficStreamOverlays;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context ctx = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.fragment_mapview, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setTileSource(new XYTileSource("OSM-BW",1,18,256,".png",new String[]{
                "http://a.tiles.wmflabs.org/bw-mapnik/",
                "http://b.tiles.wmflabs.org/bw-mapnik/",
                "http://c.tiles.wmflabs.org/bw-mapnik/"},"© OpenStreetMap contributors"));

        mapView.getOverlays().add(new CopyrightOverlay(getActivity()));

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
        if (getActivity() instanceof MainActivity) {
            dataManager = ((MainActivity) getActivity()).getDataManager();
            dataManager.addListener(this);
            update();
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    /**
     * Called by {@link DataManager} when {@link TrafficStream}s changed.
     */
    @Override
    public void onDataChanged() {
        mapView.getOverlayManager().removeAll(trafficStreamOverlays);
        trafficStreamOverlays.clear();
        streams.clear();
        update();
    }

    private void update() {
        streams.addAll(dataManager.getTrafficStreams());
        for (TrafficStream stream : streams) {
            trafficStreamOverlays.add(new TrafficStreamOverlay(stream));
        }
        mapView.getOverlayManager().addAll(trafficStreamOverlays);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapView.invalidate();
            }
        });
    }

    /**
     * required for accessing MapView controls, view invalidation etc.
     * @return
     */
    public MapView getMapView() {
        return mapView;
    }

    /**
     * Currently implemented by {@link MainActivity} and called by {@link TrafficStreamOverlay} directly.
     */
    interface MapViewFragmentListener {
        void onTrafficStreamSelected(TrafficStream trafficStream);
    }
}
