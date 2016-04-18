package com.android.lovesixgod.myrefreshlistview;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private RefreshFirstView firstView;
    private Button play;
    private RefreshSecondView secondAnim;
    private RefreshThirdView thirdAnim;
    private Button toList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        firstView = (RefreshFirstView) findViewById(R.id.first_view);
        play = (Button) findViewById(R.id.play_animation);
        toList = (Button) findViewById(R.id.go_to_list);
        secondAnim = (RefreshSecondView) findViewById(R.id.second_animation);
        thirdAnim = (RefreshThirdView) findViewById(R.id.third_animation);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float currentProgress = (float) progress / (float) seekBar.getMax();
                firstView.setCurrentProgress(currentProgress);
                firstView.postInvalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationDrawable second = (AnimationDrawable) secondAnim.getBackground();
                second.start();
                AnimationDrawable third = (AnimationDrawable) thirdAnim.getBackground();
                third.start();
            }
        });

        toList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
    }
}
