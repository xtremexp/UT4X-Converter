package net.nikr.dds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

    public class FormatItem implements Comparable<FormatItem> {

        private final String name;
        private final Long mask;
        private final long bits;

    public FormatItem(String name, long mask, long bits) {
            this.name = name;
            this.mask = mask;
            this.bits = bits;
        }

        @Override
        public String toString(){
            return name+bits;
        }

        @Override
        public int compareTo(FormatItem o) {
            return o.mask.compareTo(mask);
        }



}

