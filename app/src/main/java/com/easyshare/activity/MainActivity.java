package com.easyshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.easyshare.R;
import com.easyshare.fragment.main.ExploreParentFragment;
import com.easyshare.fragment.main.HomepageFragment;
import com.easyshare.fragment.main.InformationFragment;
import com.easyshare.fragment.main.MeFragment;
import com.easyshare.network.Constants;
import com.easyshare.utils.SharedPreferenceUtils;
import com.easyshare.utils.UserUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hjq.toast.ToastUtils;

import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.TranslationConfig;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private Fragment[] mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_EasyShare_MainActivity);
        if (debugActivity()) return;
        initApplication();
        setContentView(R.layout.activity_main);
        // initialize view
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setUserInputEnabled(false);  // 拒绝翻页
        mViewPager.setOffscreenPageLimit(3);
        mBottomNavigationView = findViewById(R.id.nav_view);
        // initialize Fragment
        mFragments = new Fragment[]{
                HomepageFragment.newInstance(),
                ExploreParentFragment.newInstance(),
                InformationFragment.newInstance(),
                MeFragment.newInstance()
        };
        // initialize pager
        mViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return mFragments[position];
            }

            @Override
            public int getItemCount() {
                return mFragments.length;
            }
        });
        // 监听换页
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mBottomNavigationView.setSelectedItemId(R.id.nav_homepage);
                        break;
                    case 1:
                        mBottomNavigationView.setSelectedItemId(R.id.nav_explore);
                        break;
                    case 2:
                        mBottomNavigationView.setSelectedItemId(R.id.nav_information);
                        break;
                    case 3:
                        mBottomNavigationView.setSelectedItemId(R.id.nav_me);
                        break;
                }
            }
        });
        // 监听nav点击
        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_homepage:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.nav_explore:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.nav_information:
                    mViewPager.setCurrentItem(2);
                    return true;
                case R.id.nav_me:
                    mViewPager.setCurrentItem(3);
                    return true;
                default:
                    return false;
            }
        });
        // 点击添加按钮
        findViewById(R.id.btn_main_add).setOnClickListener(v -> {
//            PublishImageTextActivity.startActivity(this);
//            PublishPhotoAlbumActivity.startActivity(this);
//            if(true) return;
            if (UserUtils.getsInstance().isLogin()) {
                if (UserUtils.getsInstance().getUserInfo().getLevel() == 0) {
                    // 直接打开发布图文页面
                    PublishImageTextActivity.startActivity(this);
                } else if (UserUtils.getsInstance().getUserInfo().getLevel() > 0) {
                    // 打开图文、图册发布选择
                    openSelectPopup();
                } else {
                    ToastUtils.show(R.string.error_user_banned);
                }
            } else { // 如果是没有登录的状态，点击则跳转到登录页面
                ToastUtils.show(R.string.error_login);
                LoginActivity.startActivity(this);
            }
        });
    }

    /**
     * 是否初始化过应用了
     */
    private boolean initApplication() {
        Boolean initApplication = SharedPreferenceUtils.getBoolean(this, Constants.IS_INITIALIZATION_APPLICATION, false);
        if (!initApplication) OnBoardingActivity.startActivity(this);
        return initApplication;
    }

    /**
     * 测试Activity方法
     */
    private boolean debugActivity() {
//        SelectClassificationActivity.startActivity(this);
        return false;
//        return true;
    }

    /**
     * 打开图文、图册发布选择
     */
    private void openSelectPopup() {
        QuickPopupBuilder.with(this)
                .contentView(R.layout.view_select_main_add)
                .config(new QuickPopupConfig()
                        .gravity(Gravity.BOTTOM)
                        .withShowAnimation(AnimationHelper.asAnimation().withTranslation(TranslationConfig.FROM_BOTTOM).toShow())
                        .withDismissAnimation(AnimationHelper.asAnimation().withTranslation(TranslationConfig.TO_BOTTOM).toDismiss())
                        .withClick(R.id.btn_publish_image_text, view -> { // 打开发布图文页面
                            PublishImageTextActivity.startActivity(this);
                        }, true)
                        .withClick(R.id.btn_publish_photo_album, view -> { // 打开发布图册页面
                            PublishPhotoAlbumActivity.startActivity(this);
                        }, true)
                        .withClick(R.id.btn_cancel, null, true)
                ).show();
    }

    /**
     * 跳转发现页面
     */
    public void onToExploreClick(){
        mViewPager.setCurrentItem(1);
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

}