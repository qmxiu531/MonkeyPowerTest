package autotest.gionee.automonkeypowertest.bean;


import java.util.ArrayList;

public class SelectApp {
    public ArrayList<String> selectApps = new ArrayList<>();

    public SelectApp() {

    }

    public SelectApp(ArrayList<String> selectApps) {
        this.selectApps = selectApps;
    }

    public boolean isEmpty() {
        return selectApps == null || selectApps.isEmpty();
    }

    public static SelectApp getDefault() {
        ArrayList<String> objects = new ArrayList<>();
        objects.add("记事本");
        return new SelectApp(objects);
    }
}
