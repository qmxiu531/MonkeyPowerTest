package autotest.gionee.automonkeypowertest.report;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper;
import autotest.gionee.automonkeypowertest.databinding.DetailsFragmentLayoutBinding;
import autotest.gionee.automonkeypowertest.util.Helper.AnimationHelper;
import autotest.gionee.automonkeypowertest.util.Helper.PowerHelper;
import autotest.gionee.automonkeypowertest.util.Util;
import autotest.gionee.automonkeypowertest.util.sqlite.DBManager;

public class PlaceholderFragment extends Fragment {
    private              String[]          itemName           = {"sumPower", "cpuPowerMah", "cameraPowerMah", "flashlightPowerMah",
            "gpsPowerMah", "mobileRadioPowerMah", "sensorPowerMah", "wakeLockPowerMah", "wifiPowerMah", "wholePowerMah", "recordTime"};
    private static final String            ARG_SECTION_NUMBER = "section_number";
    private              ArrayList<String> mData              = new ArrayList<>();
    private DetailsFragmentLayoutBinding binding;
    private DetailsAdapter               mDetailsAdapter;

    public static PlaceholderFragment newInstance(int sectionNumber, String pkgName, long selectBatch) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle              args     = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("pkgName", pkgName);
        args.putLong("selectBatch", selectBatch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.details_fragment_layout, container, false);
        mDetailsAdapter = new DetailsAdapter();
        binding.detailList.setAdapter(mDetailsAdapter);
        AnimationHelper.setUpAnimation(Util.getAllChild(binding.detailsRoot));
        Bundle arguments     = getArguments();
        String pkgName       = arguments.getString("pkgName");
        long   selectBatch   = arguments.getLong("selectBatch");
        int    sectionNumber = arguments.getInt(ARG_SECTION_NUMBER);
        new UpdateDetailsTask(pkgName).execute((int) selectBatch, sectionNumber);
        return binding.getRoot();
    }


    private class DetailsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            DetailsHolder holder;
            if (view == null) {
                holder = new DetailsHolder();
                view = View.inflate(getContext(), R.layout.item_details_list, null);
                holder.itemName = (TextView) view.findViewById(R.id.itemName);
                holder.value = (TextView) view.findViewById(R.id.value);
                view.setTag(holder);
            } else {
                holder = (DetailsHolder) view.getTag();
            }
            holder.itemName.setText(itemName[i]);
            holder.value.setText(mData.get(i));
            return view;
        }
    }


    private class DetailsHolder {
        TextView itemName;
        TextView value;
    }

    private class UpdateDetailsTask extends AsyncTask<Integer, Void, MyBatterySipper> {

        private String pkgName;

        UpdateDetailsTask(String pkgName) {
            this.pkgName = pkgName;
        }

        @Override
        protected MyBatterySipper doInBackground(Integer... params) {
            HtmlReportInfo.VersionSipper     versionSipper    = DBManager.getVersionSipper(params[0]);
            HashMap<String, MyBatterySipper> sipper_map_front = versionSipper.sipper_Map_Front;
            HashMap<String, MyBatterySipper> sipper_map_back  = versionSipper.sipper_Map_Back;
            switch (params[1]) {
                case 1:
                    return sipper_map_front.get(pkgName);

                case 2:
                    return sipper_map_back.get(pkgName);
                default:
                    break;
            }
            return sipper_map_front.get(pkgName);
        }

        @Override
        protected void onPostExecute(MyBatterySipper myBatterySipper) {
            super.onPostExecute(myBatterySipper);
            binding.appName.setText(myBatterySipper.appInfo.appName);
            binding.appVersion.setText(myBatterySipper.appInfo.appVersion);
            binding.packageName.setText(myBatterySipper.appInfo.packageName);
            mData.clear();
            mData.add(PowerHelper.setDoubleScale(myBatterySipper.sumPower) + "");
            mData.add(PowerHelper.setDoubleScale(myBatterySipper.cpuPowerMah) + "");
            mData.add(PowerHelper.setDoubleScale(myBatterySipper.cameraPowerMah) + "");
            mData.add(PowerHelper.setDoubleScale(myBatterySipper.flashlightPowerMah) + "");
            mData.add(PowerHelper.setDoubleScale(myBatterySipper.gpsPowerMah) + "");
            mData.add(PowerHelper.setDoubleScale(myBatterySipper.mobileRadioPowerMah) + "");
            mData.add(PowerHelper.setDoubleScale(myBatterySipper.sensorPowerMah) + "");
            mData.add(PowerHelper.setDoubleScale(myBatterySipper.wakeLockPowerMah) + "");
            mData.add(PowerHelper.setDoubleScale(myBatterySipper.wifiPowerMah) + "");
            mData.add(PowerHelper.setDoubleScale(myBatterySipper.wholePower) + "");
            mData.add(myBatterySipper.time);
            mDetailsAdapter.notifyDataSetChanged();
        }
    }
}
