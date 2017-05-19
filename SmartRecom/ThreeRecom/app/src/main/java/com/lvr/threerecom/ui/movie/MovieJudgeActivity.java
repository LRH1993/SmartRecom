package com.lvr.threerecom.ui.movie;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lvr.threerecom.R;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.ui.movie.presenter.Impl.MovieJudgePresenterImpl;
import com.lvr.threerecom.ui.movie.view.MovieJudgeView;

import butterknife.BindView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by lvr on 2017/5/19.
 */

public class MovieJudgeActivity extends BaseActivity implements MovieJudgeView {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_hint)
    TextView mTvHint;
    @BindView(R.id.rtb_rating)
    MaterialRatingBar mRtbRating;
    @BindView(R.id.btn_submit)
    Button mBtnSubmit;

    private int mId;
    private MovieJudgePresenterImpl mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_movie_judge;
    }

    @Override
    public void initPresenter() {
        mPresenter = new MovieJudgePresenterImpl(this);
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        mId = (int) intent.getExtras().get("id");
        mToolbar.setTitle("电影评分");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRtbRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (rating > 0 && rating <= 1) {
                    mTvHint.setText("很差");
                } else if (rating > 1 && rating <= 2) {
                    mTvHint.setText("较差");
                } else if (rating > 2 && rating <= 3) {
                    mTvHint.setText("还行");
                } else if (rating > 3 && rating <= 4) {
                    mTvHint.setText("推荐");
                } else if (rating > 4 && rating <= 5) {
                    mTvHint.setText("力荐");
                }
            }
        });
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = mRtbRating.getRating();
                if (rating == 0) {
                    Toast.makeText(MovieJudgeActivity.this, "请填写评分", Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: 2017/5/19 暂时写死用户id
                    mPresenter.requestRatingResult("1", mId, (int) rating);
                }

            }
        });
    }


    @Override
    public void returnJudegeResult(boolean result) {
        if (result) {
            Toast.makeText(MovieJudgeActivity.this, "评分成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MovieJudgeActivity.this, "评分失败", Toast.LENGTH_SHORT).show();
        }
    }


}
