// Wait for the deviceready event before using any of Cordova's device APIs.
// See https://cordova.apache.org/docs/en/latest/cordova/events/events.html#deviceready
document.addEventListener("deviceready", onDeviceReady, false);

function onDeviceReady() {
  // Cordova is now initialized. Have fun!
  console.log("Running cordova-" + cordova.platformId + "@" + cordova.version);
  document.getElementById("deviceready").classList.add("ready");

  // Setup NFC reading
  setupNFC();
}

function setupNFC() {
  // Add event listener for the NFC read button
  document.getElementById("readNfc").addEventListener("click", function () {
    readNFC();
  });
}

function readNFC() {
  cordova.plugins.NFCPlugin.readNFC(
    function (nfcId) {
      console.log("NFC ID: " + nfcId);
      document.getElementById("nfcId").textContent = "NFC ID: " + nfcId;
    },
    function (error) {
      console.error("NFC reading failed: " + error);
      document.getElementById("nfcId").textContent = "Error: " + error;
    }
  );
}
