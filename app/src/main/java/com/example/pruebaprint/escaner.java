package com.example.pruebaprint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.hp.mobile.scan.sdk.Scanner;
import com.hp.mobile.scan.sdk.ScannerCapabilitiesFetcher;
import com.hp.mobile.scan.sdk.ScannerException;
import com.hp.mobile.scan.sdk.browsing.ScannersBrowser;
import com.hp.mobile.scan.sdk.model.ScannerCapabilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class escaner extends AppCompatActivity {

    private List<Scanner> mScanners;
    ScannersBrowser scannerBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaner);

        mScanners = new ArrayList<>();
        scannerBrowser = new ScannersBrowser(escaner.this);
    }

    public void searchScanners(View v){
        scannerBrowser.start(new ScannersBrowser.ScannerAvailabilityListener() {
            @Override
            public void onScannerFound(Scanner scanner) {
                mScanners.add(scanner);
            }

            @Override
            public void onScannerLost(Scanner scanner) {
                mScanners.remove(scanner);
            }
        });
    }

    public void putCapabilities(){
        Scanner scaner = mScanners.get(0);
        scaner.fetchCapabilities(new ScannerCapabilitiesFetcher.ScannerCapabilitiesListener() {
            @Override
            public void onFetchCapabilities(ScannerCapabilities scannerCapabilities) {
                Map<String, Object> mapa=scannerCapabilities.getCapabilities();
            }

            @Override
            public void onFetchCapabilitiesError(ScannerException e) {
                e.printStackTrace();
            }
        });
    }

    public void setTicket(){

    }


}