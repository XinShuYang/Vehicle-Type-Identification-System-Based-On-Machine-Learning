package com.example.lalala;

import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class CameraTestActivity extends Activity {

	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/ASoohue/CameraCache");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_image);

		Button bt = (Button) findViewById(R.id.button1);
		bt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(intent);

			}
		});
		
		makefile3();

	}

	private void makefile3() {
		if (!PHOTO_DIR.exists()) {
			boolean isCreat2 = PHOTO_DIR.mkdirs();
			Log.e("YAO3333", "111" + isCreat2);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}