package com.example.zcsoftware.Viewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.database.Cursor;

import com.example.zcsoftware.R;
import com.example.zcsoftware.hwInterface.HWDevice;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by arthur on 9/4/13.
 *
 * This Activity is for:
 *
 *      REGISTER a NEW DEVICE
 *               OR
 *      MODIFY an EXISTING DEVICE
 *
 */
public class DeviceDetail extends Activity {

    private String id;
    private String mac;
    private String name;
    //private String description;
    private String nickname;
    private String imageName; // actually it is the absolute URL of the image, equals to mCurrentPhotoPath below
    private String pickedImg; // the image the user selected from his gallery
    private int type; // bike, phone etc
    private String latitude;
    private String longitude;
    private boolean isLost;
    private String showDelBtn;

    private TextView mac_tv;
    private TextView deviceName_tv;
    private TextView kind_tv;
    //private Button takePic_btn;
    private EditText alias_et;
    //private EditText description_et;
    private Spinner type_spinner;
    private CheckBox isLost_chb;
    private Button save_btn;
    private Button pick_btn;

    private Intent result_form_intent;
    private Intent in_coming_intent;
    private int idvalue;

    private static final int ACTIVITY_SELECT_IMAGE = 100;

    public static final String DEV_NAME = "dev_name";
    public static final String DEV_MAC = "dev_mac";
    public static final String DEV_ALIAS = "dev_alias";
    public static final String DEV_DESP = "dev_desp";
    public static final String DEV_LGCTYPE = "dev_lgctype";
    public static final String DEV_IMGNAME = "dev_imagename";
    public static final String DEV_POST = "dev_post";
    public static final String DEV_LOST = "dev_lost";
    public static final String DEV_HWTYPE = "dev_hwtype";
    public static final String DEV_ID = "dev_id";
    public static final String DEV_SHOW_DEL = "show_del";
    /**-----------------------------------------------------------------------------------------------------------------------------------------------------------
     * Code deals with camera, downloaded from Google Official Training Examples.
     *
     * URL =
     * http://developer.android.com/training/camera/photobasics.html
     *
     * ALSO WORKS WITH FILES:
     *  +     AlbumStorageDirFactory.java
     *  +     BaseAlbumDirFactory.java
     *  +     FroyoAlbumDirFactory.java
     -----------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int ACTION_TAKE_PHOTO_S = 2;
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    /**----------End of Google Code------------------------------------------------------------------------------------------------------------------------------*/

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detail_with_delete);
        iniGoogleCameraCode();
        initVar();
        initUI();
        initListener();
    }

    /**
     * UI
     */
    private void initUI(){

        Button loc_del=(Button)findViewById(R.id.loc_del_btn);
        Button glo_del=(Button)findViewById(R.id.glo_del_btn);
        if(showDelBtn.toLowerCase().equals("no")){
            loc_del.setVisibility(View.GONE);
            glo_del.setVisibility(View.GONE);
        }
        mac_tv = (TextView)findViewById(R.id.deviveDetail_mac_tv);
        mac_tv.setText(mac);

        deviceName_tv = (TextView)findViewById(R.id.deviceDetail_name_tv);
        deviceName_tv.setText(name);

        kind_tv = (TextView)findViewById(R.id.deviveDetail_kind_tv);
        //takePic_btn = (Button)findViewById(R.id.deviceDetail_takePic_btn);
        save_btn = (Button)findViewById(R.id.deviveDetail_save_btn);
        pick_btn = (Button)findViewById(R.id.deviceDetail_pickPic_btn);

        alias_et = (EditText)findViewById(R.id.deviveDetail_alias_et);
        alias_et.setFocusable(false);
        if(nickname!=null){
            alias_et.setText(nickname.toString());
        }

        //description_et = (EditText)findViewById(R.id.deviveDetail_description_et);
        isLost_chb = (CheckBox)findViewById(R.id.deviveDetail_islost_flag);
        type_spinner = (Spinner) findViewById(R.id.deviceDetail_type_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.device_type_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        type_spinner.setAdapter(adapter);

        type_spinner.setSelection(type);

        /*
        if(type!=null){
            if(type.toLowerCase().equals("camera")){
                type_spinner.setSelection(adapter.getPosition("camera"));
            } else if(type.toLowerCase().equals("key")){
                type_spinner.setSelection(adapter.getPosition("key"));
            } else if(type.toLowerCase().equals("phone")){
                type_spinner.setSelection(2);
            } else if(type.toLowerCase().equals("tile")){
                type_spinner.setSelection(3);
            } else if(type.toLowerCase().equals("car")){
                type_spinner.setSelection(4);
            } else if(type.toLowerCase().equals("trackr")){
                type_spinner.setSelection(5);
            } else if(type.toLowerCase().equals("keyboard")){
                type_spinner.setSelection(6);
            } else if(type.toLowerCase().equals("unknown")){
                type_spinner.setSelection(7);
            }
        }
        */
        // isLost, the check-box
        isLost_chb.setChecked(isLost);
    }

    /**
     * get var from in-coming intent
     */
    private void initVar(){
        in_coming_intent = getIntent();
        Bundle extras = in_coming_intent.getExtras();
        mac = "00-00-00-00-00";
        name = "unknown device name";
        showDelBtn="no";
        if(extras!=null){
            if(extras.getString(DEV_MAC)!=null) mac = extras.getString(DEV_MAC);
            if(extras.getString(DEV_NAME)!=null) name=extras.getString(DEV_NAME);
            if(extras.getString(DEV_ALIAS)!=null) nickname=extras.getString(DEV_ALIAS);
            //if(extras.getString(DEV_DESP)!=null) description=extras.getString(DEV_DESP);
            type = extras.getInt(DEV_LGCTYPE);
            /*
            if(extras.getString(DEV_LGCTYPE)!=null) {
                type=extras.getString(DEV_LGCTYPE);
            } else type="camera";
            */

            imageName = extras.getString(DEV_IMGNAME);
//            if(extras.getBoolean(DEV_LOST)!=null)
            if(extras.getBoolean(DEV_LOST)==true)isLost=true;
            else isLost = false;
            if(extras.getString(DEV_SHOW_DEL)!=null) showDelBtn=extras.getString(DEV_SHOW_DEL);
            idvalue=extras.getInt(DEV_ID);

            ImageView imageView = (ImageView)findViewById(R.id.imageView1);
            if (null ==  imageName)
            {
                imageView.setImageResource(R.drawable.bt);
            }
            else
            {
                //imageView.setImageResource(currentLocDev.getImgID());
                imageView.setImageBitmap(BitmapFactory.decodeFile(imageName));
                //imageView.setClickable(true);
                imageView.setFocusable(false);
            }

        }
    }

    /**
     * Listeners
     */
    private void initListener(){
        alias_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alias_et.setFocusableInTouchMode(true);
                alias_et.setFocusable(true);
                alias_et.requestFocus();
            }
        });
//        description_et.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                description_et.setFocusableInTouchMode(true);
//                description_et.setFocusable(true);
//                description_et.requestFocus();
//            }
//        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            //type = R.array.device_type_array[type_spinner.getSelectedItemPosition()];
            @Override
            public void onClick(View v) {
                result_form_intent = new Intent(DeviceDetail.this, activity_regdevselect.class);
                result_form_intent.putExtra(DEV_NAME,name);
                result_form_intent.putExtra(DEV_MAC,mac);
                result_form_intent.putExtra(DEV_ALIAS,alias_et.getText().toString());
                //result_form_intent.putExtra(DEV_DESP,description_et.getText().toString());
                // to be tested
                result_form_intent.putExtra(DEV_LGCTYPE,(type_spinner.getSelectedItemPosition()));
                // not implemented
                result_form_intent.putExtra(DEV_IMGNAME,imageName);
                result_form_intent.putExtra(DEV_POST,false);
                result_form_intent.putExtra(DEV_LOST,isLost_chb.isChecked());
                result_form_intent.putExtra(DEV_ID,idvalue);
                setResult(Activity.RESULT_OK,result_form_intent);
                finish();
            }
        });
        pick_btn.setOnClickListener(new View.OnClickListener() {
            //type = R.array.device_type_array[type_spinner.getSelectedItemPosition()];
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
            }
        });
    }

    /**-----------------------------------------------------------------------------------------------------------------------------------------------------------
     * The FLOWING CODE ARE:
     * -----------------------------------------------------------------------------------------------------------------------------------------------------------
     * Code deals with camera, downloaded from Google Official Training Examples.
     * -----------------------------------------------------------------------------------------------------------------------------------------------------------
     * URL =
     * http://developer.android.com/training/camera/photobasics.html
     * -----------------------------------------------------------------------------------------------------------------------------------------------------------
     * ALSO WORKS WITH FILES:
     *  +     AlbumStorageDirFactory.java
     *  +     BaseAlbumDirFactory.java
     *  +     FroyoAlbumDirFactory.java
     -----------------------------------------------------------------------------------------------------------------------------------------------------------*/
    private void iniGoogleCameraCode(){
        mImageView = (ImageView) findViewById(R.id.imageView1);
        mImageBitmap = null;
        Button picBtn = (Button) findViewById(R.id.deviceDetail_takePic_btn);
        setBtnListenerOrDisable(
                picBtn,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }
    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        imageName = mCurrentPhotoPath;
        return f;
    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case ACTION_TAKE_PHOTO_B:
            case ACTION_TAKE_PHOTO_S:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch
        startActivityForResult(takePictureIntent, actionCode);
    }
    private void handleBigCameraPhoto() {
        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }
    }

    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        mImageBitmap = (Bitmap) extras.get("data");
        mImageView.setImageBitmap(mImageBitmap);
        //mVideoUri = null;
        mImageView.setVisibility(View.VISIBLE);
        //mVideoView.setVisibility(View.INVISIBLE);
    }

    private void handleNewSmallCameraPhoto() {
        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }
    }
    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_S);
                }
            };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO_B

            case ACTION_TAKE_PHOTO_S: {
                if (resultCode == RESULT_OK) {
                    //handleSmallCameraPhoto(data);
                    handleNewSmallCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO_S

            case ACTIVITY_SELECT_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    imageName = filePath;
                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    mImageView.setImageBitmap(yourSelectedImage);
                }
        } // switch
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );
    }

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action The Intent action to check for availability.
     *
     * @return True if an Intent with the specified action can be sent and
     *         responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }
}