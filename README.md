# NFCPlugin for Cordova

This plugin enables Cordova applications to read the ID of NFC tags on Android devices. It provides a simple interface for capturing the NFC tag ID, allowing React-based Cordova apps to utilize NFC technology easily.

## Features

- Read NFC tag IDs.
- Simple JavaScript interface for interaction from React components.
- Android platform support.

## Installation

To install the plugin into your Cordova project, run the following command in your project directory:

```bash
cordova plugin add cordova-plugin-nfc-id
```

## Usage

To use the plugin in your Cordova project, follow these steps:

- Make sure your app is running on an actual Android device with NFC capabilities.
- Add the following code to your app's main JavaScript or TypeScript file:

```javascript
document.addEventListener('deviceready', function() {
    cordova.plugins.NFCPlugin.readNFC(
        function(result) {
            console.log('NFC Tag ID:', result);
        },
        function(error) {
            console.error('Error reading NFC tag:', error);
        }
    );
});
```

This code sets up an event listener that calls the readNFC method when the device is ready. The readNFC method triggers reading the NFC tag when one is detected.

## API

`readNFC(success, error)`

- success: A callback function that is called with the NFC tag ID as its argument when an NFC tag is successfully read.
- error: A callback function that is called with an error message if the reading fails.

## Platform Support

This plugin supports only the Android platform as of now.
