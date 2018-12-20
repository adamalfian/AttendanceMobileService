package com.example.irfan.squarecamera;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.williamww.silkysignature.views.SignaturePad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignInTtdActivity extends AppCompatActivity {
    private static String TAG = SignInTtdActivity.class.getSimpleName();
    protected int counter = 0;
    private static String BASE_DIR = "camtest/";
    private String nrp = "5115100705";
    private String password = "admin123";
    private List<String> listPathFile;
    private ArrayList<String> encodedImagesList;
    protected SweetAlertDialog loadingDialog;
    protected static String UPLOAD_URL = "http://etc.if.its.ac.id/signin_TTD/";
    private int requestCounter = 0;
    private boolean hasRequestFailed = false;

    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSendButton;
    private EditText nrp_signTtd, passwrd_signTtd;
    Spinner spinner;
    private double longitude;
    private double latitude;
    String idAgenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_ttd);
        init();

        mSignaturePad = findViewById(R.id.signature_pad);
        mClearButton = findViewById(R.id.btn_clearsignttd);
        mSendButton = findViewById(R.id.btn_sendsignttd);
        nrp_signTtd = findViewById(R.id.nrp_signTtd);
        passwrd_signTtd = findViewById(R.id.passwrd_signTtd);
        spinner = findViewById(R.id.agendaSpinner);

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                Toast.makeText(SignInTtdActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mClearButton.setEnabled(true);
                mSendButton.setEnabled(true);

            }

            @Override
            public void onClear() {
                mClearButton.setEnabled(false);
                mSendButton.setEnabled(false);

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
                if (nrp_signTtd.getText().toString().isEmpty() || passwrd_signTtd.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Isi User dan Password anda!", Toast.LENGTH_SHORT).show();
                } else uploadFIle();
            }
        });
    }

    protected void init() {
        //nrp = this.getIntent().getStringExtra("nrp");
        listPathFile = new ArrayList<>();
        encodedImagesList = new ArrayList<>();
        //Dialog();
        GpsTracker gpsTracker = new GpsTracker(SignInTtdActivity.this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        }

        spinner = (Spinner) findViewById(R.id.agendaSpinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Agenda, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        //idAgenda = parent.getItemAtPosition(pos).
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Toast.makeText(getApplicationContext(),"Pilih Kelas Anda!",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SignInTtdActivity.this, volleyError.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    // Get encoded Image
//                    String image = encodedImagesList.get(index);
                    Map<String, String> params = new HashMap<>();
                    // Adding parameters

                    params.put("idUser", nrp_signTtd.getText().toString());
                    params.put("password", passwrd_signTtd.getText().toString());
                    params.put("image", "data:/image/jpeg;base64," + mSignaturePad.getSignatureBitmap());
                    params.put("Lat", String.valueOf(latitude));
                    params.put("Lon", String.valueOf(longitude));
                    idAgenda = spinner.getSelectedItem().toString();
                    params.put("idAgenda", idAgenda);
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
                        //finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                })
                .show();
    }



}
