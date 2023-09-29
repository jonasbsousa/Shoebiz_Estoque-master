package br.com.shoebiz.shoeconf_2.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import br.com.shoebiz.shoeconf_2.R;

public class CustomScannerActivity extends BaseActivity {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);
        setUpToolBar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_custom_scanner);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

        ViewfinderView viewfinderView = findViewById(R.id.zxing_viewfinder_view);
        viewfinderView.setLaserVisibility(true);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.setShowMissingCameraPermissionDialog(false);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}