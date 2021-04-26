package com.example.pruebaprint;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PathGetter {

    public static String getRealPathFromURI(Uri contentURI, Context context){
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI,null,null,null,null);
        if(cursor == null){
            result = contentURI.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }

        return result;
    }

    @Nullable
    public static String createCopyAndReturnRealPath(@NonNull Context context, @NonNull Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver(); //Cogemos el proveedor de contenido
        if (contentResolver == null) //si es nulo, devolvemos nulo
            return null;

        // Creamos un archivo en la direccion de la app
        String filePath = context.getApplicationInfo().dataDir + File.separator
                + System.currentTimeMillis();

        File file = new File(filePath); //Abrimos el archivo con la ruta dada
        try {
            InputStream inputStream = contentResolver.openInputStream(uri); //Abrimos el Archivo que queremos imprimir para lectura
            if (inputStream == null)
                return null;

            OutputStream outputStream = new FileOutputStream(file); //Abrimos el archivo creado para escritura
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) != -1){ //mientras la longitud de lectura no sea -1 escribimos
                outputStream.write(buf, 0, len);
            }


            outputStream.close(); //cerramos los archivos
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }

        return file.getAbsolutePath(); //devolvemos la ruta absoluta del archivo
    }
}
