package com.example.atoz;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atoz.Adapter.MyAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnButtonClickListener {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<String> buttonTexts;
    private TextView countdownTextView;
    private TextView statusTextView;
    private TextView timerTextView;
    private Button startButton;
    private CountDownTimer gameTimer;
    private Handler handler = new Handler();
    private boolean gameStarted = false;
    private boolean gameWon = false;
    private char currentChar = 'A';
    private int wrongClickCount = 0;
    private final int MAX_WRONG_CLICKS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        countdownTextView = findViewById(R.id.countdown_text_view);
        statusTextView = findViewById(R.id.status_text_view);
        timerTextView = findViewById(R.id.timer_text_view);
        startButton = findViewById(R.id.start_button);

        // Hide all elements except the start button
        countdownTextView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        statusTextView.setVisibility(View.INVISIBLE);
        timerTextView.setVisibility(View.INVISIBLE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5)); // 5 columns for the grid

        buttonTexts = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            buttonTexts.add(String.valueOf(c));
        }
        Collections.shuffle(buttonTexts);

        adapter = new MyAdapter(buttonTexts, this); // Pass 'this' as OnButtonClickListener
        recyclerView.setAdapter(adapter);

        startButton.setOnClickListener(v -> {
            startButton.setEnabled(false);
            resetGame();
            startCountdown();
        });
    }

    private void startCountdown() {
        countdownTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        statusTextView.setVisibility(View.INVISIBLE);
        timerTextView.setVisibility(View.INVISIBLE);

        final int[] countdown = {3};

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (countdown[0] > 0) {
                    countdownTextView.setText(String.valueOf(countdown[0]));
                    countdown[0]--;
                    handler.postDelayed(this, 1000);
                } else {
                    countdownTextView.setText("Go!");
                    handler.postDelayed(() -> {
                        countdownTextView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        statusTextView.setVisibility(View.VISIBLE);
                        timerTextView.setVisibility(View.VISIBLE);
                        enableButtons(true);
                        gameStarted = true;
                        gameWon = false;
                        startGameTimer();
                        startButton.setEnabled(true);
                    }, 1000);
                }
            }
        }, 1000);
    }

    private void startGameTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }

        gameTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setVisibility(View.VISIBLE);
                timerTextView.setText("Time left: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if (gameStarted && !gameWon) {
                    updateStatusText("LOST", Color.RED);
                    disableButtons(true);
                    gameStarted = false;
                }
            }
        }.start();
    }

    private void updateStatusText(String message, int color) {
        statusTextView.setText(message);
        statusTextView.setTextColor(color);
        statusTextView.setVisibility(View.VISIBLE);
    }

    private void resetGame() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }

        currentChar = 'A';
        wrongClickCount = 0;
        gameWon = false;

        buttonTexts = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            buttonTexts.add(String.valueOf(c));
        }
        Collections.shuffle(buttonTexts);
        adapter.updateData(buttonTexts);

        updateStatusText("", Color.BLACK);

        recyclerView.setVisibility(View.INVISIBLE);
        enableButtons(false);

        gameStarted = false;
    }

    private void enableButtons(boolean enable) {
        adapter.setButtonsEnabled(enable);
    }

    private void disableButtons(boolean disable) {
        adapter.setButtonsEnabled(!disable);
    }

    @Override
    public void onButtonClick(int position, String buttonText) {
        if (gameStarted && !gameWon) {
            if (buttonText.equals(String.valueOf(currentChar))) {
                buttonTexts.set(position, ""); // Hide text in the list
                adapter.updateData(buttonTexts); // Notify adapter of the data change
                currentChar++;
                if (currentChar > 'Z') {
                    updateStatusText("YOU WIN!", Color.GREEN);
                    disableButtons(true);
                    gameWon = true;
                    gameTimer.cancel(); // Stop the timer
                }
            } else {
                wrongClickCount++;
                if (wrongClickCount >= MAX_WRONG_CLICKS) {
                    updateStatusText("LOST", Color.RED);
                    disableButtons(true);
                    gameStarted = false;
                    gameWon = false;
                    gameTimer.cancel(); // Stop the timer
                }
            }
        }
    }
}
