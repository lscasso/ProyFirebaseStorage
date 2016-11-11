package com.lscasso.proyfirebasestorage;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.EventListener;

public class MainActivity extends AppCompatActivity
        {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )) {
                ActivityCompat.requestPermissions(this,
                        new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        }
    }


    public void uploadImage(View v) {
        String sd = System.getenv("SECONDARY_STORAGE");
        Log.v("lalal",sd);
        File directorioFotos = new File(
                Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath()
                + "/Camera");
        File files[] = directorioFotos.listFiles();
        File foto = null;
        Log.v("lala",directorioFotos.getAbsolutePath());
        for(File f : files){
            if (!f.isDirectory()){
                foto = f;
                break;
            }
        }
        FirebaseStorage fs = FirebaseStorage.getInstance();
        StorageReference sr = fs.getReferenceFromUrl(
                "gs://proyfirebasestorage.appspot.com");
        StorageReference srFile = sr.child(foto.getName());

        try {
            InputStream is = new FileInputStream(foto);
            UploadTask uploadTask = srFile.putStream(is);

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this,
                            "Va " + taskSnapshot.getBytesTransferred() + "/"
                                , Toast.LENGTH_LONG).show();
                }
            });
            uploadTask.addOnCompleteListener(
                    new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull
                           Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(MainActivity.this,
                            "Termino " + task.getResult(),
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void deleteImage(View v) {
        FirebaseStorage fs = FirebaseStorage.getInstance();
        StorageReference sr = fs.getReferenceFromUrl(
                "gs://proyfirebasestorage.appspot.com");
        StorageReference srFile = sr.child("20161107_201846.jpg");

        srFile.delete();
    }
    public void loadImage(View v) {
        FirebaseStorage fs = FirebaseStorage.getInstance();
        StorageReference sr = fs.getReferenceFromUrl(
                "gs://proyfirebasestorage.appspot.com");
        StorageReference srFile = sr.child("20161107_201846.jpg");
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        Glide.with(this).using(new FirebaseImageLoader())
                .load(srFile)
                .into(iv);
        /*try {
            File localFile = File.createTempFile("foto",".jpg",
                    new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM).getAbsolutePath()
                            + "/Camera"));
            srFile.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this,"Termino",Toast.LENGTH_LONG)
                            .show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
