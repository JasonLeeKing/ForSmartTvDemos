package com.ktc.waveloadingview;

import me.itangqi.waveloadingview.WaveLoadingView;
import me.itangqi.waveloadingview.WaveLoadingView.ShapeType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ktc.waveloadingview.R;

public class MainActivity extends Activity {

    private WaveLoadingView mWaveLoadingView;
    private int checkedItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);

        // Shape Type
        findViewById(R.id.tv_shape).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(MainActivity.this).setTitle("Shape Type").setSingleChoiceItems(
                        new String[] { "CIRCLE", "TRIANGLE", "SQUARE", "RECTANGLE" }, checkedItem,
              new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem = which;
                        switch (which) {
                            case 0:
                                mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
                                dialog.dismiss();
                                break;
                            case 1:
                                mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.TRIANGLE);
                                dialog.dismiss();
                                break;
                            case 2:
                                mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.SQUARE);
                                dialog.dismiss();
                                break;
                            case 3:
                                mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.RECTANGLE);
                                dialog.dismiss();
                                break;
                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
              }).show();
            }
        });

        // Center Title
        ((CheckBox) findViewById(R.id.cb_title_center)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mWaveLoadingView.setCenterTitle("Center Title");
                } else {
                    mWaveLoadingView.setCenterTitle("");
                }
            }
        });

        // Progress
        ((SeekBar) findViewById(R.id.seekbar_progress)).setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mWaveLoadingView.setProgressValue(progress);
			}
		});
		
        // Border
        ((SeekBar) findViewById(R.id.seekbar_border_width)).setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mWaveLoadingView.setBorderWidth(progress);
			}
		});

        // Amplitude
        ((SeekBar) findViewById(R.id.seek_bar_amplitude)).setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mWaveLoadingView.setAmplitudeRatio(progress);
			}
		});

        // Wave Color
        /*((LobsterShadeSlider) findViewById(R.id.shadeslider_wave_color)).addOnColorListener(new OnColorListener() {
            @Override
            public void onColorChanged(@ColorInt int color) {
                mWaveLoadingView.setWaveColor(color);
            }

            @Override
            public void onColorSelected(@ColorInt int color) {
            }
        });

        // Border Color
        ((LobsterShadeSlider) findViewById(R.id.shadeslider_border_color)).addOnColorListener(new OnColorListener() {
            @Override
            public void onColorChanged(@ColorInt int color) {
                mWaveLoadingView.setBorderColor(color);
            }

            @Override
            public void onColorSelected(@ColorInt int color) {
            }
        });*/
    }

}
