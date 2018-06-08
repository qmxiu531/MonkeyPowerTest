package autotest.gionee.automonkeypowertest.bean;

import java.util.ArrayList;

public class SelectAppParams {
    public ArrayList<String> appNames;
    public boolean[]         selectItem;

    public SelectAppParams(ArrayList<String> appNames, boolean[] selectItem) {
        this.appNames = appNames;
        this.selectItem = selectItem;
    }
}
