package de.tu_dresden.vlp.trafficinfrastructuremonitor.layout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.BuildConfig;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.R;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.backend.DataManager;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.backend.ExportGenerator;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStream;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Markus Wutzler on 25.01.18.
 */
public class MainActivity extends AppCompatActivity implements TrafficStreamInfoFragment.OnFragmentInteractionListener, MapViewFragment.MapViewFragmentListener {

    private Intent mRequestFileIntent;
    private static final int mRequestFileCode = 777;

    private TrafficStreamInfoFragment trafficStreamInfoFragment;
    private MapViewFragment mapViewFragment;
    private DataManager dataManager;

    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = new DataManager(getApplicationContext());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Configuration.getInstance().load(this, prefs);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        Configuration.getInstance().setDebugMode(true);
        Configuration.getInstance().setDebugMapTileDownloader(true);
        Configuration.getInstance().setDebugMapView(true);
        Configuration.getInstance().setDebugTileProviders(true);
        Configuration.getInstance().save(this, prefs);

        mRequestFileIntent = new Intent(this, SelectDataFileActivity.class);
        setContentView(R.layout.activity_main);

        if (dataManager.getTrafficStreams().isEmpty()) {
            new AlertDialog.Builder(this).setMessage("Es sind keine Daten vorhanden! Daten (XML-Datei) können über das Menü geladen werden!").setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.load_data:
                startActivityForResult(mRequestFileIntent, mRequestFileCode);
                return true;
            case R.id.export_data:
                new ExportGenerator(this, dataManager.getDatabase()).execute(dataManager.getTrafficStreams().toArray(new TrafficStream[dataManager.getTrafficStreams().size()]));
                return true;
            case R.id.refresh_map:
                refreshMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshMap() {
        CacheManager cacheManager = getMapViewFragment().getCacheManager();
        ArrayList<MapTile> tiles = new ArrayList<>();
        for (TrafficStream stream : dataManager.getTrafficStreams()) {
            tiles.addAll(CacheManager.getTilesCoverage(new ArrayList<>(stream.getCoordinates()), 14, 19));
        }
        Iterator<MapTile> tileIterator = tiles.iterator();
        while (tileIterator.hasNext()) {
            MapTile tile = tileIterator.next();
            if (cacheManager.checkTile(tile)) tileIterator.remove();
        }
        cacheManager.downloadAreaAsync(this, tiles, 14, 19);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent returnIntent) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            final String path = returnIntent.getStringExtra("path");
            if (path != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dataManager.load(new File(path));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(MainActivity.this).setMessage(R.string.dialog_download_map).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            MainActivity.this.refreshMap();
                                        }
                                    }).setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).create().show();
                                }
                            });
                        } catch (final IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataManager.invalidate();
            }
        }).start();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTrafficStreamSelected(TrafficStream trafficStream) {
        if (trafficStreamInfoFragment == null) {
            trafficStreamInfoFragment = TrafficStreamInfoFragment.newInstance(trafficStream);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_traffic_stream_info_container,
                            trafficStreamInfoFragment,
                            TrafficStreamInfoFragment.TAG)
                    .commit();
        } else {
            trafficStreamInfoFragment.setTrafficStream(trafficStream);
        }
    }

    private MapViewFragment getMapViewFragment() {
        if (mapViewFragment == null) {
            mapViewFragment = (MapViewFragment) getFragmentManager().findFragmentById(R.id.fragment_mapview);
        }
        return mapViewFragment;
    }
}
