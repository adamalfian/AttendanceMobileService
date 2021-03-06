package com.example.irfan.squarecamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class SignInActivity extends AppCompatActivity {
    long start;
    long end;
    private double longitude;
    private double latitude;
    Location location;
    String message, idAgenda;
    long probability;
    String validation;
    private static String TAG = CameradActivity.class.getSimpleName();
    private CameraView camerad;
    private CameraKitEventListener cameradListener;
    private Button btnCapture;
    private TextView tvHint;
    private TextView tvValidasi;
    protected boolean pakaiKacamata;
    protected int counter = 0;
    private static String BASE_DIR = "camtest/";
    private String hint[];
    private String nrp = "5115100705";
    private String password = "admin123";
    private List<String> listPathFile;
    private ArrayList<String> encodedImagesList;
    protected SweetAlertDialog loadingDialog, errorDialog, successDialog;
    protected static String UPLOAD_URL = "http://etc.if.its.ac.id/signin/";
    private int requestCounter = 0;
    private boolean hasRequestFailed = false;
    Spinner spinner;
    EditText nrp_sign, passwrd_sign;
    String result = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();

        cameradListener = new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                start = System.nanoTime();
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                //File pictureFile = getFilesDir();

                if (pictureFile == null) {
                    Log.d(TAG, "Error creating media file, check storage permissions.");
                    return;
                }

                try {
                    FileOutputStream outStream = new FileOutputStream(pictureFile);
                    byte[] picture = cameraKitImage.getJpeg();
                    Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                    result = Bitmap.createScaledBitmap(result, 512, 512, true);
                    result.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();

                    counter++;

                    if (counter == 1) {
                        showLoadingDialog();
//                        closeLoadingDialog();
//                        showSuccessDialog();
                        if (getEncodedImage()) {
                            uploadFIle();
                        }
                    } else uploadFIle();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        };

        camerad = (CameraView) findViewById(R.id.camerad);
        camerad.addCameraKitListener(cameradListener);
        tvValidasi = (TextView) findViewById(R.id.tvValidasi);
        btnCapture = (Button) findViewById(R.id.btn_capture);
        nrp_sign = findViewById(R.id.nrp_sign);
        passwrd_sign = findViewById(R.id.passwrd_sign);
        spinner = (Spinner) findViewById(R.id.agendaSpinner);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nrp_sign.getText().toString().isEmpty() || passwrd_sign.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "User atau Password anda belum di isi Sayang <3", Toast.LENGTH_SHORT).show();
                }
                else camerad.captureImage();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        camerad.start();
    }

    @Override
    protected void onPause() {
        camerad.stop();
        super.onPause();
    }

    protected void init() {
        //nrp = this.getIntent().getStringExtra("nrp");
        listPathFile = new ArrayList<>();
        encodedImagesList = new ArrayList<>();
        tvHint = (TextView) findViewById(R.id.tvHint);

        pakaiKacamata = this.getIntent().getBooleanExtra("pakaiKacamata", false);
        //Dialog();
        GpsTracker gpsTracker = new GpsTracker(SignInActivity.this);
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

    protected File getOutputMediaFile(int type) {
        File folder = getFilesDir();
        File mediaStorageDir = new File(folder, BASE_DIR);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "sikemas: failed to create directory");
                return null;
            }
        }

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            String filepath = mediaStorageDir.getPath() + File.separator + (pakaiKacamata ? "pakai_kacamata_" : "tidak_pakai_kacamata_") +
                    nrp + "_" + counter + ".png";
            listPathFile.add(filepath);
            mediaFile = new File(filepath);
        } else {
            return null;
        }

        return mediaFile;
    }

    protected boolean getEncodedImage() {
        loadingDialog.setTitleText("Encoding images");

        Bitmap image;
        ByteArrayOutputStream baos;
        byte[] byteArrayImage;
        String image_base64;

        for (String imagepath : listPathFile) {
            image = BitmapFactory.decodeFile(imagepath);
            baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byteArrayImage = baos.toByteArray();
            image_base64 = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            encodedImagesList.add(image_base64);
        }

        return true;
    }

    protected void uploadFIle() {
        loadingDialog.setTitleText("Uploading images");
        StringRequest stringRequest;
        for (int i = 0; i < encodedImagesList.size(); i++) {
            final int index = i;
            stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(), "the response : " + response, Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onResponse: " + response);
                            result = response;
//                            JSONObject obj = new JSONObject();
//                            message = obj.optString("message");
//                            probability = obj.optLong("probability");
//                            validation = obj.optString("validation");
//                            switch (message) {
//                                case "ok":
//                                    tvValidasi.setText("Hasil Prediksi : " + validation + "\ndengan Probability : " + probability);
//                                default:
//                                    tvValidasi.setText("GAGAL PREDIKSI");
//                            }
                            requestCounter--;
                            if (requestCounter == 0 && !hasRequestFailed) {
                                //closeLoadingDialog();
                                showSuccessDialog();
//                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
    //                            finish();
  //                              startActivity(intent);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            hasRequestFailed = true;
                            //Showing toast
                            Toast.makeText(SignInActivity.this, volleyError.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    // Get encoded Image
                    String image = encodedImagesList.get(index);
                    Map<String, String> params = new HashMap<>();

                    // Adding parameters
                    params.put("idUser", nrp_sign.getText().toString());
                    params.put("password", passwrd_sign.getText().toString());
                    params.put("image", "data:/image/jpeg;base64," + image+".png");
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

    protected void showHintDialog() {
        int hintNumber = getIntent().getIntExtra("counterCount", 0);
        tvHint.setText(hint[hintNumber]);
        new SweetAlertDialog(this)
                .setTitleText("Hint:")
                .setContentText(hint[hintNumber]);
        //.show();
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
                .setTitleText("Your Picture Uploaded with Result :!")
                .setContentText(result)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        setResult(Activity.RESULT_OK);
                        startActivity(new Intent(getApplicationContext(), SignInTtdActivity.class));
                    }
                })
                .show();
    }

}