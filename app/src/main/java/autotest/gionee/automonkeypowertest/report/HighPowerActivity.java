package autotest.gionee.automonkeypowertest.report;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.bean.PowerInfoResult;
import autotest.gionee.automonkeypowertest.bean.ReportResultBean;
import autotest.gionee.automonkeypowertest.databinding.ActivityHighPowerBinding;

public class HighPowerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHighPowerBinding binding          = DataBindingUtil.setContentView(HighPowerActivity.this, R.layout.activity_high_power);
        HighPowerAdapter         highPowerAdapter = new HighPowerAdapter();
        binding.listViewHighPower.setAdapter(highPowerAdapter);
        ReportResultBean highPowerList = (ReportResultBean) getIntent().getSerializableExtra("highPowerList");
        highPowerAdapter.setData(highPowerList);
    }

    class HighPowerAdapter extends BaseAdapter {
        private ArrayList<PowerInfoResult> data = new ArrayList<>();

        public void setData(ReportResultBean data) {
            this.data = data.highPowerResults;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            {
                HighPowerHolder holder;
                if (view == null) {
                    holder = new HighPowerHolder();
                    view = View.inflate(getApplicationContext(), R.layout.high_power_listview_item, null);
                    holder.appName = (TextView) view.findViewById(R.id.highPowerAppName);
                    holder.power = (TextView) view.findViewById(R.id.highPowerPower);
                    holder.time = (TextView) view.findViewById(R.id.highPowerTime);
                    view.setTag(holder);
                } else {
                    holder = (HighPowerHolder) view.getTag();
                }
                PowerInfoResult result = data.get(position);
                holder.appName.setText(result.getName());
                holder.power.setText(result.getPower());
                holder.time.setText(result.getTime());
                return view;
            }
        }
    }

    class HighPowerHolder {
        public TextView appName;
        TextView power;
        public TextView time;
    }
}
