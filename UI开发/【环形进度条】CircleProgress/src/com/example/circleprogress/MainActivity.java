package com.example.circleprogress;

import com.example.circleprogress.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private CircleProgress circleBar;
	private EditText setnum;
	private Button setnumbutton;
	int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		circleBar = (CircleProgress) findViewById(R.id.circle);
		setnum = (EditText) findViewById(R.id.setnum);
		setnumbutton = (Button) findViewById(R.id.setnumbutton);
		circleBar.setMaxProgress(100);
		setnumbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				circleBar.updateProgress(Integer.parseInt(setnum.getText().toString()),700);
			}
		});

	}

}
