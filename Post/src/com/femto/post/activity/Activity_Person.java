package com.femto.post.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.femto.post.R;
import com.femto.post.appfinal.AppUrl;
import com.femto.post.application.MyApplication;
import com.femto.post.customview.CircleImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hyphenate.chat.EMClient;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.ResLoader;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("SimpleDateFormat")
public class Activity_Person extends BaseActivity {
	
	private RelativeLayout rl_left, rl_changehead, rl_myright, rl_mymessage,
			rl_setting;
	private TextView tv_title, tv_exit;
	private CircleImageView im_head;
	//裁剪之后保存的位置...
	private String HEAD_PHOTO_PATH = "head_photo_path.jpg";
	//裁剪头像的路径..
	private File headFile;

	private TextView tv_right;
	private TextView tv_nicheng;
	private HttpUtils httpUtils = new HttpUtils();
	private RelativeLayout rl_right_text;
	//private TextView tv_right_genggai;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_left:
			finish();
			break;
		case R.id.rl_changehead:
			showSettingFaceDialog();
			break;
		case R.id.rl_myright:
			openActivity(Activity_MyRight.class, null);
			break;
		case R.id.rl_mymessage:
			openActivity(Activity_MyMessage.class, null);
			break;
		case R.id.rl_setting:
			openActivity(Activity_Setting.class, null);
			break;
		case R.id.tv_exit:
			showExit();
			break;
		case R.id.rl_nicheng:
			openActivityForResult(UpdateNameActivity.class, UPDATE_NAME_RESULT);
			break;
		
		case R.id.rl_my_phone:
			openActivityForResult(UpdatePhoneActivity.class, UPDATE_PHONE_RESULT);
			break;
			
		default:
			break;
		}
	}

	private void openActivityForResult(Class<?> cls,int requestCode) {
		Intent intent = new Intent(Activity_Person.this,cls);
		startActivityForResult(intent, requestCode);
	}

	/*private void sendMessageToNet() {
		String userName = et_nicheng.getText().toString();
		if(TextUtils.isEmpty(userName.trim())) {
			Toast.makeText(Activity_Person.this, "昵称不能为空", 0).show();
			return;
		} 
		showProgressDialog("提交个人信息中...");
		com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
		params.addBodyParameter("id", MyApplication.userId);
		Log.i("test3","userId==="+MyApplication.userId);
		params.addBodyParameter("username", userName);
		Log.i("test3", userName);
		RequestCallBack<String> callBack = new RequestCallBack<String>() {
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				dismissProgressDialog();
				try {
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					String result = jsonObject.getString("result");
					if(result.equals("0")) {
						String username = jsonObject.getString("username");
						SharedPreferences sp = getSharedPreferences(
								"login", Context.MODE_PRIVATE);
						Editor edit = sp.edit();
						//edit.putInt("userId", userId);
						edit.putString("username", username);
						edit.commit();
						MyApplication.getBasicInformation();
						Toast.makeText(Activity_Person.this, "修改信息成功", 0).show();
					} else {
						Toast.makeText(Activity_Person.this, "修改信息失败", 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(Activity_Person.this, "修改信息失败", 0).show();
				}
				
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dismissProgressDialog();
				Toast.makeText(Activity_Person.this, "网络异常", 0).show();
			}
		};
		httpUtils.send(HttpMethod.POST, AppUrl.updateUserInfo, params,callBack);
	}*/

	@Override
	public void initView() {
		rl_left = (RelativeLayout) findViewById(R.id.rl_left);
		rl_changehead = (RelativeLayout) findViewById(R.id.rl_changehead);
		rl_myright = (RelativeLayout) findViewById(R.id.rl_myright);
		rl_mymessage = (RelativeLayout) findViewById(R.id.rl_mymessage);
		rl_setting = (RelativeLayout) findViewById(R.id.rl_setting);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_exit = (TextView) findViewById(R.id.tv_exit);
		tv_right = (TextView)findViewById(R.id.tv_right);
		im_head = (CircleImageView) findViewById(R.id.im_head);
		tv_nicheng = (TextView) findViewById(R.id.tv_nicheng);
		rl_my_phone = (RelativeLayout) findViewById(R.id.rl_my_phone);
		rl_nicheng = (RelativeLayout) findViewById(R.id.rl_nicheng);
		tv_my_phone = (TextView) findViewById(R.id.tv_my_phone);
		/*tv_right_genggai = (TextView) findViewById(R.id.tv_right_genggai);
		tv_right_genggai.setVisibility(View.VISIBLE);*/
		setUserInfo();
	}

	private void setUserInfo() {
		tv_nicheng.setText(MyApplication.username);
		if(MyApplication.phone!=null) {
			rl_my_phone.setClickable(true);
		} else {
			rl_my_phone.setClickable(false);
		}
		tv_my_phone.setText(MyApplication.phone);
		//注意  网络返回的图片地址只是相对地址  因此要手动加上服务器ip和项目名
		String photoUrl = AppUrl.BASEURL+"/YouBi"+MyApplication.url;
		Log.i("test3", "头像图片的地址为"+photoUrl);
		ImageLoader.getInstance().displayImage(photoUrl, im_head, MyApplication.getOptions(R.drawable.person));
	}

	@Override
	public void initUtils() {
		
	}

	@Override
	public void Control() {
		tv_title.setText("个人中心");
		rl_left.setOnClickListener(this);
		rl_changehead.setOnClickListener(this);
		rl_myright.setOnClickListener(this);
		rl_mymessage.setOnClickListener(this);
		rl_setting.setOnClickListener(this);
		tv_exit.setOnClickListener(this);
		//tv_right_genggai.setOnClickListener(this);
		//设置用户名和头像...
		rl_nicheng.setOnClickListener(this);
		rl_my_phone.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_person);
		MyApplication.addActivity(this);
	}
	//
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		// super.onSaveInstanceState(outState);
	}

	// 选择头像
	private String[] items = new String[] { "图库", "拍照" };
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "face.jpg";
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int SELECT_PIC_KITKAT = 3;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private static final int UPDATE_NAME_RESULT = 5;
	private static final int UPDATE_PHONE_RESULT = 4;
	/* 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	@SuppressWarnings("unused")
	private File mCurrentPhotoFile;// 照相机拍照得到的图片
	private RelativeLayout rl_my_phone;
	private RelativeLayout rl_nicheng;
	private TextView tv_my_phone;

	private void showSettingFaceDialog() {

		new AlertDialog.Builder(this)
				.setTitle("图片来源")
				.setCancelable(true)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0://从相册选择..
							Intent intent = new Intent(
									Intent.ACTION_GET_CONTENT);
							intent.addCategory(Intent.CATEGORY_OPENABLE);
							intent.setType("image/*");
							if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
								startActivityForResult(intent,
										SELECT_PIC_KITKAT);
							} else {
								startActivityForResult(intent,
										IMAGE_REQUEST_CODE);
							}
							break;
						case 1:// Take Picture
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (hasSdcard()) {
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												IMAGE_FILE_NAME)));
							}
							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	private boolean hasSdcard() {
		// TODO Auto-generated method stub
		return Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case SELECT_PIC_KITKAT:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory(),
							IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(Activity_Person.this, "没有找到SD卡，无法存储照片！",
							Toast.LENGTH_SHORT).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				//设置图片到圆形头像上...
				if (data == null) {
					return;
				}
				setImageToView(data, im_head);
				//发送网络请求将保存图片...
				Bitmap bitmap = data.getParcelableExtra("data");
				FileOutputStream fos;
				if(headFile == null) {
					headFile = new File(Environment.getExternalStorageDirectory(),HEAD_PHOTO_PATH);
				}
				try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos.flush();
				fos = new FileOutputStream(headFile);
				bos.writeTo(fos);
				bos.close();
				fos.close();
				showProgressDialog("上传头像中...");
				RequestParams params = new RequestParams();
				params.add("user.id", MyApplication.userId);
				Log.i("test", "userId ="+MyApplication.userId);
				params.put("doc", headFile);
				Log.i("test", headFile.getPath());
				//params.add("doc", headFile.getPath());
				//发送网络请求将图片发送过去...
				//MyApplication.ahc.post(url, responseHandler);
				
				MyApplication.ahc.post(AppUrl.userPhoto, params,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode, Header[] headers,
									JSONObject response) {
								super.onSuccess(statusCode, headers, response);
								dismissProgressDialog();
								String result = response.optString("result");
								String message = response.optString("message");
								//getII(result);
								if (result.equals("0")) {
									//Login();
									Toast.makeText(Activity_Person.this, "头像上传成功", 0).show();
									//拿到头像的url
									String photoUrl = response.optString("url");
									Log.i("test3", "头像保存的地址为"+photoUrl);
									//将url保存到首选项...
									SharedPreferences sp = getSharedPreferences(
											"login", Context.MODE_PRIVATE);
									Editor edit = sp.edit();
									//edit.putInt("userId", userId);
									edit.putString("url", photoUrl);
									edit.commit();
									MyApplication.getBasicInformation();
								} else {
									Toast.makeText(Activity_Person.this, "上传头像失败", 0).show();
								}
							}
							
							@Override
							public void onFailure(int statusCode,
									Header[] headers, String responseString,
									Throwable throwable) {
								super.onFailure(statusCode, headers, responseString, throwable);
								dismissProgressDialog();
								Toast.makeText(Activity_Person.this, "网络异常", 0).show();
							}
						});
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(Activity_Person.this, "上传头像失败", 0).show();
				}
				
				break;
			case UPDATE_PHONE_RESULT:
				tv_my_phone.setText(MyApplication.phone);
				break;
			case UPDATE_NAME_RESULT:
				tv_nicheng.setText(MyApplication.username);
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		if (uri == null) {
			// Log.i("tag", "The uri is not exist.");
			return;
		}

		Intent intent = new Intent("com.android.camera.action.CROP");
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			String url = getPath(this, uri);
			intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
		} else {
			intent.setDataAndType(uri, "image/*");
		}
		mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setImageToView(Intent data, ImageView imageView) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			// Bitmap roundBitmap = ImageUtil.toRoundBitmap(photo);
			imageView.setImageBitmap(photo);
			saveBitmap(photo);
		}
	}

	public void saveBitmap(Bitmap mBitmap) {
		if (!PHOTO_DIR.exists()) {
			PHOTO_DIR.mkdirs();
		}
		File f = new File(PHOTO_DIR, IMAGE_FILE_NAME);
		try {
			f.createNewFile();
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();
			System.out.println("zuo==file=" + f.getPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date) + ".jpg";
	}

	private void showExit() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确定退出?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						SharedPreferences sp = getSharedPreferences("login",
								Context.MODE_PRIVATE);
						//环信退出登录...
						EMClient.getInstance().logout(true);
						MyApplication.IsLoginHuanxin = false;
						Editor edit = sp.edit();
						//edit.putInt("userId", 0);
						edit.putString("userId", null);
						edit.putString("userName", "");
						edit.putString("phone", "");
						edit.putString("name", "");
						edit.putString("url", "");
						edit.putBoolean("islogin", false);
						edit.commit();
						MyApplication.getBasicInformation();
						
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}
}
