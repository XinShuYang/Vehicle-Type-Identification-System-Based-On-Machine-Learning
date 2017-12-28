package com.example.lalala;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.Resources;

public class MainActivity extends Activity implements OnClickListener {
	
	
	private static final String TAG = "TestPhotoActivity";
	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3023;
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	/* 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/ASoohue/CameraCache");
	private File mCurrentPhotoFile;// 照相机拍照得到的图片
	Button buttonOK,button01;
	ImageView imageViewPhoto;
	String FileName;
	private CascadeClassifier mJavaDetector;
	private Bitmap srcBitmap;
	private Bitmap grayBitmap;
	private Bitmap cutBitmap;
	private Bitmap bm;// 需要旋转的图片资源Bitmap
	private float scaleW = 1;// 横向缩放系数，1表示不变
	private float scaleH = 1;// 纵向缩放系数，1表示不变
	private float curDegrees = 90;// 当前旋转度数
	private boolean isFirstTime = false;
	private static boolean flag = true; 
	private static boolean isFirst = true; 
	TextView text1;
	
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

		@Override
		
		public void onManagerConnected(int status) {
			// TODO Auto-generated method stub
			switch (status){
			case BaseLoaderCallback.SUCCESS:{
				mJavaDetector = new CascadeClassifier(
						Environment.getExternalStorageDirectory() + "//1/cascade.xml");
				if (mJavaDetector.empty()) {
                    Log.e(TAG, "Failed to load cascade classifier");
                    mJavaDetector = null;
                } else
                    Log.i(TAG, "Loaded cascade classifier");

			}
				
				break;
			default:
				super.onManagerConnected(status);
				Log.i(TAG, "加载失败");
				break;
			}
			
		}
	};

	/** Called when the activity is first created */

	private Handler mHandler;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.way);
		button01=(Button) findViewById(R.id.Button01);
		buttonOK = (Button) findViewById(R.id.ButtonOK);
		text1=(TextView)findViewById(R.id.textView1);
		imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
		buttonOK.setOnClickListener(this);
		button01.setOnClickListener(this);
 		//创建一个handler对象，重写handleMessage回调函数,在回调函数里面做UI更新操作
 		/*mHandler = new Handler() {
 			@Override
 			public void handleMessage(Message msg) {
 				switch (msg.what) {
 				case 0x1:
 					//在这里更新UI
 				
 					imageViewPhoto.setImageBitmap(cutBitmap);
 					break;

 				default:
 					break;
 				}
 			}
 		};*/
 		//new Thread(new InnerRunnable()).start();
 		
	}
 	class InnerRunnable implements Runnable {

 		@Override
 		public void run() {
 			//做耗时操作放在这里
 			DetectFace();
 			//创建一个消息对象
 			Message msg = Message.obtain();
 			msg.what = 0x1;
 			//可以用Bundle传递数据
 			//mHandler.sendMessage(msg);
 		}
 	}

	
	
	public void procSrc2Gray(){
		Mat rgbMat = new Mat();
		Mat grayMat = new Mat();
		imageViewPhoto.setDrawingCacheEnabled(true);
		//Resources res=getResources();
		//Drawable drawable = imageViewPhoto.getResources().getDrawable(R.drawable.this); 
		//srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car);
		srcBitmap=imageViewPhoto.getDrawingCache();
		grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Config.RGB_565);
		Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.
		Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
		Utils.matToBitmap(grayMat, srcBitmap); //convert mat to bitmap
		Log.i(TAG, "procSrc2Gray sucess...");

	}
	
	//public void imagecuter()
	//{
		//Mat image = new Mat();
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //System.out.println("\nRunning FaceDetector");
		//System.loadLibrary("detection_based_tracker");
        //CascadeClassifier faceDetector = new CascadeClassifier(MainActivity.class.getResource("/assets/cascade.xml").getPath().substring(1));
		
        //CascadeClassifier faceDetector = new CascadeClassifier("/assets/cascade.xml");
        //Log.i(TAG, "xml sucess...");
        
        //CascadeClassifier faceDetector1 = new CascadeClassifier(facetest1.class.getResource("haarcascade_mcs_nose.xml").getPath().substring(1));
        
        //CascadeClassifier faceDetector2 = new CascadeClassifier(facetest1.class.getResource("haarcascade_eye_tree_eyeglasses.xml").getPath().substring(1));
        
        //CascadeClassifier faceDetector3 = new CascadeClassifier(facetest1.class.getResource("haarcascade_eye_tree_eyeglasses.xml").getPath().substring(1));
        
       // imageViewPhoto.setDrawingCacheEnabled(true);
        //srcBitmap=imageViewPhoto.getDrawingCache();
        
       /* Utils.bitmapToMat(srcBitmap, image);
        int height = image.rows();
        float mRelativeFaceSize   = 0.2f;
        float mAbsoluteFaceSize   = 0;
        if (Math.round(height * mRelativeFaceSize) > 0) {
            mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
        }*/
        
       // MatOfRect faceDetections = new MatOfRect();
       // faceDetector.detectMultiScale(image, faceDetections, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        //faceDetector.detectMultiScale(image, faceDetections);
      
        /*
        for (Rect rect : faceDetections.toArray()) {
        	Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
            Core.circle(image, new Point(rect.x+rect.width/2, rect.y+rect.height/2),(rect.width+rect.height)/4 , new Scalar(0, 255, 0));
            
            System.out.println(rect.area());
            Graphics p;
            
        	for(Rect rect1:faceDetections1.toArray())
        	{
               if(rect.contains(rect1.tl())&&rect.contains(rect1.br()))
            	   Core.rectangle(image, new Point(rect1.x, rect1.y), new Point(rect1.x + rect1.width, rect1.y + rect1.height),
                           new Scalar(0, 255, 0));
        	}
        	for(Rect rect2:faceDetections2.toArray())
        	{
        		if(rect.contains(rect2.tl())&&rect.contains(rect2.br()))
             	   Core.rectangle(image, new Point(rect2.x, rect2.y), new Point(rect2.x + rect2.width, rect2.y + rect2.height),
                            new Scalar(0, 255, 0));
        	}
        	
        }*/
        
        
        
        
        
        //Highgui.imwrite(filename, image);
       // for (Rect rect : faceDetections.toArray()) {
        //	cutBitmap=Bitmap.createBitmap(srcBitmap,rect.x,rect.y,rect.width,rect.height);
        	 
		
	//}
	//}
        
	
    public void DetectFace() {  
        Mat image = new Mat();  
        MatOfRect faceDetections = new MatOfRect();  
        imageViewPhoto.setDrawingCacheEnabled(true);
        srcBitmap=imageViewPhoto.getDrawingCache(); 
        Utils.bitmapToMat(srcBitmap, image);
		try{
		mJavaDetector.detectMultiScale(image, faceDetections);
	    //mJavaDetector.detectMultiScale(image, faceDetections, 1.1, 2, 3,new Size(0,0), new Size(0,0));
			
	    Log.i(TAG, "成功");
		}
		catch(Exception e){
			e.printStackTrace();
			Log.i(TAG, "失败");
		}
		
		  if(faceDetections.toArray().length!=0){
		    Log.i(TAG, "数量成功");
		  }
		  else{
			  Log.i(TAG, "数量失败");
		  }
		
        for (Rect rect : faceDetections.toArray()) {
            cutBitmap=Bitmap.createBitmap(srcBitmap,rect.x,rect.y,rect.width,rect.height);	 
    		}
           
    }
       
        
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ButtonOK:
			doPickPhotoAction();
			break;
		case R.id.Button01:
			if(isFirst){
				//Thread loginThread = new Thread(new InnerRunnable());
				//loginThread.start();
				DetectFace();
				isFirst = false;
			}
			if(flag){
			imageViewPhoto.setImageBitmap(cutBitmap);
			button01.setText("查看原图");
			flag = false;
			}
			else{
				imageViewPhoto.setImageBitmap(srcBitmap);
				button01.setText("灰度化");
				flag = true;
			}
			
		default:
			break;
		}
	}
	
	
	private void doPickPhotoAction() {
		Context context = MainActivity.this;
		// Wrap our context to inflate list items using correct theme
		final Context dialogContext = new ContextThemeWrapper(context, android.R.style.Theme_Light);
		String cancel = "返回";
		String[] choices;
		choices = new String[2];
		choices[0] = getString(R.string.take_photo); // 拍照
		choices[1] = getString(R.string.pick_photo); // 从相册中选择
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext, android.R.layout.simple_list_item_1,
				choices);
		final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
		builder.setTitle(R.string.attachToContact);
		builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0: {
					String status = Environment.getExternalStorageState();
					isFirst=true;
					flag=true;
					if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
						doTakePhoto();// 用户点击了从照相机获取
					} else {
						Toast.makeText(MainActivity.this, "没有SD卡", Toast.LENGTH_LONG).show();
					}
					break;
				}
				case 1:
					doPickPhotoFromGallery();// 从相册中去获取
					isFirst=true;
					flag=true;
					break;
				}
			}
		});
		builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	protected void doTakePhoto() {
		try {
			// Launch camera to take photo for selected contact
		if (!PHOTO_DIR.exists()) {
				boolean iscreat = PHOTO_DIR.mkdirs();// 创建照片的存储目录
				Log.e("YAO", "" + iscreat);
			}
			FileName = "sn11.jpg";
			mCurrentPhotoFile = new File(PHOTO_DIR, FileName);
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "photoPickerNotFoundText", Toast.LENGTH_LONG).show();
		}
	}
	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	/**
	 * 用当前时间给取得的图片命名,该方法调用后不能获得照片
	 * 
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date) + ".jpg";
	}

	// 请求Gallery程序
	protected void doPickPhotoFromGallery() {
		try {
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.photoPickerNotFoundText, Toast.LENGTH_LONG).show();
		}
	}

	// // 封装请求Gallery的intent
	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		return intent;
	}

	// 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA:
			Uri uri = data.getData();
			String selectedImagePath = getPath(uri);
			Log.e(TAG, "1111111111" + selectedImagePath);
			imageViewPhoto.setImageURI(uri);

			break;
		case CAMERA_WITH_DATA:
			 //String latestImagePath = getLatestImage();
			 //Log.e(TAG, "1111111111" + latestImagePath);
			 //File file = new File(latestImagePath);
			 //Uri uri1 = Uri.fromFile(file);
			 //imageViewPhoto.setImageURI(uri1);
			right();

			break;
		}
	}

	/**
	 * 获取访问SD卡中图片路径
	 * 
	 * @return
	 */
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * 获取SD卡中最新图片路径
	 * 只能获取到自己创建的目录，并且第一次创建还获取不到【暂时废弃】
	 * @return
	 */
	protected String getLatestImage() {
		String latestImage = null;
		String[] items = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items, null, null,
				MediaStore.Images.Media._ID + " desc");

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				latestImage = cursor.getString(1);
				break;
			}
		}

		return latestImage;
	}

	// 右转事件处理
	private void right() {
		File f = new File(PHOTO_DIR,FileName);
		if (f.exists()) {
			// 此外，你还可以使用BitmapFactory的decodeResource方法获得一个Bitmap对象
			// 使用decodeResource方法时传入的是一个drawable的资源id
			// 还有一个decodeStream方法，这个方法传入一个图片文件的输入流即可！
			bm = BitmapFactory.decodeFile(f.getAbsolutePath());
			// 设置ImageView的显示图片
			// imageViewPhoto.setImageBitmap(bm);
		} else {
			Toast.makeText(this, "文件不存在！", Toast.LENGTH_SHORT).show();
			return;
		}
		int bmpW = bm.getWidth();
		int bmpH = bm.getHeight();
		// 设置图片放大比例
		double scale = 1;
		// 计算出这次要放大的比例
		scaleW = (float) (scaleW * scale);
		scaleH = (float) (scaleH * scale);
		// 产生reSize后的Bitmap对象
		// 注意这个Matirx是android.graphics包下的那个
		Matrix mt = new Matrix();
		mt.postScale(scaleW, scaleH);
		mt.setRotate(curDegrees);
		Bitmap resizeBmp = Bitmap.createBitmap(bm, 0, 0, bmpW, bmpH, mt, true);
		imageViewPhoto.setImageBitmap(resizeBmp);
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//load OpenCV engine and init OpenCV library
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, getApplicationContext(), mLoaderCallback);
		Log.i(TAG, "onResume sucess load OpenCV...");
//		new Handler().postDelayed(new Runnable(){
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				procSrc2Gray();
//			}
//			
//		}, 1000);
		
	}
	 
     	
	 
}
    