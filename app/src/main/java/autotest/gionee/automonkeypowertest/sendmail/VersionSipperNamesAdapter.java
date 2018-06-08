package autotest.gionee.automonkeypowertest.sendmail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.R;


class VersionSipperNamesAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<VersionSipperNamesBean> mData;

    VersionSipperNamesAdapter(Context context) {
        this.context = context;
        this.mData = new ArrayList<>();
    }

    public ArrayList<VersionSipperNamesBean> getData() {
        return mData;
    }

    VersionSipperNamesBean getData(int index) {
        return mData.get(index);
    }

    void updateDate(int index, VersionSipperNamesBean bean) {
        mData.set(index, bean);
    }

    void updateData(ArrayList<VersionSipperNamesBean> data) {
        mData.clear();
        mData.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        VersionSipperNamesHolder holder;
        if (view == null) {
            holder = new VersionSipperNamesHolder();
            view = View.inflate(context, R.layout.item_versionnames_list, null);
            holder.number = (TextView) view.findViewById(R.id.number);
            holder.addressee = (TextView) view.findViewById(R.id.addressee);
            holder.isChecked = (CheckBox) view.findViewById(R.id.isChecked);
            view.setTag(holder);
        } else {
            holder = (VersionSipperNamesHolder) view.getTag();
        }
        VersionSipperNamesBean bean = mData.get(i);
        holder.number.setText((i + 1) + "");
        holder.addressee.setText(bean.remoteVersionName.version);
        holder.isChecked.setChecked(bean.isChecked);
        return view;
    }
}