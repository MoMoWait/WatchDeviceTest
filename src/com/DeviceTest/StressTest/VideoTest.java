package com.DeviceTest.StressTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.DeviceTest.helper.OpenFileDialog;
import com.DeviceTest.helper.OpenFileDialog.CallbackBundle;
import com.DeviceTest.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class VideoTest extends StressBase implements OnClickListener {
	private final static String LOG_TAG = "VideoTestActivity";
	
	public String mFilePath = "/mnt/external_sd/test.avi";
	public final int DIALOG_ID = 1;
	private final String FILTER_SUFFIX = ".avi;.mp4;.wmv;.mkv;.mpg;";
	private final String VIDEO_PATH_CONFIG = "video.path";
	
	private TextView mFilePathTV;
	private Button mSelectBtn;
	private Button mDeleteBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_test);
		init();
		hideSettingView();
	}

	private void init() {
		getVideoPathConfig(this);
		
		setDefaultBtnId(R.id.start_btn, R.id.stop_btn, R.id.exit_btn, 0);

		mFilePathTV = (TextView) findViewById(R.id.filePath_tv);
		mFilePathTV.setText(getString(R.string.video_path) + mFilePath);

		mSelectBtn = (Button) findViewById(R.id.select_btn);
		mSelectBtn.setOnClickListener(this);

		mDeleteBtn = (Button) findViewById(R.id.delete_btn);
		mDeleteBtn.setOnClickListener(this);

	}

	private void updateView() {
		mFilePathTV.setText(getString(R.string.video_path) + mFilePath);
	}
	
	
	@Override
	public void onStartClick() {
		if (!isVideoFileExist()) {
			Log.e(LOG_TAG, "video file (" + mFilePath+ ") isn't exist");
			Toast.makeText(this, R.string.error_video, Toast.LENGTH_LONG).show();
			return;
		}
		
		Intent intent = new Intent(this, VideoPlayActivity.class);
		intent.putExtra("path", mFilePath);
		startActivity(intent);
	}

	@Override
	public void onStopClick() {
		finish();

	}

	@Override
	public void onSetMaxClick() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_btn:
			showFileSelectDialog();
			break;
		case R.id.delete_btn:
			deleteVideoFile();
			break;
		default:
			break;
		}
	}
	
	
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_ID) {
			Map<String, Integer> images = new HashMap<String, Integer>();
			// 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹 
			images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root);	// 根目录图标  
			images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up);	//返回上一层的图标
			images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder);	//文件夹图标  
			images.put("mkv", R.drawable.filedialog_videofile);	//wav文件图标
			images.put("avi", R.drawable.filedialog_videofile); //视频
			images.put("mp4", R.drawable.filedialog_videofile); //视频
			images.put("wmv", R.drawable.filedialog_videofile); //视频
			images.put("mpg", R.drawable.filedialog_videofile); //视频
			images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);
			Dialog dialog = OpenFileDialog.createDialog(DIALOG_ID, this, "打开文件", new CallbackBundle() {
				@Override
				public void callback(Bundle bundle) {
					String filepath = bundle.getString("path");
					mFilePath = filepath;
					updateView();
				}
			}, 
			FILTER_SUFFIX,
			images);
			return dialog;
		}
		
		
		return super.onCreateDialog(id);
	}
	
	
	private void showFileSelectDialog() {
		showDialog(DIALOG_ID);
	}
	
	private boolean isVideoFileExist() {
		File file = new File(mFilePath);
		return file.exists();
	}
	
	private void deleteVideoFile() {
		if (isVideoFileExist()) {
			File file = new File(mFilePath);
			boolean result = file.delete();
			Toast.makeText(this, getString(R.string.delete_video)+" "+result, Toast.LENGTH_LONG).show();
		} else {
			
		}
	}
	
	public void getVideoPathConfig(Context c) {
		String temp;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(c.getAssets().open(VIDEO_PATH_CONFIG)));

			while ((temp = br.readLine()) != null) {
				String[] path = temp.split(":");
				mFilePath = path[1];
			}
			
			if (br != null) {
				br.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void hideSettingView() {
		mFilePathTV.setVisibility(View.GONE);
		mSelectBtn.setVisibility(View.GONE);
		mDeleteBtn.setVisibility(View.GONE);
	}
	
}
