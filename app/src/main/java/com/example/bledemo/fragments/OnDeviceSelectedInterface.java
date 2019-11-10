package com.example.bledemo.fragments;

public interface OnDeviceSelectedInterface {
    void onDeviceSelected(String address, String name);
    void connectToGatServer(String address);
}
