package com.app.blacklight;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VmGame extends ViewModel {

    private final MutableLiveData<Integer> randomNumber = new MutableLiveData<>();
    private final MutableLiveData<Integer> score = new MutableLiveData<>(0);

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running = false;

    private final Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            Integer prev = randomNumber.getValue();
            int next;
            do {
                next = (int) (Math.random() * 4);
            } while (prev != null && next == prev);

            randomNumber.setValue(next);
            handler.postDelayed(this, 1000);
        }
    };

    public LiveData<Integer> getRandomNumber() {
        return randomNumber;
    }

    public LiveData<Integer> getScore() {
        return score;
    }

    public void addScore(int value) {
        Integer s = score.getValue();
        if (s == null) s = 0;
        score.setValue(s + value);
    }

    public void startGame() {
        if (running) return;
        running = true;
        handler.post(gameLoop);
    }

    public boolean isRunning() {
        return running;
    }

    public void stopGame() {
        running = false;
        handler.removeCallbacks(gameLoop);
    }

    public void resetGame() {
        stopGame();
        score.setValue(0);        // reset score
        randomNumber.setValue(-1); // reset gray box
    }

    @Override
    protected void onCleared() {
        handler.removeCallbacks(gameLoop);
    }
}