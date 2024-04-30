package de.meko.nfc.id;

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
  private CallbackContext callbackContext; // Declare callbackContext here

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.callbackContext = callbackContext; // Assign callbackContext from method parameter to class member
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

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
      flags |= PendingIntent.FLAG_IMMUTABLE; // Add FLAG_IMMUTABLE for Android 12 and above
    }

    pendingIntent = PendingIntent.getActivity(cordova.getActivity(), 0, intent, flags);

    if (nfcAdapter != null && nfcAdapter.isEnabled()) {
      nfcAdapter.enableForegroundDispatch(cordova.getActivity(), pendingIntent, null, null);
    } else {
      callbackContext.error("NFC is not available or enabled."); // Correct usage of callbackContext
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
      callbackContext.success(bytesToHexString(id)); // Correct usage of callbackContext
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