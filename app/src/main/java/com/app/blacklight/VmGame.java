package com.app.blacklight;

import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VmGame extends ViewModel {
    private final MutableLiveData<Integer> score = new MutableLiveData<Integer>(0);
    private final MutableLiveData<Integer> time = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isClicked = new MutableLiveData<>(true);
    private CountDownTimer timer;

    public LiveData<Integer> getScore() {
        return score;
    }

    public LiveData<Integer> getTime() {
        return time;
    }

    public LiveData<Boolean> getIsClicked() {
        return isClicked;
    }

    public void addScore(int val) {
        Integer currentScore = score.getValue();
        if (currentScore == null) currentScore = 0;
        score.setValue(currentScore + val);
        isClicked.setValue(true);
    }

    public void setTime(int val) {

    }

    public void resetScore() {
        score.setValue(0);
    }

    public void resetTime() {
        time.setValue(0);
    }

    void startTimer() {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long l) {
                Integer currentTime = time.getValue();
                if (currentTime == null) currentTime = 0;
                time.setValue(currentTime + 1);

                Boolean clicked = isClicked.getValue();

                if (clicked != null || !clicked) score.setValue(0);
                isClicked.setValue(false);
            }

            @Override
            public void onFinish() {
            }

        };
        timer.start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (timer != null) timer.cancel();
    }
}
