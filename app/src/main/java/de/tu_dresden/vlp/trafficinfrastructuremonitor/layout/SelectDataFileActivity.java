package de.tu_dresden.vlp.trafficinfrastructuremonitor.layout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.R;
import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException;
import ir.sohreco.androidfilechooser.FileChooser;

/**
 * Dialog for selecting a file from the local file system.
 */
public class SelectDataFileActivity extends AppCompatActivity {
    private final static int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 13;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_data_file);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            addFileChooserFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addFileChooserFragment();
            }
        }
    }

    private void addFileChooserFragment() {
        FileChooser.Builder builder = new FileChooser.Builder(FileChooser.ChooserType.FILE_CHOOSER,
                new FileChooser.ChooserListener() {
                    @Override
                    public void onSelect(String path) {
                        setResult(RESULT_OK, new Intent().putExtra("path", path));
                        SelectDataFileActivity.this.finish();
                    }
                }).setMultipleFileSelectionEnabled(false).setFileFormats(new String[]{".xml"});
        try {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.file_chooser_fragment_container_framelayout, builder.build())
                    .commit();
        } catch (ExternalStorageNotAvailableException e) {
            Toast.makeText(this, "There is no external storage available on this device.",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
