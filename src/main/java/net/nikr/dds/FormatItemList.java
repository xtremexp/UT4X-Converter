package net.nikr.dds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormatItemList {
    private List<FormatItem> list;

    public FormatItemList() {
        this.list = new ArrayList<>();
    }

    public void addItem(String name, long mask, int bits) {
        if (mask != 0) {
            list.add(new FormatItem(name, mask, bits));
        }
    }

    public void sortItems() {
        Collections.sort(list);
    }

    @Override
    public String toString() {
        String s = "";
        for (FormatItem item : list) {
            s += item.toString();
        }
        return s;
    }
}

