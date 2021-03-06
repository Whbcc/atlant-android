package com.frostchein.atlant.activities.camera;

import com.frostchein.atlant.activities.base.BasePresenter;
import com.google.zxing.Result;
import javax.inject.Inject;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import org.web3j.crypto.WalletUtils;

public class CameraPresenterImpl implements CameraPresenter,
    ZXingScannerView.ResultHandler, BasePresenter {

  private ZXingScannerView mScannerView;
  private CameraView view;
  private int typeResult;

  @Inject
  CameraPresenterImpl(CameraView view) {
    this.view = view;
  }

  @Override
  public void onCreate(int typeResult) {
    this.typeResult = typeResult;
    mScannerView = new ZXingScannerView(view.getContext());
    view.scannerView(mScannerView);
  }

  @Override
  public void onResume() {
    mScannerView.setResultHandler(this);
    mScannerView.startCamera();
  }

  @Override
  public void onPause() {
    mScannerView.stopCamera();
  }

  @Override
  public void handleResult(Result result) {
    if (typeResult == CameraActivity.TAG_FROM_SEND || typeResult == CameraActivity.TAG_FROM_HOME) {
      if (WalletUtils.isValidAddress(result.getText())) {
        view.onSuccessfulScanQR(result.getText());
      } else {
        view.onFailScanQR();
        mScannerView.resumeCameraPreview(this);
      }
    }
    if (typeResult == CameraActivity.TAG_FROM_IMPORT) {
      if (WalletUtils.isValidPrivateKey(result.getText())) {
        view.onSuccessfulScanQR(result.getText());
      } else {
        view.onFailScanQR();
        mScannerView.resumeCameraPreview(this);
      }
    }
  }
}
