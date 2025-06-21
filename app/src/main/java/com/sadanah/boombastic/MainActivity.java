package com.sadanah.boombastic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;

    private RecyclerView musicList;
    private ImageButton btnPlayPause, btnNext, btnPrevious;

    private List<File> mp3Files;
    private MediaPlayer mediaPlayer;
    private int currentTrackIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        musicList = findViewById(R.id.musicList);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);

        musicList.setLayoutManager(new LinearLayoutManager(this));

        if (checkPermission()) {
            loadMusic();
        } else {
            requestAllFilesAccessPermission();
        }

        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnNext.setOnClickListener(v -> playNext());
        btnPrevious.setOnClickListener(v -> playPrevious());
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    private void loadMusic() {
        File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        mp3Files = getMp3Files(musicDir);

        if (mp3Files.isEmpty()) {
            Toast.makeText(this, "No MP3 files found", Toast.LENGTH_SHORT).show();
            return;
        }

        MusicAdapter adapter = new MusicAdapter(this, mp3Files, file -> {
            currentTrackIndex = mp3Files.indexOf(file);
            playSelectedMusic(file);
        });

        musicList.setAdapter(adapter);
    }

    private List<File> getMp3Files(File directory) {
        List<File> fileList = new ArrayList<>();
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".mp3")) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    private void playSelectedMusic(File file) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, android.net.Uri.fromFile(file));
        mediaPlayer.start();
        btnPlayPause.setImageResource(R.drawable.ic_stop);
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlayPause.setImageResource(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                btnPlayPause.setImageResource(R.drawable.ic_stop);
            }
        }
    }

    private void playNext() {
        if (mp3Files != null && !mp3Files.isEmpty()) {
            currentTrackIndex = (currentTrackIndex + 1) % mp3Files.size();
            playSelectedMusic(mp3Files.get(currentTrackIndex));
        }
    }

    private void playPrevious() {
        if (mp3Files != null && !mp3Files.isEmpty()) {
            currentTrackIndex = (currentTrackIndex - 1 + mp3Files.size()) % mp3Files.size();
            playSelectedMusic(mp3Files.get(currentTrackIndex));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMusic();
            } else {
                Toast.makeText(this, "Permission denied to read music files", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestAllFilesAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

}
