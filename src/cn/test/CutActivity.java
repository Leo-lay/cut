package cn.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class CutActivity extends Activity {
	private Button button;
	private ImageView imageView;
	private File mCurrentPhotoFile;
	private Bitmap cameraBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		button = (Button) findViewById(R.id.button);
		imageView = (ImageView) findViewById(R.id.imageView);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
				"android.media.action.IMAGE_CAPTURE");
				mCurrentPhotoFile = new File(
						"mnt/sdcard/DCIM/Concordy/",
						getPhotoFileName());
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
		startActivityForResult(
				intent,
				Activity.DEFAULT_KEYS_DIALER);
			}

			
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		case 1:
			Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			ContentResolver cr = CutActivity.this
					.getContentResolver();

			Uri fileUri = Uri.fromFile(mCurrentPhotoFile);
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
					fileUri));
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Cursor cursor = cr
					.query(imgUri, null,
							MediaStore.Images.Media.DISPLAY_NAME + "='"
									+ mCurrentPhotoFile.getName() + "'",
							null, null);
			Uri uri = null;
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToLast();
				long id = cursor.getLong(0);
				uri = ContentUris.withAppendedId(imgUri, id);
			}
			final Intent intent = new Intent(
					"com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			intent.putExtra("crop", "true");
			intent.putExtra("outputX", 380);
			intent.putExtra("outputY", 500);
			intent.putExtra("return-data", true);
			CutActivity.this.startActivityForResult(intent, 3);
			break;
		case 2:
			break;
		case 3:
			if (data != null) {
				cameraBitmap = (Bitmap) data.getExtras().get("data");
				imageView.setImageBitmap(cameraBitmap);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";

	}
}