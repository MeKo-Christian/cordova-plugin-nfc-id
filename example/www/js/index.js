// Wait for the deviceready event before using any of Cordova's device APIs.
// See https://cordova.apache.org/docs/en/latest/cordova/events/events.html#deviceready
document.addEventListener("deviceready", onDeviceReady, false);

function onDeviceReady() {
    console.log("Running cordova-" + cordova.platformId + "@" + cordova.version);
    document.getElementById("deviceready").classList.add("ready");

    // Register for NFC reading immediately
    registerNFC();
}

function registerNFC() {
    cordova.plugins.nfcid.registerNFC(
        function () {
            console.log("NFC read registration successful");
        },
        function (error) {
            console.error("Error registering for NFC reads:", error);
        }
    );

    // Listen for NFC tags being read
    document.addEventListener('nfcTagRead', function (event) {
        console.log("NFC Tag Read:", event.tagId);
        document.getElementById("nfcId").textContent = "NFC ID: " + event.tagId;
    });
}