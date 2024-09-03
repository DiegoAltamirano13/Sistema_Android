package com.diego.lina.sistemadealmacenes;

import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

public class CaptureActivituPortrait extends CaptureActivity implements CompoundBarcodeView.TorchListener {
    private BarcodeView barcodeView;
    private CompoundBarcodeView.TorchListener torchListener;



    @Override
    public void onTorchOn() {
        barcodeView.setTorch(true);

        if (torchListener != null) {
            torchListener.onTorchOn();
        }
    }

    @Override
    public void onTorchOff() {
        barcodeView.setTorch(false);

        if (torchListener != null) {
            torchListener.onTorchOff();
        }
    }
}