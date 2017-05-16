package com.lvr.threerecom.ui.home;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.aspsine.irecyclerview.IRecyclerView;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.InformationAdapter;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.bean.FavorListBean;
import com.lvr.threerecom.bean.InformationBean;
import com.lvr.threerecom.ui.home.presenter.impl.InformationPresenterImpl;
import com.lvr.threerecom.ui.home.view.InformationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by lvr on 2017/5/15.
 */

public class MyInformationActivity extends BaseActivity implements InformationView,InformationAdapter.onItemClickListenr {

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
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

    }

    /**
     * 把数据上传到服务器进行保存
     */
    private void saveInforamtion() {

    }

    private void setRecycleView() {
        mAdapter = new InformationAdapter(mContext,mList);
        mAdapter.setOnItemClickListenr(this);
        mIrvInformation.setLayoutManager(new LinearLayoutManager(mContext));
        mIrvInformation.setItemAnimator(new DefaultItemAnimator());
        mIrvInformation.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        mIrvInformation.setIAdapter(mAdapter);
    }


    @Override
    public void returnInformation(List<InformationBean> informationBeanList) {
        mList.addAll(informationBeanList);

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 更新喜爱的电影/音乐事件的接收
     *
     * @param info 喜好信息
     */
    @Subscribe
    public void onFavorListEvent(FavorListBean info) {
        if(info.isMovie()){
            List<String> list = info.getList();
            InformationBean bean = mList.get(4);
            StringBuffer buffer = new StringBuffer();
            for(int i=0;i<list.size();i++){
                if(i==list.size()-1){
                    buffer.append(list.get(i));
                }else {
                    buffer.append(list.get(i)+"、");
                }
            }
            bean.setSet(true);
            bean.setContent(buffer.toString());
            mAdapter.notifyItemChanged(4);
        }else{
            List<String> list = info.getList();
            InformationBean bean = mList.get(5);
            StringBuffer buffer = new StringBuffer();
            for(int i=0;i<list.size();i++){
                if(i==list.size()-1){
                    buffer.append(list.get(i));
                }else {
                    buffer.append(list.get(i)+"、");
                }
            }
            bean.setSet(true);
            bean.setContent(buffer.toString());
            mAdapter.notifyItemChanged(5);
        }

    }

    @Override
    public void onItemClick(int position) {
        if(position==0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("选择图片");
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.item_photo_select, (ViewGroup) findViewById(R.id.ll_root),false);
            builder.setView(view,100,30,100,30);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if(position==1){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("修改用户名");
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.item_edit, (ViewGroup) findViewById(R.id.ll_root), false);
            final EditText editText = (EditText) view.findViewById(R.id.ed_content);
            editText.setText(mList.get(1).getContent());
            editText.setSelection(editText.getText().length());
            builder.setView(view,100,30,100,30);
            final AlertDialog dialog = builder.create();
            Button bt_positive = (Button) view.findViewById(R.id.bt_positive);
            Button bt_negative = (Button) view.findViewById(R.id.bt_negative);
            bt_positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InformationBean bean = mList.get(1);
                    if(!editText.getText().toString().isEmpty()&&!editText.getText().toString().equals("未设置")){
                        bean.setSet(true);
                        bean.setContent(editText.getText().toString());
                        mAdapter.notifyItemChanged(1);
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
        if(position==2){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("修改年龄");
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.item_edit, (ViewGroup) findViewById(R.id.ll_root), false);
            final EditText editText = (EditText) view.findViewById(R.id.ed_content);
            editText.setText(mList.get(2).getContent());
            editText.setSelection(editText.getText().length());
            builder.setView(view,100,30,100,30);
            final AlertDialog dialog = builder.create();
            Button bt_positive = (Button) view.findViewById(R.id.bt_positive);
            Button bt_negative = (Button) view.findViewById(R.id.bt_negative);
            bt_positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InformationBean bean = mList.get(2);
                    if(!editText.getText().toString().isEmpty()&&!editText.getText().toString().equals("未设置")){
                        bean.setSet(true);
                        bean.setContent(editText.getText().toString());
                        mAdapter.notifyItemChanged(2);
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
        if(position==3){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("修改性别");
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.item_gender_select, (ViewGroup) findViewById(R.id.rg_root), false);
            final RadioButton rb_male = (RadioButton) view.findViewById(R.id.rb_male);
            final RadioButton rb_female = (RadioButton) view.findViewById(R.id.rb_female);
            if(mList.get(3).getContent().equals("男")){
                rb_male.setChecked(true);
            }else if(mList.get(3).getContent().equals("女")){
                rb_female.setChecked(true);
            }
            builder.setView(view,100,30,100,30);
            final AlertDialog dialog = builder.create();
            Button bt_positive = (Button) view.findViewById(R.id.bt_positive);
            Button bt_negative = (Button) view.findViewById(R.id.bt_negative);
            bt_positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InformationBean bean = mList.get(3);
                    if(rb_female.isChecked()){
                        bean.setContent("女");
                        bean.setSet(true);
                    }
                    if(rb_male.isChecked()){
                        bean.setContent("男");
                        bean.setSet(true);
                    }
                    mAdapter.notifyItemChanged(3);
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
        if(position==4){
            if(mList.get(4).isSet()){
                String content = mList.get(4).getContent();
                String[] movie = content.split("、");
                favorMovie.clear();
                for(int i=0;i<movie.length;i++){
                    favorMovie.add(movie[i]);
                }
            }
            Intent intent = new Intent(MyInformationActivity.this, FavorMovieActivity.class);
            intent.putStringArrayListExtra("select", (ArrayList<String>) favorMovie);
            startActivity(intent);
        }
        if(position==5){
            if(mList.get(5).isSet()){
                String content = mList.get(5).getContent();
                String[] music = content.split("、");
                favorMusic.clear();
                for(int i=0;i<music.length;i++){
                    favorMusic.add(music[i]);
                }
            }
            Intent intent = new Intent(MyInformationActivity.this, FavorMusicActivity.class);
            intent.putStringArrayListExtra("select", (ArrayList<String>) favorMusic);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveInforamtion();
    }
}
