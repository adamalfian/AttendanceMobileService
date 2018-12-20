package com.example.irfan.squarecamera;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TrainActivity extends AppCompatActivity {

    private Button btnTrainImage;
    private EditText nrp_train, passwrd_train;
    private String predictionResult = "none";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        btnTrainImage = findViewById(R.id.btnTrainImage);
        nrp_train = findViewById(R.id.et_nrp_train);
        passwrd_train = findViewById(R.id.et_passwrd_train);

        btnTrainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nrp_train.getText().toString().isEmpty() || passwrd_train.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Isi User dan Password anda!", Toast.LENGTH_SHORT).show();
                }
                else training();
            }
        });
    }

    protected void training() {
        StringRequest stringRequest;
        for (int i = 0; i<1; i++){
            stringRequest = new StringRequest(Request.Method.POST, "http://etc.if.its.ac.id/doTrain/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(), "the response: " + response, Toast.LENGTH_LONG).show();
                            Log.d("TRAIN", "onResponse: " + response);
                            predictionResult = response;

                            showSuccessDialog();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(TrainActivity.this, error.getMessage(),
                                    Toast.LENGTH_LONG).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    // Adding parameters
                    params.put("idUser", nrp_train.getText().toString());
                    params.put("password", passwrd_train.getText().toString());
                    //returning parameters
                    return params;
                }
            };
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    protected void showSuccessDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success! Hasil Training")
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
