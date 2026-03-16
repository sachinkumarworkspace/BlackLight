package com.app.blacklight;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.app.blacklight.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding bin;
    private VmGame vmGame;
    private Animation anim;

    private int previousIndex = -1;
    private int currentGrayIndex = -1;

    // Original colors
    private final int[] originalColors = {
            Color.parseColor("#ff6e00"),
            Color.parseColor("#0096ff"),
            Color.parseColor("#ffbf00"),
            Color.parseColor("#93c572")
    };

    private final Handler clickHandler = new Handler();
    private Runnable clickTimeoutRunnable;
    private boolean tappedThisTick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        bin = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bin.getRoot());

        anim = AnimationUtils.loadAnimation(this, R.anim.game_over);

        init();
    }

    private void init() {
        vmGame = new ViewModelProvider(this).get(VmGame.class);

        observeRandomNumber();
        observeScore();
        observerClicks();
        initControls();

        bin.btnStart.setOnClickListener(v -> {
            bin.btnStart.setVisibility(View.GONE);
            bin.llGame.setVisibility(View.VISIBLE);
            startGame();
        });
    }

    private void startGame() {
        resetCardColors();
        previousIndex = -1;
        currentGrayIndex = -1;
        vmGame.resetGame();

        bin.score.setText("Score: 0");
        bin.textGameFinalScore.setText("Your score is: 0");

        vmGame.startGame();
    }

    private void observeRandomNumber() {
        final View[] cards = {bin.v1, bin.v2, bin.v3, bin.v4};

        vmGame.getRandomNumber().observe(this, index -> {
            if (index == null || index < 0) return;

            if (previousIndex != -1) {
                cards[previousIndex].setBackgroundColor(originalColors[previousIndex]);
            }

            cards[index].setBackgroundColor(Color.GRAY);

            if (clickTimeoutRunnable != null) {
                clickHandler.removeCallbacks(clickTimeoutRunnable);
            }

            tappedThisTick = false;

            clickTimeoutRunnable = () -> {
                if (!tappedThisTick && currentGrayIndex == index) {
                    stopGame();
                }
            };
            clickHandler.postDelayed(clickTimeoutRunnable, 1000);

            // Update trackers
            previousIndex = index;
            currentGrayIndex = index;
        });
    }

    private void observeScore() {
        vmGame.getScore().observe(this, s ->
        {
            if (s == null) s = 0;

            bin.score.setText("Score: " + s);
            bin.textGameFinalScore.setText("Your score is: " + s);
        });
    }

    private void observerClicks() {
        View.OnClickListener listener = v -> {
            if (!vmGame.isRunning()) return; // <-- ignore clicks if game over

            int clickedIndex = -1;
            if (v == bin.v1) clickedIndex = 0;
            else if (v == bin.v2) clickedIndex = 1;
            else if (v == bin.v3) clickedIndex = 2;
            else if (v == bin.v4) clickedIndex = 3;

            if (clickedIndex == currentGrayIndex) {
                vmGame.addScore(1);
                tappedThisTick = true; // mark tapped
            } else {
                stopGame();
            }
        };

        bin.v1.setOnClickListener(listener);
        bin.v2.setOnClickListener(listener);
        bin.v3.setOnClickListener(listener);
        bin.v4.setOnClickListener(listener);
    }

    private void stopGame() {
        vmGame.stopGame();
        if (clickTimeoutRunnable != null) clickHandler.removeCallbacks(clickTimeoutRunnable);

        bin.llGame.setVisibility(View.GONE);
        bin.rlGameStatus.setVisibility(View.VISIBLE);
        bin.textGameStatus.setText("Game Over!");
        bin.textGameStatus.startAnimation(anim);
    }

    private void initControls() {
        bin.btnRestart.setOnClickListener(v -> {
            bin.rlGameStatus.setVisibility(View.GONE);
            bin.llGame.setVisibility(View.VISIBLE);
            resetCardColors();
            previousIndex = -1;
            currentGrayIndex = -1;
            vmGame.resetGame();
            vmGame.startGame();
        });
    }

    private void resetCardColors() {
        bin.v1.setBackgroundColor(originalColors[0]);
        bin.v2.setBackgroundColor(originalColors[1]);
        bin.v3.setBackgroundColor(originalColors[2]);
        bin.v4.setBackgroundColor(originalColors[3]);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        vmGame.stopGame();
        if (clickTimeoutRunnable != null) clickHandler.removeCallbacks(clickTimeoutRunnable);
    }
}