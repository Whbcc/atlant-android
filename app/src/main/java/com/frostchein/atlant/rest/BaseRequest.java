package com.frostchein.atlant.rest;

import android.util.Log;
import com.frostchein.atlant.events.network.OnStatusError;
import com.frostchein.atlant.events.network.OnStatusTimeOut;
import java.net.SocketTimeoutException;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class BaseRequest<T> implements Callback<T> {

  private static final String TAG_LOG = "TAG_RETROFIT";
  private int requestCode = 0;
  private Callback<T> callback;

  interface Callback<T> {

    void onResponse(Response<T> response);
  }

  BaseRequest(Callback<T> callback, int requestCode) {
    this.callback = callback;
    this.requestCode = requestCode;
  }

  BaseRequest(Callback<T> callback) {
    this.callback = callback;
  }

  @Override
  public void onResponse(Call<T> call, Response<T> response) {
    if (callback != null) {
      if (response.isSuccessful()) {
        Log.i(TAG_LOG, "onResponse isSuccessful true");
        callback.onResponse(response);
      }
    } else {
      Log.i(TAG_LOG, "onResponse isSuccessful fail");
      EventBus.getDefault().post(new OnStatusError(requestCode));
    }
  }

  @Override
  public void onFailure(Call<T> call, Throwable t) {
    if (call.isCanceled()) {
      Log.i(TAG_LOG, "onFailure cancel");
      return;
    }
    if (t instanceof SocketTimeoutException) {
      Log.i(TAG_LOG, "onFailure timeout");
      EventBus.getDefault().post(new OnStatusTimeOut(requestCode));
    } else {
      Log.i(TAG_LOG, "onFailure " + t.getMessage());
      EventBus.getDefault().post(new OnStatusError(requestCode));
    }
  }
}
