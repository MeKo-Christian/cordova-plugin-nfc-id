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
    private CallbackContext nfcCallbackContext; // to keep the JavaScript callback context

    @Override
    public void pluginInitialize() {
        super.pluginInitialize();
        initNFC();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("registerNFC".equals(action)) {
            this.nfcCallbackContext = callbackContext; // Save the callback context to use for later
            registerNFC(); // Ensure NFC is initialized and setup to read tags
            return true;
        }
        return false;
    }

    private void initNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(cordova.getActivity());
        Intent intent = new Intent(cordova.getActivity(), cordova.getActivity().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        pendingIntent = PendingIntent.getActivity(cordova.getActivity(), 0, intent, flags);

        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            LOG.e("NFCIDPlugin", "NFC is not available or enabled.");
        }
    }

    private void registerNFC() {
        if (nfcAdapter != null && pendingIntent != null) {
            nfcAdapter.enableForegroundDispatch(cordova.getActivity(), pendingIntent, null, null);
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
            nfcCallbackContext.sendPluginResult(result);
        } else {
            nfcCallbackContext.error("NFC is not available or enabled.");
        }
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        if (nfcAdapter != null && pendingIntent != null) {
            nfcAdapter.enableForegroundDispatch(cordova.getActivity(), pendingIntent, null, null);
        }
        handleIntent(cordova.getActivity().getIntent());
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(cordova.getActivity());
        }
    }

    private void handleIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                String tagId = bytesToHexString(tag.getId());
                sendEventToJavaScript(tagId);
            }
        }
    }

    private void sendEventToJavaScript(String tagId) {
        PluginResult result = new PluginResult(PluginResult.Status.OK, tagId);
        result.setKeepCallback(true);
        nfcCallbackContext.sendPluginResult(result);
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02X", b));
        }
        return stringBuilder.toString();
    }
}