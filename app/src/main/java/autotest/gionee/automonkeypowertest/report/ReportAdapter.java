package autotest.gionee.automonkeypowertest.report;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.bean.ReportBean;
import autotest.gionee.automonkeypowertest.bean.ReportResultBean;
import autotest.gionee.automonkeypowertest.bean.ReportViewHolder;
import autotest.gionee.automonkeypowertest.bean.TestParams;

class ReportAdapter extends BaseAdapter {
    private Context                  context;
    private HashMap<String, Integer> mUids;
    private ArrayList<ReportBean>    mData;
    private TestParams               params;
    private ReportResultBean originData;

    ReportAdapter(Context context, HashMap<String, Integer> uids) {
        this.context = context;
        this.mUids = uids;
        mData = new ArrayList<>();
    }

    void updateData(ReportResultBean data) {
        this.originData = data;
        mData.clear();
        if (!data.reportBeans.isEmpty()) {
            mData.addAll(data.reportBeans);
        }
        params = data.params;
        this.notifyDataSetChanged();
    }

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
        ReportViewHolder holder;
        if (view == null) {
            holder = new ReportViewHolder();
            view = View.inflate(context, R.layout.item_report_list2, null);
            holder.pkgName = (TextView) view.findViewById(R.id.pkgName);
            holder.power_front = (TextView) view.findViewById(R.id.power_front);
            holder.power_front_avg = (TextView) view.findViewById(R.id.power_front_avg);
            holder.power_back = (TextView) view.findViewById(R.id.power_back);
            holder.power_back_avg = (TextView) view.findViewById(R.id.power_back_avg);
            holder.wholePower_front = (TextView) view.findViewById(R.id.batter_front);
            holder.wholePower_back = (TextView) view.findViewById(R.id.batter_back);
            holder.number = (TextView) view.findViewById(R.id.number);
            holder.appVersion = (TextView) view.findViewById(R.id.appVersion);
            holder.voltageAvg_front = (TextView) view.findViewById(R.id.voltageAvg_front);
            holder.voltageAvg_back = (TextView) view.findViewById(R.id.voltageAvg_back);
            holder.coverage_front = (TextView) view.findViewById(R.id.coverage_front);
            holder.coverage_back = (TextView) view.findViewById(R.id.coverage_back);
            holder.coverage_text = (TextView) view.findViewById(R.id.coverage_text);
            view.setTag(holder);
        } else {
            holder = (ReportViewHolder) view.getTag();
        }
        ReportBean reportBean = mData.get(i);
        holder.number.setText((i + 1) + "");
        holder.pkgName.setText(reportBean.appName + ":" + mUids.get(reportBean.appName));//测试用
        holder.power_front.setText(reportBean.power_front + "");
        holder.power_front_avg.setText(reportBean.power_front_avg + "");
        holder.power_back.setText(reportBean.power_back + "");
        holder.power_back_avg.setText(reportBean.power_back_avg + "");
        holder.wholePower_front.setText(reportBean.wholePower_front + "");
        holder.wholePower_back.setText(reportBean.wholePower_back + "");
        holder.voltageAvg_front.setText((float) reportBean.voltageBean_F.voltageAvg / 1000 + "");
        holder.voltageAvg_back.setText((float) reportBean.voltageBean_B.voltageAvg / 1000 + "");
        holder.appVersion.setText(reportBean.appVersion);
        if (reportBean.testType == R.id.traverseAppTest) {
            holder.coverage_text.setVisibility(View.VISIBLE);
            holder.coverage_front.setVisibility(View.VISIBLE);
            holder.coverage_back.setVisibility(View.VISIBLE);
            holder.coverage_front.setText(reportBean.coverage_front);
            holder.coverage_back.setText(reportBean.coverage_back);
        } else {
            holder.coverage_front.setVisibility(View.GONE);
            holder.coverage_back.setVisibility(View.GONE);
            holder.coverage_text.setVisibility(View.GONE);
        }
        return view;
    }

    public ArrayList<ReportBean> getData() {
        return mData;
    }

    public ReportResultBean getOriginData() {
        return originData;
    }
}
