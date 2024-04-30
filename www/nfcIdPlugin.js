var exec = require("cordova/exec");

var NFCPlugin = {
  readNFC: function (success, error) {
    exec(success, error, "NFCPlugin", "readNFC", []);
  },
};

module.exports = NFCPlugin;
