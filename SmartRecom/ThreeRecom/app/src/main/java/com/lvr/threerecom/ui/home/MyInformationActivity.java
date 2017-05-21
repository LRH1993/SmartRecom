package com.lvr.threerecom.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.irecyclerview.IRecyclerView;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.InformationAdapter;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.bean.FavorListBean;
import com.lvr.threerecom.bean.InformationBean;
import com.lvr.threerecom.bean.LoginBean;
import com.lvr.threerecom.ui.home.presenter.impl.InformationPresenterImpl;
import com.lvr.threerecom.ui.home.view.InformationView;
import com.lvr.threerecom.utils.BitmapUtils;
import com.lvr.threerecom.utils.CapturePhotoHelper;
import com.lvr.threerecom.utils.FolderManager;
import com.lvr.threerecom.utils.SPUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by lvr on 2017/5/15.
 */

public class MyInformationActivity extends BaseActivity implements InformationView, InformationAdapter.onItemClickListenr {
    //请求开启相册
    private static final int REQUEST_PICK_IMAGE = 3;
    private static final int REQUEST_PICKER_AND_CROP_2 = 4;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.irv_information)
    IRecyclerView mIrvInformation;
    private ActionBar mBar;
    private InformationPresenterImpl mPresenter;
    private List<InformationBean> mList = new ArrayList<>();
    private List<String> favorMovie = new ArrayList<>();
    private List<String> favorMusic = new ArrayList<>();
    private InformationAdapter mAdapter;
    private Context mContext;
    private final static String TAG = MyInformationActivity.class.getSimpleName();
    private final static String EXTRA_RESTORE_PHOTO = "extra_restore_photo";
    //监听文本信息是否变化了
    private boolean isTextChange;
    //监听图片信息是否变化了
    private boolean isPhotoChange;
    //选取图片并裁剪
    private final static int REQUEST_PICKER_AND_CROP = 2;
    /**
     * 运行时权限申请码
     */
    private final static int RUNTIME_PERMISSION_REQUEST_CODE = 0x1;

    private CapturePhotoHelper mCapturePhotoHelper;
    private File mRestorePhotoFile;
    private File tempFile = new File(Environment.getExternalStorageDirectory(), SPUtils.getSharedStringData(AppApplication.getAppContext(),"userid")+".jpg");

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_information;
    }

    @Override
    public void initPresenter() {
        mPresenter = new InformationPresenterImpl(this);
    }

    @Override
    public void initView() {
        mContext = this;
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInforamtion();
                finish();
            }
        });
        mBar = getSupportActionBar();
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        mBar.setTitle("完善个人信息");
        setRecycleView();
        mPresenter.requestInformation();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    /**
     * 把数据上传到服务器进行保存
     */
    private void saveInforamtion() {

        if (isTextChange) {
            //上传文本信息
            String nickname = mList.get(1).getContent();
            String age = mList.get(2).getContent();
            String gender = mList.get(3).getContent();
            String movie = mList.get(4).getContent();
            String music = mList.get(5).getContent();
            String userid = SPUtils.getSharedStringData(AppApplication.getAppContext(), "userid");
            if (gender.equals("男")) {
                gender = "M";
            } else if (gender.equals("女")) {
                gender = "F";
            } else {
                gender = null;
            }
            if (age.equals("未设置")) {
                age = null;
            }
            if (nickname.equals("未设置")) {
                nickname = null;
            }
            if (movie.equals("未设置")) {
                movie = null;
            }
            if (music.equals("未设置")) {
                music = null;
            }
            mPresenter.requestUpdateInformation(userid, nickname, age, gender, movie, music);
            //在SharedPreference中更新信息
            SPUtils.setSharedStringData(AppApplication.getAppContext(), "nickname", nickname);
            SPUtils.setSharedStringData(AppApplication.getAppContext(), "age", age);
            if (gender == null) {
                SPUtils.setSharedStringData(AppApplication.getAppContext(), "gender", null);
            } else {
                if (gender.equals("M")) {
                    SPUtils.setSharedStringData(AppApplication.getAppContext(), "gender", "男");
                } else {
                    SPUtils.setSharedStringData(AppApplication.getAppContext(), "gender", "女");
                }
            }
            SPUtils.setSharedStringData(AppApplication.getAppContext(), "movie_preference", movie);
            SPUtils.setSharedStringData(AppApplication.getAppContext(), "music_preference", music);
        }
        if (isPhotoChange) {
            //上传图片信息
            String url = mList.get(0).getContent();
            if(url.equals("default")){
                SPUtils.setSharedStringData(AppApplication.getAppContext(), "photoUrl", null);
            }else{
                SPUtils.setSharedStringData(AppApplication.getAppContext(), "photoUrl", url);
                String userid = SPUtils.getSharedStringData(AppApplication.getAppContext(), "userid");
                mPresenter.requestUpdatePhoto(userid,url);
            }
        }
        if(isPhotoChange||isTextChange){
            //通知MainActivity更新UI
            LoginBean bean = new LoginBean();
            String url = mList.get(0).getContent();
            if(url.equals("default")){
                bean.setUser_photo_url(null);
            }else{
                bean.setUser_photo_url(url);
            }
            String nickname = mList.get(1).getContent();
            if(nickname.equals("未设置")){
                bean.setNickname(null);
            }else{
                bean.setNickname(nickname);
            }
            EventBus.getDefault().post(bean);
        }

    }

    private void setRecycleView() {
        mAdapter = new InformationAdapter(mContext, mList);
        mAdapter.setOnItemClickListenr(this);
        mIrvInformation.setLayoutManager(new LinearLayoutManager(mContext));
        mIrvInformation.setItemAnimator(new DefaultItemAnimator());
        mIrvInformation.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mIrvInformation.setIAdapter(mAdapter);
    }


    @Override
    public void returnInformation(List<InformationBean> informationBeanList) {
        mList.addAll(informationBeanList);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void returnUpdateInformationResult(boolean result) {
        if (result) {
            Toast.makeText(MyInformationActivity.this, "成功上传信息", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MyInformationActivity.this, "上传信息失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void returnUpdatePhotoResult(boolean result) {
        if (result) {
            Toast.makeText(MyInformationActivity.this, "成功上传头像", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MyInformationActivity.this, "上传头像失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 更新喜爱的电影/音乐事件的接收
     *
     * @param info 喜好信息
     */
    @Subscribe
    public void onFavorListEvent(FavorListBean info) {
        if (info.isMovie()) {
            List<String> list = info.getList();
            InformationBean bean = mList.get(4);
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                if (i == list.size() - 1) {
                    buffer.append(list.get(i));
                } else {
                    buffer.append(list.get(i) + "、");
                }
            }
            bean.setSet(true);
            bean.setContent(buffer.toString());
            mAdapter.notifyItemChanged(4);
            isTextChange = true;
        } else {
            List<String> list = info.getList();
            InformationBean bean = mList.get(5);
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                if (i == list.size() - 1) {
                    buffer.append(list.get(i));
                } else {
                    buffer.append(list.get(i) + "、");
                }
            }
            bean.setSet(true);
            bean.setContent(buffer.toString());
            mAdapter.notifyItemChanged(5);
            isTextChange = true;
        }

    }

    @Override
    public void onItemClick(int position) {
        if (position == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("选择图片");
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.item_photo_select, (ViewGroup) findViewById(R.id.ll_root), false);
            TextView mTv_take_photo = (TextView) view.findViewById(R.id.tv_take_photo);
            TextView mTv_select_photo = (TextView) view.findViewById(R.id.tv_select_photo);
            builder.setView(view, 100, 30, 100, 30);
            final AlertDialog dialog = builder.create();
            mTv_take_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Android M 处理Runtime Permission
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {//检查是否有写入SD卡的授权
                            Log.i(TAG, "granted permission!");
                            turnOnCamera();
                        } else {
                            Log.i(TAG, "denied permission!");
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MyInformationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                Log.i(TAG, "should show request permission rationale!");
                            }
                            requestPermission();
                        }
                    } else {
                        turnOnCamera();
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                    }

                }
            });
            mTv_select_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Android M 处理Runtime Permission
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {//检查是否有写入SD卡的授权
                            Log.i(TAG, "granted permission!");
                            turnOnAlbum();
                        } else {
                            Log.i(TAG, "denied permission!");
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MyInformationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                Log.i(TAG, "should show request permission rationale!");
                            }
                            requestPermission();
                        }
                    } else {
                        turnOnAlbum();
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        }
        if (position == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("修改用户名");
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.item_edit, (ViewGroup) findViewById(R.id.ll_root), false);
            final EditText editText = (EditText) view.findViewById(R.id.ed_content);
            editText.setText(mList.get(1).getContent());
            editText.setSelection(editText.getText().length());
            builder.setView(view, 100, 30, 100, 30);
            final AlertDialog dialog = builder.create();
            Button bt_positive = (Button) view.findViewById(R.id.bt_positive);
            Button bt_negative = (Button) view.findViewById(R.id.bt_negative);
            bt_positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InformationBean bean = mList.get(1);
                    if (!editText.getText().toString().isEmpty() && !editText.getText().toString().equals("未设置")) {
                        bean.setSet(true);
                        bean.setContent(editText.getText().toString());
                        mAdapter.notifyItemChanged(1);
                        isTextChange = true;
                    }
                    dialog.dismiss();
                }
            });
            bt_negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        if (position == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("修改年龄");
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.item_edit, (ViewGroup) findViewById(R.id.ll_root), false);
            final EditText editText = (EditText) view.findViewById(R.id.ed_content);
            editText.setText(mList.get(2).getContent());
            editText.setSelection(editText.getText().length());
            builder.setView(view, 100, 30, 100, 30);
            final AlertDialog dialog = builder.create();
            Button bt_positive = (Button) view.findViewById(R.id.bt_positive);
            Button bt_negative = (Button) view.findViewById(R.id.bt_negative);
            bt_positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InformationBean bean = mList.get(2);
                    if (!editText.getText().toString().isEmpty() && !editText.getText().toString().equals("未设置")) {
                        bean.setSet(true);
                        bean.setContent(editText.getText().toString());
                        mAdapter.notifyItemChanged(2);
                        isTextChange = true;
                    }
                    dialog.dismiss();
                }
            });
            bt_negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        if (position == 3) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("修改性别");
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.item_gender_select, (ViewGroup) findViewById(R.id.rg_root), false);
            final RadioButton rb_male = (RadioButton) view.findViewById(R.id.rb_male);
            final RadioButton rb_female = (RadioButton) view.findViewById(R.id.rb_female);
            if (mList.get(3).getContent().equals("男")) {
                rb_male.setChecked(true);
            } else if (mList.get(3).getContent().equals("女")) {
                rb_female.setChecked(true);
            }
            builder.setView(view, 100, 30, 100, 30);
            final AlertDialog dialog = builder.create();
            Button bt_positive = (Button) view.findViewById(R.id.bt_positive);
            Button bt_negative = (Button) view.findViewById(R.id.bt_negative);
            bt_positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InformationBean bean = mList.get(3);
                    if (rb_female.isChecked()) {
                        bean.setContent("女");
                        bean.setSet(true);
                    }
                    if (rb_male.isChecked()) {
                        bean.setContent("男");
                        bean.setSet(true);
                    }
                    mAdapter.notifyItemChanged(3);
                    isTextChange = true;
                    dialog.dismiss();
                }
            });
            bt_negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        if (position == 4) {
            if (mList.get(4).isSet()) {
                String content = mList.get(4).getContent();
                String[] movie = content.split("、");
                favorMovie.clear();
                for (int i = 0; i < movie.length; i++) {
                    favorMovie.add(movie[i]);
                }
            }
            Intent intent = new Intent(MyInformationActivity.this, FavorMovieActivity.class);
            intent.putStringArrayListExtra("select", (ArrayList<String>) favorMovie);
            startActivity(intent);
        }
        if (position == 5) {
            if (mList.get(5).isSet()) {
                String content = mList.get(5).getContent();
                String[] music = content.split("、");
                favorMusic.clear();
                for (int i = 0; i < music.length; i++) {
                    favorMusic.add(music[i]);
                }
            }
            Intent intent = new Intent(MyInformationActivity.this, FavorMusicActivity.class);
            intent.putStringArrayListExtra("select", (ArrayList<String>) favorMusic);
            startActivity(intent);
        }
    }

    /**
     * 开启相册
     */
    private void turnOnAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);

    }

    /**
     * 开启相机
     */
    private void turnOnCamera() {
        if (mCapturePhotoHelper == null) {
            mCapturePhotoHelper = new CapturePhotoHelper(this, FolderManager.getPhotoFolder());
        }
        mCapturePhotoHelper.capture();
    }

    /**
     * 申请写入sd卡的权限
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RUNTIME_PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //应用恢复时 获取拍照保存的文件目录
        if (mCapturePhotoHelper != null) {
            mRestorePhotoFile = (File) savedInstanceState.getSerializable(EXTRA_RESTORE_PHOTO);
            Log.i(TAG, "onRestoreInstanceState , mRestorePhotoFile: " + mRestorePhotoFile);
            mCapturePhotoHelper.setPhoto(mRestorePhotoFile);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //应用闪退时，保留拍照保存的文件目录
        if (mCapturePhotoHelper != null) {
            mRestorePhotoFile = mCapturePhotoHelper.getPhoto();
            Log.i(TAG, "onSaveInstanceState , mRestorePhotoFile: " + mRestorePhotoFile);
            if (mRestorePhotoFile != null) {
                outState.putSerializable(EXTRA_RESTORE_PHOTO, mRestorePhotoFile);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RUNTIME_PERMISSION_REQUEST_CODE) {
            for (int index = 0; index < permissions.length; index++) {
                String permission = permissions[index];
                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                    if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "onRequestPermissionsResult: permission is granted!");
                        turnOnCamera();

                    } else {
                        showMissingPermissionDialog();

                    }
                }
            }
        }
    }

    /**
     * 显示打开权限提示的对话框
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("帮助");
        builder.setMessage("当前权限被禁用，建议到设置界面开启权限!");

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MyInformationActivity.this, "启动相机失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                turnOnSettings();
            }
        });

        builder.show();
    }

    /**
     * 启动系统权限设置界面
     */
    private void turnOnSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    //拍照完成后 获取目标文件 跳转到裁剪页面
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CapturePhotoHelper.CAPTURE_PHOTO_REQUEST_CODE) {
            //获取拍照后图片路径
            File photoFile = mCapturePhotoHelper.getPhoto();

            if (photoFile != null) {
                if (resultCode == RESULT_OK) {
                    Uri uri = Uri.fromFile(photoFile);
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(uri, "image/*");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 300);
                    intent.putExtra("outputY", 300);
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    intent.putExtra("return-data", false);
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    intent.putExtra("noFaceDetection", true); // no face detection
                    intent = Intent.createChooser(intent, "裁剪图片");
                    startActivityForResult(intent, REQUEST_PICKER_AND_CROP);
                } else {
                    if (photoFile.exists()) {
                        photoFile.delete();
                    }
                }
            }

        } else if (requestCode == REQUEST_PICKER_AND_CROP) {
            File photoFile = mCapturePhotoHelper.getPhoto();
            //存放到相册
            BitmapUtils.displayToGallery(this, photoFile);
            //更新UI 显示图像
            InformationBean informationBean = mList.get(0);
            informationBean.setContent(photoFile.getAbsoluteFile().toString());
            informationBean.setSet(true);
            mAdapter.notifyItemChanged(0);
            isPhotoChange = true;

        } else if (requestCode == REQUEST_PICK_IMAGE) {
            //获取选择图片后图片路径

            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                intent.putExtra("return-data", false);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("noFaceDetection", true); // no face detection
                intent = Intent.createChooser(intent, "裁剪图片");
                startActivityForResult(intent, REQUEST_PICKER_AND_CROP_2);
            }
        } else if (requestCode == REQUEST_PICKER_AND_CROP_2) {

            //更新UI 显示图像
            InformationBean informationBean = mList.get(0);
            System.out.println("adapter中图片url:"+tempFile.getAbsolutePath().toString());
            informationBean.setContent(tempFile.getAbsoluteFile().toString());
            informationBean.setSet(true);
            mAdapter.notifyItemChanged(0);
            isPhotoChange = true;

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveInforamtion();
    }
}
