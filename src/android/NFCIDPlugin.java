package de.meko.nfc.id;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.PendingIntent;
import android.util.Log;

public class NFCIDPlugin extends CordovaPlugin {
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private CallbackContext nfcCallbackContext; // to keep the JavaScript callback context

    @Override
    public void pluginInitialize() {
        super.pluginInitialize();
        setupNFC();
    }

    private void setupNFC() {
        // Initialize the NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(cordova.getActivity());
        if (nfcAdapter == null) {
            Log.e("NFCIDPlugin", "NFC is not available.");
            sendErrorToJavaScript("NFC is not available.");
            return;
        }

        // Create an intent that points to the current activity with FLAG_ACTIVITY_SINGLE_TOP
        Intent intent = new Intent(cordova.getActivity(), cordova.getActivity().getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }
        pendingIntent = PendingIntent.getActivity(cordova.getActivity(), 0, intent, flags);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("registerNFC".equals(action)) {
            this.nfcCallbackContext = callbackContext;
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            Log.i("NFCIDPlugin", "registerNFC called");
            return true;
        }
        return false;
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);

        Log.i("NFCIDPlugin", "onResume called");

        // Retrieve the current intent that resumed the activity
        Intent intent = cordova.getActivity().getIntent();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) ||
            NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
            NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            handleIntent(intent);
        }

        // Create a PendingIntent to handle NFC events
        Intent nfcIntent = new Intent(cordova.getActivity(), cordova.getActivity().getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(cordova.getActivity(), 0, nfcIntent, flags);

        // Enable foreground dispatch to handle NFC events
        if (nfcAdapter != null) {
            IntentFilter[] nfcIntentFilter = new IntentFilter[]{
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
            };
            nfcAdapter.enableForegroundDispatch(cordova.getActivity(), pendingIntent, nfcIntentFilter, null);
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);

        Log.i("NFCIDPlugin", "onPause called");

        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(cordova.getActivity());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.i("NFCIDPlugin", "onNewIntent called");
        super.onNewIntent(intent);
        cordova.getActivity().setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            processNfcIntent(intent);
        }
    }

    private void processNfcIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            String tagId = bytesToHexString(tag.getId());
            Log.i("NFCIDPlugin", "sendTagIdToJavaScript called");
            sendTagIdToJavaScript(tagId);
        }
    }

    private void sendTagIdToJavaScript(String tagId) {
        if (nfcCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, tagId);
            result.setKeepCallback(true);
            nfcCallbackContext.sendPluginResult(result);
        }
    }

    private void sendErrorToJavaScript(String error) {
        if (nfcCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, error);
            result.setKeepCallback(true);
            nfcCallbackContext.sendPluginResult(result);
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