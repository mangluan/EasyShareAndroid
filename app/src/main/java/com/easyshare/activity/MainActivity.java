package com.easyshare.activity;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.easyshare.R;
import com.easyshare.base.RxjavaResponse;
import com.easyshare.fragment.main.ExploreFragment;
import com.easyshare.fragment.main.HomepageFragment;
import com.easyshare.fragment.main.MeFragment;
import com.easyshare.network.Constants;
import com.easyshare.network.RetrofitFactory;
import com.easyshare.utils.SharedPreferenceUtils;
import com.easyshare.utils.UserUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hjq.toast.ToastUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private Fragment[] mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_EasyShare_MainActivity);
        setContentView(R.layout.activity_main);
        // initialize view
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setUserInputEnabled(false);  // 拒绝翻页
        mViewPager.setOffscreenPageLimit(3);
        mBottomNavigationView = findViewById(R.id.nav_view);
        // initialize Fragment
        mFragments = new Fragment[]{
                HomepageFragment.newInstance(),
                ExploreFragment.newInstance(),
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
                case R.id.nav_me:
                    mViewPager.setCurrentItem(2);
                    return true;
                default:
                    return false;
            }
        });
        // TODO 临时直接进入第二页
        mViewPager.setCurrentItem(2);
    }
}