package org.brit.permission;

/**
 * Created by Serhii Bryt
 * 02.04.2024 14:40
 **/
public enum Permissions {
    GEOLOCATION("geolocation"),
    MIDI("midi"),
    MIDI_SYSEX("midi-sysex"),
    NOTIFICATIONS("notifications"),
    CAMERA("camera"),
    MICROPHONE("microphone"),
    BACKGROUND_SYNC("background-sync"),
    AMBIENT_LIGHT_SENSOR("ambient-light-sensor"),
    ACCELEROMETER("accelerometer"),
    GYROSCOPE("gyroscope"),
    MAGNETOMETER("magnetometer"),
    ACCESSIBILITY_EVENTS("accessibility-events"),
    CLIPBOARD_READ("clipboard-read"),
    CLIPBOARD_WRITE("clipboard-write"),
    PAYMENT_HANDLER("payment-handler");

    private String value;

    Permissions(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
