package com.cordova.plugin.NFCIDPlugin;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.content.Intent;
import android.app.PendingIntent;

public class NFCIDPlugin extends CordovaPlugin {
  private NfcAdapter nfcAdapter;
  private PendingIntent pendingIntent;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("readNFC")) {
      initNFC(); // Ensure NFC is initialized
      return true;
    }
    return false;
  }

  private void initNFC() {
    nfcAdapter = NfcAdapter.getDefaultAdapter(cordova.getActivity());
    Intent intent = new Intent(cordova.getActivity(), cordova.getActivity().getClass())
        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    pendingIntent = PendingIntent.getActivity(cordova.getActivity(), 0, intent, 0);

    if (nfcAdapter != null && nfcAdapter.isEnabled()) {
      nfcAdapter.enableForegroundDispatch(cordova.getActivity(), pendingIntent, null, null);
    } else {
      callbackContext.error("NFC is not available or enabled.");
    }
  }

  @Override
  public void onResume(boolean multitasking) {
    super.onResume(multitasking);
    Intent intent = cordova.getActivity().getIntent();
    if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
      handleIntent(intent);
    }
  }

  private void handleIntent(Intent intent) {
    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    if (tag != null) {
      byte[] id = tag.getId();
      callbackContext.success(bytesToHexString(id));
    }
  }

  private String bytesToHexString(byte[] bytes) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b : bytes) {
      stringBuilder.append(String.format("%02X", b));
    }
    return stringBuilder.toString();
  }
}