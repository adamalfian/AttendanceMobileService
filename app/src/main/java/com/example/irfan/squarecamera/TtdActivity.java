package com.example.irfan.squarecamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.williamww.silkysignature.views.SignaturePad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class TtdActivity extends AppCompatActivity {

    private SignaturePad mSignaturePad;
    private Button sendTtdButton, clearTtdButton;
    private EditText nrpTtd, passwrdTtd;
    private static String BASE_DIR = "camtest/";
    private String nrp = "5115100705";
    private String password = "admin123";
    private List<String> listPathFile;
    private static String TAG = TtdActivity.class.getSimpleName();
    protected int counter = 0;
    private ArrayList<String> encodedImagesList;
    protected static String UPLOAD_URL = "http://etc.if.its.ac.id/sendSignature/";
    protected SweetAlertDialog loadingDialog, errorDialog, successDialog;
    private int requestCounter = 0;
    private boolean hasRequestFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttd);

        sendTtdButton = findViewById(R.id.btn_sendttd);
        clearTtdButton = findViewById(R.id.btn_clearTtd);
        nrpTtd = findViewById(R.id.nrp_ttd);
        passwrdTtd = findViewById(R.id.passwrd_ttd);
        mSignaturePad = findViewById(R.id.signature_pad);

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                Toast.makeText(TtdActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                sendTtdButton.setEnabled(true);
                clearTtdButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                sendTtdButton.setEnabled(false);
                clearTtdButton.setEnabled(false);

            }
        });

        clearTtdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        sendTtdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long startTime = System.nanoTime();
                uploadFIle();
                long endTime = System.nanoTime();
                long totalTime= endTime - startTime;
                double time = totalTime/1000000000.0;
                Toast.makeText(TtdActivity.this, "waktu upload: " + (float) time, Toast.LENGTH_LONG).show();

            }
        });
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        stream.close();
    }


    protected void uploadFIle() {
//        loadingDialog.setTitleText("Uploading images");
        StringRequest stringRequest;
        for (int i = 0; i < 1; i++) {
            final int index = i;
            stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(), "the response : " + response, Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onResponse: "+response);
                            mSignaturePad.clear();
                            requestCounter--;

                            if (requestCounter == 0 && !hasRequestFailed) {
//                                closeLoadingDialog();
                                showSuccessDialog();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            hasRequestFailed = true;
                            //Showing toast
                            Toast.makeText(TtdActivity.this, volleyError.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    // Get encoded Image
//                    String image = encodedImagesList.get(index);
                    Map<String, String> params = new HashMap<>();
                    // Adding parameters

                    params.put("idUser", nrp );
                    params.put("password", password );
                    params.put("image","data:/image/jpeg;base64," + mSignaturePad.getSignatureBitmap());

                    //returning parameters
                    return params;
                }
            };
            //Adding request to the queue
            requestCounter++;
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    protected void showLoadingDialog() {
        loadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.setTitleText("Uploading Images");
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    protected void closeLoadingDialog() {
        loadingDialog.cancel();
    }

    protected void showSuccessDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success!")
                .setContentText("Images uploaded successfully")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                    }
                })
                .show();
    }

}
