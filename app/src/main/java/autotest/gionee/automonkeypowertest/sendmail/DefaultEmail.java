package autotest.gionee.automonkeypowertest.sendmail;

import java.util.ArrayList;


class DefaultEmail {
    ArrayList<String> address_List;
    ArrayList<String> ccAddress_List;

    DefaultEmail(ArrayList<String> address_List, ArrayList<String> ccAddress_List) {
        this.address_List = address_List;
        this.ccAddress_List = ccAddress_List;
    }

    public boolean isEmpty() {
        return address_List.isEmpty() && ccAddress_List.isEmpty();
    }
}
