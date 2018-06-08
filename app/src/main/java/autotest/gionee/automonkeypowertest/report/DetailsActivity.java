package autotest.gionee.automonkeypowertest.report;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.databinding.ActivityDetailsBinding;
import autotest.gionee.automonkeypowertest.util.DensityUtils;

public class DetailsActivity extends AppCompatActivity {
    private ActionBar              actionBar;
    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        setLogo();
        long   selectBatch = getIntent().getLongExtra("selectBatch", 1L);
        String pkgName     = getIntent().getStringExtra("selectPkgName");
        addPointToContainer();
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext(), pkgName, selectBatch);
        binding.container.addOnPageChangeListener(pageChangeListener);
        binding.container.setAdapter(mSectionsPagerAdapter);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int                         point  = DensityUtils.dp2px(DetailsActivity.this, 10) * 2;
            int                         place  = (int) (positionOffset * point + point * position + 0.5f);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.guidePointSelected.getLayoutParams();
            params.leftMargin = place;
            binding.guidePointSelected.setLayoutParams(params);
        }

        @Override
        public void onPageSelected(int position) {
            if (actionBar != null) {
                actionBar.setTitle(position == 0 ? getString(R.string.title_Front_Details) : getString(R.string.title_Back_Details));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setLogo() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_Front_Details));
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.logo);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }

    private void addPointToContainer() {
        for (int i = 0; i < 2; i++) {
            View point = new View(this);
            point.setBackgroundResource(R.drawable.point_normal);
            int                       px     = DensityUtils.dp2px(this, 10);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(px, px);
            if (i != 0) {
                params.leftMargin = DensityUtils.dp2px(this, 10);
            }
            binding.guidePointContainer.addView(point, params);
        }
    }

}
