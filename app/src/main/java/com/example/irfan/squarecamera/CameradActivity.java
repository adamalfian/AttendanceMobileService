package com.example.irfan.squarecamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class CameradActivity extends AppCompatActivity {

    private static String TAG = CameradActivity.class.getSimpleName();
    private CameraView camerad;
    private CameraKitEventListener cameradListener;
    private Button btnCapture, btn_table;
    private TextView tvHint;
    protected boolean pakaiKacamata;
    protected int counter = 0;
    private static String BASE_DIR = "camtest/";
    private String hint[];
    private String nrp = "5115100705";
    private String password = "admin123";
    private List<String> listPathFile;
    private ArrayList<String> encodedImagesList;
    protected SweetAlertDialog loadingDialog, errorDialog, successDialog;
    protected static String UPLOAD_URL = "http://etc.if.its.ac.id/sendImg/";
    private int requestCounter = 0;
    private boolean hasRequestFailed = false;
    private DBHelper dbHelper;

    private String hintTidakPakaiKacamata[] = new String[]{
            "1. Normal tegak lurus kamera.",
            "2. Normal tegak lurus kamera berkacamata.",
            "3. Tersenyum tegak lurus kamera.",
            "4. Sedih tegak lurus kamera.",
            "5. Mengantuk tegak lurus kamera.",
            "6. Normal menoleh ke kanan 30 derajat.",
            "7. Normal menoleh ke kanan 30 derajat beracamata.",
            "8. Tersenyum menoleh ke kanan 30 derajat.",
            "9. Sedih menoleh ke kanan 30 derajat.",
            "10. Mengantuk menoleh ke kanan 30 derajat.",
            "11. Normal menoleh ke kiri 30 derajat.",
            "12. Normal menoleh ke kiri 30 derajat berkacamata.",
            "13. Tersenyum menoleh ke kiri 30 derajat.",
            "14. Sedih menoleh ke kiri 30 derajat.",
            "15. Mengantuk menoleh ke kiri 30 derajat.",
            "16. Normal tegak lurus kamera muka basah.",
            "17. Normal tegak lurus kamera berkacamata muka basah.",
            "18. Tersenyum tegak lurus kamera muka basah.",
            "19. Sedih tegak lurus kamera muka basah.",
            "20. Mengantuk tegak lurus kamera muka basah.",
            "21. Normal menoleh ke kanan 30 derajat muka basah.",
            "22. Normal menoleh ke kanan 30 derajat berkacamata muka basah.",
            "23. Tersenyum menoleh ke kanan 30 derajat muka basah.",
            "24. Sedih menoleh ke kanan 30 derajat muka basah",
            "25. Mengantuk menoleh ke kanan 30 derajat muka basah",
            "26. Normal menoleh ke kiri 30 derajat muka basah",
            "27. Normal menoleh ke kiri 30 derajat berkacamata muka basah.",
            "28. Tersenyum menoleh ke kiri 30 derajat muka basah.",
            "29. Sedih menoleh ke kiri 30 derajat muka basah.",
            "30. Mengantuk menoleh ke kiri 30 derajat muka basah."
    };

    private String hintPakaiKacamata[] = new String[]{
            "1. Normal tegak lurus kamera.",
            "2. Normal tegak tegak lurus kamera tidak berkacamata.",
            "3. Tersenyum tegak lurus kamera.",
            "4. Sedih tegak lurus kamera.",
            "5. Mengantuk tegak lurus kamera.",
            "6. Normal menoleh ke kanan 30 derajat.",
            "7. Normal menoleh ke kanan 30 derajat tidak berkacamata.",
            "8. Tersenyum menoleh ke kanan 30 derajat.",
            "9. Sedih menoleh ke kanan 30 derajat.",
            "10. Mengantuk menoleh ke kanan 30 derajat.",
            "11. Normal menoleh ke kiri 30 derajat.",
            "12. Normal menoleh ke kiri 30 derajat tidak berkacamata.",
            "13. Tersenyum menoleh ke kiri 30 derajat.",
            "14. Sedih menoleh ke kiri 30 derajat.",
            "15. Mengantuk menoleh ke kiri 30 derajat.",
            "16. Normal tegak lurus kamera muka basah.",
            "17. Normal tegak lurus kamera tidak berkacamata muka basah.",
            "18. Tersenyum tegak lurus kamera muka basah.",
            "19. Sedih tegak lurus kamera muka basah.",
            "20. Mengantuk tegak lurus kamera muka basah.",
            "21. Normal menoleh ke kanan 30 derajat muka basah.",
            "22. Normal menoleh ke kanan 30 derajat tidak berkacamata muka basah.",
            "23. Tersenyum menoleh ke kanan 30 derajat muka basah.",
            "24. Sedih menoleh ke kanan 30 derajat muka basah.",
            "25. Mengantuk menoleh ke kanan 30 derajat muka basah.",
            "26. Normal menoleh ke kiri 30 derajat muka basah.",
            "27. Normal menoleh ke kiri 30 derajat tidak berkacamata muka basah.",
            "28. Tersenyum menoleh ke kiri 30 derajat muka basah.",
            "29. Sedih menoleh ke kiri 30 derajat muka basah.",
            "30. Mengantuk menoleh ke kiri 30 derajat muka basah."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerad);
        dbHelper = new DBHelper(this);
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
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

                if (pictureFile == null) {
                    Log.d(TAG, "Error creating media file, check storage permissions.");
                    return;
                }

                try {
                    FileOutputStream outStream = new FileOutputStream(pictureFile);
                    byte[] picture = cameraKitImage.getJpeg();
                    Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                    result = Bitmap.createScaledBitmap(result, 512,512, true);
                    result.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();


                    counter++;
//                    showHintDialog();
//                    getEncodedImage();
//                    uploadFIle();
//                    showLoadingDialog();
//                    showSuccessDialog();
                    if (counter == 1) {
                        long startTime = System.nanoTime();
                        showLoadingDialog();
//                        closeLoadingDialog();
//                        showSuccessDialog();
                        if (getEncodedImage()){
                            uploadFIle();
                            long endTime = System.nanoTime();
                            long totalTime= endTime - startTime;
                            double time = totalTime/1000000000.0;
                            Toast.makeText(CameradActivity.this, "waktu upload: " + (float) time, Toast.LENGTH_LONG).show();
                            String total = String.valueOf(totalTime);
                            String start = String.valueOf(startTime/1000000000.0);
                            String end = String.valueOf(endTime/1000000000.0);

                            Calculate calculate = new Calculate(String.valueOf(getIntent().getIntExtra("counterCount",0)),
                                    start,end,total);

                            dbHelper.addData(calculate);
                        }
                    }
                    else {
                        uploadFIle();

                    }

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

        btnCapture = (Button) findViewById(R.id.btn_capture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camerad.captureImage();
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
        hint = pakaiKacamata ? hintPakaiKacamata : hintTidakPakaiKacamata;
        //showHintDialog();

    }

    protected File getOutputMediaFile(int type) {
        //external storage
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), BASE_DIR + nrp);

        //internal storage
        File folder= getFilesDir();
        File mediaStorageDir = new File(folder, BASE_DIR);

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "sikemas: failed to create directory");
                return null;
            }
        }

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
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
                            requestCounter--;

                            if (requestCounter == 0 && !hasRequestFailed) {
                                closeLoadingDialog();
                                showSuccessDialog();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            hasRequestFailed = true;
                            //Showing toast
                            Toast.makeText(CameradActivity.this, volleyError.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    // Get encoded Image
                    String image = encodedImagesList.get(index);
                    Map<String, String> params = new HashMap<>();
                    // Adding parameters

                    params.put("idUser", nrp );
                    params.put("password", password );
                    params.put("image","data:/image/jpeg;base64," + image+".png");

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
        int hintNumber= getIntent().getIntExtra("counterCount",0);
        //tvHint.setText(hint[counter]);
        new SweetAlertDialog(this)
                .setTitleText("Hint:")
                .setContentText(hint[hintNumber])
//                .show()
;
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
                        //closeLoadingDialog();
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                })
                .show();
    }

}