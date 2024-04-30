var exec = require("cordova/exec");

var NFCIDPlugin = {
  readNFC: function (success, error) {
    exec(success, error, "NFCIDPlugin", "readNFC", []);
  },
};

module.exports = NFCIDPlugin;

// Make plugin work under window.plugins
if (!window.plugins) {
  window.plugins = {};
}
if (!window.plugins.intent) {
  window.plugins.nfcid = NFCIDPlugin;
}
