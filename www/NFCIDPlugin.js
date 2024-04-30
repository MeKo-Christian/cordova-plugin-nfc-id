var exec = require("cordova/exec");

var NFCPlugin = {
  readNFC: function (success, error) {
    exec(success, error, "NFCIDPlugin", "readNFC", []);
  },
};

module.exports = NFCPlugin;
