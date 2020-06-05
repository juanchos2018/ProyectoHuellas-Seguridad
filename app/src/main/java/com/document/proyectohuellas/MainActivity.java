package com.document.proyectohuellas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {


    private Handler handler = new Handler();
    ImageView img1,img2;
    Uri uri1,uri2;
    private final int MIS_PERMISOS = 100;

    TextView t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img1=(ImageView)findViewById(R.id.image1);
        img2=(ImageView)findViewById(R.id.image2);
        Button biometricLoginButton = findViewById(R.id.biometric_login);

        t1=(TextView)findViewById(R.id.idresultad);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirgaleria1();
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirgaleria2();
            }
        });


        biometricLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showBiometricPrompt();
                FingerImageMatch ();
            }
        });


        if(solicitaPermisosVersionesSuperiores()){

        }

    }
    public  void FingerImageMatch ()  {
        Bitmap bitmap = ((BitmapDrawable) img1.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] path1= baos.toByteArray();
        String enoncebase64_1= Base64.encodeToString(path1, Base64.DEFAULT);
        Log.e("huella 1 :",enoncebase64_1);
        Bitmap bitmap1 = ((BitmapDrawable) img2.getDrawable()).getBitmap();
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
        byte[] path2= baos1.toByteArray();
        String enoncebase64_2= Base64.encodeToString(path2, Base64.DEFAULT);
        Log.e("huella 2 ",enoncebase64_2);

        if (enoncebase64_1.equals(enoncebase64_2)){
            Toast.makeText(this, "Son igules", Toast.LENGTH_SHORT).show();
            t1.setText("Son Iguales");
        }
        else {
            Toast.makeText(this, "No son iguales", Toast.LENGTH_SHORT).show();
            t1.setText("No Son Iguales");
        }


    }
    private void abrirgaleria1() {
        //TODO ESTO ES PARA ABRIR LA GALERIA DEL CELULAR
        try {
            Intent intent=new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");
            startActivityForResult(intent.createChooser(intent,"Seleccione"),10);// 10
        }
        catch (Exception ex){
            Toast.makeText(this, "Eror "+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void abrirgaleria2() {
        //TODO ESTO ES PARA ABRIR LA GALERIA DEL CELULAR
        try {
            Intent intent=new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");
            startActivityForResult(intent.createChooser(intent,"Seleccione"),20);// 10
        }
        catch (Exception ex){
            Toast.makeText(this, "Eror "+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode==10 && resultCode==RESULT_OK && data!=null){
                uri1  =data.getData(); //return the uir of selected file
                img1.setImageURI(uri1);
            }

            else   if (requestCode==20 && resultCode==RESULT_OK && data!=null){
                Log.e("archivi ",data.getData().toString());
                uri2=data.getData();
                img2.setImageURI(uri2);
            }

            else{
                Toast.makeText(this, "No seleciono un archivo", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex){
            Toast.makeText(this, "Error "+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean solicitaPermisosVersionesSuperiores() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){//validamos si estamos en android menor a 6 para no buscar los permisos
            return true;
        }

        //validamos si los permisos ya fueron aceptados
        if((getApplicationContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)&&getApplicationContext().checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED){
            return true;
        }

        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)||(shouldShowRequestPermissionRationale(CAMERA)))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MIS_PERMISOS);
        }

        return false;//implementamos el que procesa el evento dependiendo de lo que se defina aqui
    }
    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getApplicationContext());
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe conceder los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }
}
