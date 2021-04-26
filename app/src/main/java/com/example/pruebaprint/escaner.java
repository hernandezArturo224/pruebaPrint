package com.example.pruebaprint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hp.mobile.scan.sdk.ScanCapture;
import com.hp.mobile.scan.sdk.ScanTicketValidator;
import com.hp.mobile.scan.sdk.Scanner;
import com.hp.mobile.scan.sdk.ScannerCapabilitiesFetcher;
import com.hp.mobile.scan.sdk.ScannerException;
import com.hp.mobile.scan.sdk.browsing.ScannersBrowser;
import com.hp.mobile.scan.sdk.model.Resolution;
import com.hp.mobile.scan.sdk.model.ScanPage;
import com.hp.mobile.scan.sdk.model.ScanTicket;
import com.hp.mobile.scan.sdk.model.ScannerCapabilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class escaner extends AppCompatActivity {

    private List<Scanner> mScanners;
    ScannersBrowser scannerBrowser;
    Scanner scaner;
    ScanTicket ticket;
    String path;

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
        scaner = mScanners.get(0);
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
        ticket = ScanTicket.createWithPreset(ScanTicket.SCAN_PRESET_PHOTO);
        ticket.setSetting(ScanTicket.SCAN_SETTING_RESOLUTION,new Resolution(1200,1200));
        scaner.validateTicket(ticket, new ScanTicketValidator.ScanTicketValidationListener() {
            @Override
            public void onScanTicketValidationComplete(ScanTicket scanTicket) {

            }

            @Override
            public void onScanTicketValidationError(ScannerException e) {

            }
        });
    }

    public void startScanning(){
        //path = Common.getAppPath(escaner.this)+"prueba"+ticket.get;
        scaner.scan(path, ticket, new ScanCapture.ScanningProgressListener() {
            @Override
            public void onScanningPageDone(ScanPage scanPage) {

            }

            @Override
            public void onScanningError(ScannerException e) {
                e.printStackTrace();
            }

            @Override
            public void onScanningComplete() {
                Toast.makeText(escaner.this,"Scan succesful",Toast.LENGTH_SHORT).show();
            }
        });
    }


}