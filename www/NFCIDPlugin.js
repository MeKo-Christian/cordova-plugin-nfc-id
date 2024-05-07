var exec = require("cordova/exec");

var NFCIDPlugin = {
  // Function to register for NFC reads
  registerNFC: function (success, error) {
    exec(success, error, "NFCIDPlugin", "registerNFC", []);
  }
};

// Export the plugin object
module.exports = NFCIDPlugin;

// Ensure it's accessible under window.plugins
if (!window.plugins) {
  window.plugins = {};
}
window.plugins.nfcid = NFCIDPlugin;
