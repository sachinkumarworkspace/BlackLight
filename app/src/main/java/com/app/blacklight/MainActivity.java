package com.app.blacklight;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.app.blacklight.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding bin;
    private VmGame vmGame;
    private Animation anim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        bin = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bin.getRoot());
        anim = AnimationUtils.loadAnimation(this, R.anim.game_over);
        init();
    }

    @SuppressLint("SetTextI18n")
    void setScoreUpdates() {
        vmGame.getScore().observe(this, score -> {
            if (score == null) bin.score.setText(0);
            bin.score.setText("Score: " + score);
        });
    }

    void setTimerUpdates() {
        vmGame.getTime().observe(this, time -> {
            Boolean isClicked = vmGame.getIsClicked().getValue();
            if (time == null) bin.time.setText(0);
            bin.time.setText("Time: " + time + "\t isClicked: " + isClicked);


        });

        vmGame.getIsClicked().observe(this, isClicked -> {
            if (isClicked != null) {
                if (!isClicked) {
                    bin.gameStatus.setText("Game Over as not clicked!");
                    bin.textGameStatus.startAnimation(anim);
                }
            }
        });
    }

    void init() {
        vmGame = new ViewModelProvider(this).get(VmGame.class);

        enableVisibility();

        bin.btnRestart.setOnClickListener(v -> {
            vmGame.resetScore();
            vmGame.resetTime();
            startGame();
        });

    }

    private void enableVisibility() {
        bin.btnStart.setOnClickListener(v->{
            bin.btnStart.setVisibility(View.GONE);
            bin.llGame.setVisibility(View.VISIBLE);
            startGame();
        });
    }

    void startGame() {
        vmGame.startTimer();
        setTimerUpdates();
        setScoreUpdates();
        play();
    }

    void play() {
        bin.v1.setOnClickListener(v -> {
            vmGame.addScore(1);
        });
        bin.v2.setOnClickListener(v -> {
            vmGame.addScore(1);
        });
        bin.v3.setOnClickListener(v -> {
            vmGame.addScore(1);
        });
        bin.v4.setOnClickListener(v -> {
            vmGame.addScore(1);
        });
    }

}