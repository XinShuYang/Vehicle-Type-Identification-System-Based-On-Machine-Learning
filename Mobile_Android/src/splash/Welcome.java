package splash;
import java.util.Timer;
import java.util.TimerTask;

import com.example.lalala.MainActivity;
import com.example.lalala.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Welcome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //ͨ��һ��ʱ����ƺ���Timer����ʵ��һ�������һ�������ת��
    	new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				startActivity(new Intent(Welcome.this,MainActivity.class));
				finish();
				
			}
		}, 3000);//����ͣ��ʱ��Ϊ1000=1s��
	}

}
