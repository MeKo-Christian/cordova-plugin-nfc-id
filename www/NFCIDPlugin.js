var exec = require("cordova/exec");

var NFCIDPlugin = {
  readNFC: function (success, error) {
    exec(success, error, "NFCIDPlugin", "readNFC", []);
  },
};

module.exports = NFCIDPlugin;
