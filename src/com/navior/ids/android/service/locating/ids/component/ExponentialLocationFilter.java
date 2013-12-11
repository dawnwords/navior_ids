package com.navior.ids.android.service.locating.ids.component;

import com.navior.ips.model.Location;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wangxiayang on 7/11/13.
 */
public class ExponentialLocationFilter extends LocationFilter {

    private final static long MAX_SLOT_LENGTH = 2000l;
    private final static int MAX_TIME_SLOT_AMOUNT = 5;
    private final static double BASE_WEIGHT = Math.sqrt( 2 );

    @Override
    void input( Location location ) {
        internalList.add( location );
        updateInternalList();
    }

    @Override
    Location getOutput() {
        ArrayList<ArrayList< Location >> slots = new ArrayList<ArrayList<Location>>();
        for( int i = 0; i < MAX_TIME_SLOT_AMOUNT; i++ ) {
            slots.add( new ArrayList<Location>() );
        }
        long currentTime = System.currentTimeMillis();
        for( Location l : internalList ) {
            int slotIndex = MAX_TIME_SLOT_AMOUNT - (int)(( currentTime - l.getLocTime().getTime() ) / MAX_SLOT_LENGTH );
            if( slotIndex < 0 ) {
                continue;
            }
            slots.get( slotIndex ).add( l );
        }
        double totalWeight = 0d;
        double aveX = 0d;
        double aveY = 0d;
        for( int i = 0; i < MAX_TIME_SLOT_AMOUNT; i++ ) {
            double quantifier = Math.pow( BASE_WEIGHT, i );
            Location aveLocation = getArithmeticAverage( slots.get( i ) );
            if( aveLocation == null ) {
                continue;
            }
            else {
                totalWeight += quantifier;
                aveX += aveLocation.getX();
                aveY += aveLocation.getY();
            }
        }
        if( totalWeight != 0d ) {
            aveX /= totalWeight;
            aveY /= totalWeight;
        }
        Location result = new Location();
        result.setX( (float)aveX );
        result.setY( (float)aveY );
        result.setLocTime( new Date() );
        return result;
    }

    private Location getArithmeticAverage( ArrayList< Location > slot ) {
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
            if( currentTime - l.getLocTime().getTime() > ( MAX_TIME_SLOT_AMOUNT * MAX_SLOT_LENGTH ) ) {
                internalList.remove( l );
            }
            else {
                break;
            }
        }
    }
}
