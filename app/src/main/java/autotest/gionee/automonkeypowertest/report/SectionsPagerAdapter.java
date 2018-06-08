package autotest.gionee.automonkeypowertest.report;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import autotest.gionee.automonkeypowertest.R;

/**
 * gionee
 * 2018/3/1
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {


    private final Context context;
    private       String  pkgName;
    private       long    selectBatch;

    SectionsPagerAdapter(FragmentManager fm, Context context, String pkgName, long selectBatch) {
        super(fm);
        this.context = context;
        this.pkgName = pkgName;
        this.selectBatch = selectBatch;
    }

    @Override
    public Fragment getItem(int position) {
        return PlaceholderFragment.newInstance(position + 1, pkgName, selectBatch);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.title_Front_Details);
            case 1:
                return context.getString(R.string.title_Back_Details);
        }
        return null;
    }
}
