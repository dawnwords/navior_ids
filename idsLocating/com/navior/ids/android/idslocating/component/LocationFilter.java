package com.navior.ids.android.idslocating.component;

import com.navior.ips.model.Location;

import java.util.LinkedList;

/**
 * Created by wangxiayang on 7/11/13.
 */
public abstract class LocationFilter {

    protected LinkedList<Location> internalList;

    protected LocationFilter() {
        this.internalList = new LinkedList<Location>();
    }

    abstract void input( Location location );

    abstract Location getOutput();
}
