package com.github.colorpickerdemo;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import com.github.colorpickerdemo.listener.OnSeekColorListener;
import com.github.colorpickerdemo.view.ColorPicker;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, OnSeekColorListener {

    /**
     * 颜色值   饱和度    值
     */
    private SeekBar mSbColor, mSbSat, mSbValue;

    private ColorPicker mColorPicker;

    private NestedScrollView  mNestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        click();
    }

    private void init() {
        mSbColor = (SeekBar) findViewById(R.id.sb_color);
        mSbSat = (SeekBar) findViewById(R.id.sb_sat);
        mSbValue = (SeekBar) findViewById(R.id.sb_value);
        mColorPicker = (ColorPicker) findViewById(R.id.cp);
        mNestedScrollView= (NestedScrollView) findViewById(R.id.nest_scroll);
    }

    private void click() {
        mSbColor.setOnSeekBarChangeListener(this);
        mSbSat.setOnSeekBarChangeListener(this);
        mSbValue.setOnSeekBarChangeListener(this);
        mColorPicker.setOnSeekColorListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sb_color:
                mColorPicker.setHSVColor(i);
                break;
            case R.id.sb_sat:
                mColorPicker.setHSVSaturation((float) i / 100);
                break;
            case R.id.sb_value:
                mColorPicker.setHSVValue((float) i / 100);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onSeekColorListener(int color) {
        mNestedScrollView.setBackgroundColor(color);
    }
}
