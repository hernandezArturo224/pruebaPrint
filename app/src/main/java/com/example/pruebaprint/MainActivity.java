package com.example.pruebaprint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.mobile.scan.sdk.model.ScanTicket;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

   Button btn_create;
   TextView path;
   Button btn_picker;
   Button btn_print;
   String pathUri;
   String formato;

   TextView path1;

   private final int CHOOSE_PDF_FROM_DEVICE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_create = (Button) findViewById(R.id.btn_create);
        btn_picker = (Button) findViewById(R.id.btn_picker);
        btn_print = (Button) findViewById(R.id.print);
        path = (TextView) findViewById(R.id.path);
        path1 = (TextView) findViewById(R.id.paths);
        //path.setText(Common.getDownloadPath(MainActivity.this)+"punteros.pdf");
        //ScanTicket ticket = new ScanTicket.



        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        btn_create.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createPDFFile(Common.getAppPath(MainActivity.this)+"test_pdf.pdf");
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();

        btn_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callChooseFileFromDevice();
            }
        });
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printImage();
                //printPDF();
            }
        });





    }//oncreate

    private void callChooseFileFromDevice(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,CHOOSE_PDF_FROM_DEVICE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_PDF_FROM_DEVICE && resultCode == RESULT_OK){
            if(data != null){
                formato = data.getData().toString();//.substring(data.getData().toString().length()-3,data.getData().toString().length());
                path1.setText(formato);
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                    pathUri = PathGetter.getRealPathFromURI(data.getData(),this);
                    path.setText(pathUri);
                }else{
                    pathUri = PathGetter.createCopyAndReturnRealPath(this,data.getData());
                    path.setText(pathUri);

                }

            }
        }
    }


    private void createPDFFile(String path) {
        if(new File(path).exists()){
            new File(path).delete();
        }

        try{
            Document document = new Document();
            //Save
            PdfWriter.getInstance(document,new FileOutputStream(path)); //guardamos la nueva File con FileOutputStream
            document.open(); //lo abrimos para modificarlo
            //Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("Arturo");
            document.addCreator("Arturo");

            BaseColor colorAccent = new BaseColor(0,153,204,255);
            float fontSize= 20.0f;
            float valueFontSize = 26.0f;

            //CustomFond
            BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf","UTF-8",BaseFont.EMBEDDED);
            //Create Title of document
            Font titleFont = new Font(fontName,36.0f,Font.NORMAL,BaseColor.BLACK);
            addNewItem(document,"Order details", Element.ALIGN_CENTER,titleFont);

            Font orderNumberFont = new Font(fontName,fontSize,Font.NORMAL,colorAccent);
            addNewItem(document,"Order No: ",Element.ALIGN_LEFT,orderNumberFont);

            Font orderNumberValueFont = new Font(fontName,valueFontSize,Font.NORMAL,BaseColor.BLACK);
            addNewItem(document,"#717171",Element.ALIGN_LEFT,orderNumberFont);

            addLineSeparator(document);

            addNewItem(document,"Order Date",Element.ALIGN_LEFT,orderNumberFont);
            addNewItem(document,"3/8/2019",Element.ALIGN_LEFT,orderNumberValueFont);

            addLineSeparator(document);

            addNewItem(document,"Account Name:",Element.ALIGN_LEFT,orderNumberFont);
            addNewItem(document,"Arturo",Element.ALIGN_LEFT,orderNumberValueFont);

            addLineSeparator(document);
            addLineSpace(document);
            addNewItem(document,"Product Detail",Element.ALIGN_CENTER,titleFont);
            addLineSeparator(document);

            document.close();

            Toast.makeText(this, "Success",Toast.LENGTH_SHORT).show();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printPDF() {
        PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
        try{
            PrintDocumentAdapter printDocumentAdapter = new FilePrinter(MainActivity.this,pathUri);
            printManager.print("Document",printDocumentAdapter,new PrintAttributes.Builder().build());
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void printImage(){
        PrintHelper photoPrinter = new PrintHelper(MainActivity.this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = BitmapFactory.decodeFile(pathUri);
        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }

    private void addLineSeparator(Document document) throws DocumentException {
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0,0,0,68));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
    }

    private void addNewItem(Document document, String text, int align, Font font) throws DocumentException {
        Chunk chunk = new Chunk(text,font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);
    }

    public void goToScanner(View v){
        Intent in = new Intent(MainActivity.this, escaner.class);
        startActivity(in);
    }
}