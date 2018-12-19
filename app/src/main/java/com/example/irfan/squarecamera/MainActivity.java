package com.example.irfan.squarecamera;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    public int counterCount=0;
    protected static String TAG = MainActivity.class.getSimpleName();
    protected LinearLayout btnCamerad, btnRecycleView, btnMenuPredict, btnScan, btnTrain, btnSignIn, btnDatasetTtd, btnTrainTTd, btnPredictTtd, btnSignInTtd;
    TextView hint;

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
        setContentView(R.layout.activity_main);

        requestReadStoragePermission();
        requestWriteStoragePermission();

        btnCamerad = findViewById(R.id.btnMenuDataset);
        hint=findViewById(R.id.hintmain);
        hint.setText(hintPakaiKacamata[counterCount]);
        btnTrain = findViewById(R.id.btnTrain);
        btnMenuPredict = findViewById(R.id.btnMenuPredict);
        btnRecycleView = findViewById(R.id.btnRecycleView);
        btnScan = findViewById(R.id.btnScan);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnDatasetTtd = findViewById(R.id.btndatasetTtd);
        btnTrainTTd = findViewById(R.id.btnTrainTtd);
        btnPredictTtd = findViewById(R.id.btnPredictTtd);
        btnSignInTtd = findViewById(R.id.btnSignInTtd);

        btnCamerad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameradActivity.class);
                intent.putExtra("counterCount",counterCount);
                startActivityForResult(intent,250);
            }
        });

        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TrainActivity.class);
                startActivity(intent);
            }
        });

        btnMenuPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PredictActivity.class);
                startActivity(intent);

            }
        });

        btnRecycleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RecycleView.class));
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),QrCode.class);
                intent.setAction("com.google.zxing.client.android.SCAN");
                intent.putExtra("SAVE_HISTORY", false);
                startActivityForResult(intent, 0);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        
        btnDatasetTtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TtdActivity.class);
                startActivity(intent);
            }
        });

        btnTrainTTd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PredictTtdActivity.class));
            }
        });

        btnPredictTtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PredictTtdActivity.class));
            }
        });

        btnSignInTtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignInTtdActivity.class));
            }
        });


    }

    public  void requestReadStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            }
        }
    }

    public  void requestWriteStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        counterCount++;
        hint.setText(hintPakaiKacamata[counterCount]);
    }
}