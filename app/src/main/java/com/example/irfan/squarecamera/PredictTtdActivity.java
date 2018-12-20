package com.example.irfan.squarecamera;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.williamww.silkysignature.views.SignaturePad;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PredictTtdActivity extends AppCompatActivity {

    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private Button mCompressButton;
    private Button mSendButton;
    private EditText nrp_predictTtd, passwrd_predictTtd;
    private String predictionResult = "none";

    private static String TAG = PredictTtdActivity.class.getSimpleName();

    protected int counter = 0;
    private static String BASE_DIR = "camtest/";
    private String nrp = "5115100705";
    private String password = "admin123";
    private List<String> listPathFile;
    private ArrayList<String> encodedImagesList;
    protected SweetAlertDialog loadingDialog;
    protected static String UPLOAD_URL = "http://etc.if.its.ac.id/doPredict_TTD/";
    private int requestCounter = 0;
    private boolean hasRequestFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_ttd);

        mSignaturePad = findViewById(R.id.signature_pad);
        mClearButton = findViewById(R.id.btn_clearTtd);
        mSendButton = findViewById(R.id.btn_sendttd);
        nrp_predictTtd = findViewById(R.id.nrp_predict_ttd);
        passwrd_predictTtd = findViewById(R.id.passwrd_predict_ttd);

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                Toast.makeText(PredictTtdActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mClearButton.setEnabled(true);
                mSendButton.setEnabled(true);

            }

            @Override
            public void onClear() {
                mSendButton.setEnabled(false);
                mClearButton.setEnabled(false);

            }
        });
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nrp_predictTtd.getText().toString().isEmpty() || passwrd_predictTtd.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Isi User dan Password anda!", Toast.LENGTH_SHORT).show();
                } else uploadFIle();
            }
        });
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
                            Toast.makeText(getApplicationContext(), "Prediction Result : " + response, Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onResponse: "+response);
                            predictionResult = response;
                            mSignaturePad.clear();
                            requestCounter--;

                            if (requestCounter == 0 && !hasRequestFailed) {
                                //closeLoadingDialog();
                                showSuccessDialog();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            hasRequestFailed = true;
                            //Showing toast
                            Toast.makeText(PredictTtdActivity.this, volleyError.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    // Get encoded Image
//                    String image = encodedImagesList.get(index);
                    Map<String, String> params = new HashMap<>();
                    // Adding parameters

                    params.put("idUser", nrp_predictTtd.getText().toString() );
                    params.put("password", passwrd_predictTtd.getText().toString() );
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
                .setTitleText("Success! Hasil Prediksi")
                .setContentText(predictionResult)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                })
                .show();
    }

}
