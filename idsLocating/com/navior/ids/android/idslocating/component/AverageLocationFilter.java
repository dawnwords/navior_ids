package com.navior.ids.android.idslocating.component;

import com.navior.ips.model.Location;

import java.util.List;

/**
 * Created by wangxiayang on 17/12/13.
 */
public class AverageLocationFilter extends LocationFilter {
    private final static long MAX_LIFESPAN = 10000l;

    @Override
    void input(Location location) {
        internalList.add(location);
        updateInternalList();
    }

    @Override
    Location getOutput() {
        return getArithmeticAverage(internalList);
    }

    private Location getArithmeticAverage( List< Location > slot ) {
        if( slot.size() == 0 ) {
            return null;
        }

        double aveX = 0d;
        double aveY = 0d;
        for( Location l : slot ) {
            aveX += l.getX();
            aveY += l.getY();
        }
        aveX /= slot.size();
        aveY /= slot.size();
        Location result = new Location();
        result.setX( (float)aveX );
        result.setY( (float)aveY );
        return result;
    }

    private void updateInternalList() {
        long currentTime = System.currentTimeMillis();
        for( Location l : internalList ) {
            if( currentTime - l.getLocTime().getTime() > MAX_LIFESPAN ) {
                internalList.remove( l );
            }
            else {
                break;
            }
        }
    }
}
