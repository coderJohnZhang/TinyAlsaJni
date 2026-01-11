package com.johnny.tinyalsa;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TinyAlsaManager alsaManager;
    private ProgressBar progressBar;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化UI
        Button btnDmicRecord = findViewById(R.id.btn_dmic_record);
        Button btnDmicPlay = findViewById(R.id.btn_dmic_play);
        Button btnLineinRecord = findViewById(R.id.btn_linein_record);
        Button btnLineinPlay = findViewById(R.id.btn_linein_play);
        progressBar = findViewById(R.id.progress_bar);
        statusText = findViewById(R.id.status_text);

        // 初始化音频管理器
        alsaManager = new TinyAlsaManager();
        alsaManager.setAudioTestCallback(new TinyAlsaManager.AudioTestCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTestProgress(int progress) {
                runOnUiThread(() -> {
                    progressBar.setProgress(progress);
                    statusText.setText("进度: " + progress + "%");
                });
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTestComplete(boolean success, String message) {
                runOnUiThread(() -> {
                    if (success) {
                        statusText.setText("测试完成: " + message);
                        Toast.makeText(MainActivity.this, "测试成功", Toast.LENGTH_SHORT).show();
                    } else {
                        statusText.setText("测试失败: " + message);
                        Toast.makeText(MainActivity.this, "测试失败: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 设置按钮点击事件
        btnDmicRecord.setOnClickListener(v -> startDmicRecord());
        btnDmicPlay.setOnClickListener(v -> playDmicRecording());
        btnLineinRecord.setOnClickListener(v -> startLineinRecord());
        btnLineinPlay.setOnClickListener(v -> playLineinRecording());
    }

    private void startDmicRecord() {
        new Thread(() -> {
            String filePath = "/storage/emulated/0/Music/dmic.wav";
            int result = alsaManager.dmicRecord(filePath, 15); // 录音15秒
            runOnUiThread(() -> {
                if (result == 0) {
                    Toast.makeText(this, "DMIC录音成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "DMIC录音失败: " + alsaManager.getError(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void playDmicRecording() {
        new Thread(() -> {
            String filePath = "/storage/emulated/0/Music/dmic.wav";
            int result = alsaManager.dmicPlayback(filePath);
            runOnUiThread(() -> {
                if (result == 0) {
                    Toast.makeText(this, "DMIC播放成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "DMIC播放失败: " + alsaManager.getError(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void startLineinRecord() {
        new Thread(() -> {
            String filePath = "/storage/emulated/0/Music/amic.wav";
            int result = alsaManager.lineinRecord(filePath, 15); // 录音15秒
            runOnUiThread(() -> {
                if (result == 0) {
                    Toast.makeText(this, "LINEIN录音成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "LINEIN录音失败: " + alsaManager.getError(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void playLineinRecording() {
        new Thread(() -> {
            String filePath = "/storage/emulated/0/Music/amic.wav";
            int result = alsaManager.lineinPlayback(filePath);
            runOnUiThread(() -> {
                if (result == 0) {
                    Toast.makeText(this, "LINEIN播放成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "LINEIN播放失败: " + alsaManager.getError(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alsaManager != null) {
            alsaManager.setAudioTestCallback(null);
        }
    }
}