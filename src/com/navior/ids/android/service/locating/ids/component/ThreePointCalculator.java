/**
 * ==============================BEGIN_COPYRIGHT===============================
 * ===================NAVIOR CO.,LTD. PROPRIETARY INFORMATION==================
 * This software is supplied under the terms of a license agreement or
 * nondisclosure agreement with NAVIOR CO.,LTD. and may not be copied or
 * disclosed except in accordance with the terms of that agreement.
 * ==========Copyright (c) 2010 NAVIOR CO.,LTD. All Rights Reserved.===========
 * ===============================END_COPYRIGHT================================
 *
 * @author cs1
 * @date 13-10-21
 */
package com.navior.ids.android.service.locating.ids.component;

import com.navior.ids.android.service.locating.ids.data.PosDistance;
import com.navior.ids.android.service.locating.ids.data.RssiRecord;
import com.navior.ips.model.Location;
import com.navior.ips.model.POS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ThreePointCalculator extends Calculator {

  private final static double RSSI_ERROR_THRESHOLD = 15d;
  private final static int WEAKEST_RSSI = -200;

  @Override
  public List<RssiRecord> aggregate(Iterable<RssiRecord> rssiRecords, HashMap<String, POS> posHashMap) {

    Map< String, Collection<RssiRecord> > classifiedRecords = classify( rssiRecords );

    LinkedList<RssiRecord> result = new LinkedList<RssiRecord>();

    Set< String > keySet = classifiedRecords.keySet();
    for( String key : keySet ) {
        if(posHashMap == null || posHashMap.containsKey(key)) {
            result.add(aggregate(classifiedRecords.get(key)));
        }
    }

    Collections.sort( result, new Comparator<RssiRecord>() {
        /**
         * Warning!!!
         * This implementation disobeys the semantics of compare(). It generates a descending list.
         * @param o
         * @param o2
         * @return
         */
        @Override
        public int compare(RssiRecord o, RssiRecord o2) {
            return -(o.getRssi() - o2.getRssi());
        }
    } );
    return result;
  }

  private RssiRecord aggregate( Collection<RssiRecord> records ) {
    if (records.size() == 0) {
      return null;
    } else {
      RssiRecord maxDiff1 = null;
      RssiRecord maxDiff2 = null;
      double maxDiffRssi1 = -1d;
      double maxDiffRssi2 = -1d;
      // get original average
      double aveRssi = 0d;
      for (RssiRecord r : records) {
        aveRssi += r.getRssi();
      }
      aveRssi /= records.size();
      // find extraordinary records
      for (RssiRecord r : records) {
        double diff = Math.abs(r.getRssi() - aveRssi);
        if (diff > RSSI_ERROR_THRESHOLD) {
          if (diff > maxDiffRssi1) {
            maxDiffRssi2 = maxDiffRssi1;
            maxDiffRssi1 = diff;
            maxDiff2 = maxDiff1;
            maxDiff1 = r;
          } else if (diff > maxDiffRssi2) {
            maxDiff2 = r;
            maxDiffRssi2 = diff;
          }
        }
      }
      // get average, with extraordinary records ignored
      aveRssi = 0d;
      int count = 0;
      int minRSSI = Integer.MAX_VALUE;
      int maxRSSI = Integer.MIN_VALUE;
      for (RssiRecord r : records) {
        if (r != maxDiff1 && r != maxDiff2) {
          aveRssi += r.getRssi();
          count++;
          if (minRSSI > r.getRssi()) {
            minRSSI = r.getRssi();
          }
          if (maxRSSI < r.getRssi()) {
            maxRSSI = r.getRssi();
          }
        }
      }
      if (count == 0) {
        aveRssi = WEAKEST_RSSI;
      } else {
        aveRssi /= count;
      }

      Iterator<RssiRecord> iterator = records.iterator();

        RssiRecord result = new RssiRecord( iterator.next().getStarName(), (int)aveRssi );
        result.setError( (maxRSSI - minRSSI) );
        return result;
    }
  }

  @Override
  public Location calculate(List<RssiRecord> aggregatedRecords, Map<String, POS> posMap) {

    int setSize = aggregatedRecords.size();

    if (setSize == 0) {
      return null;
    }

    if (setSize == 1) {
      POS pos = posMap.get( aggregatedRecords.get( 0 ).getStarName() );
      if( pos == null ) {
          return null;
      }

      Location location = new Location();
      location.setX( pos.getX() );
      location.setY( pos.getY() );
      location.setError( 0f );
      return location;
    }

    else if (setSize == 2) {

      String starname1 = aggregatedRecords.get( 0 ).getStarName();
      PosDistance pd1 = new PosDistance();
      pd1.pos = posMap.get( starname1 );
        if( posMap.get( starname1 ) == null ) {
            return null;
        }
      pd1.distance = rssi2Distance( aggregatedRecords.get( 0 ).getRssi() );
      pd1.rssiError = aggregatedRecords.get( 0 ).getError();

      String starname2 = aggregatedRecords.get( 1 ).getStarName();
      PosDistance pd2 = new PosDistance();
      pd2.pos = posMap.get( starname2 );
        if( posMap.get( starname2 ) == null ) {
            return null;
        }
      pd2.distance = rssi2Distance( aggregatedRecords.get( 1 ).getRssi() );
        pd2.rssiError = aggregatedRecords.get( 1 ).getError();

      return getWeightLocation( pd1, pd2 );
    }

    else {
      String starname1 = aggregatedRecords.get( 0 ).getStarName();
      PosDistance pd1 = new PosDistance();
      pd1.pos = posMap.get( starname1 );
        if( posMap.get( starname1 ) == null ) {
            return null;
        }
      pd1.distance = rssi2Distance( aggregatedRecords.get( 0 ).getRssi() );
        pd1.rssiError = aggregatedRecords.get( 0 ).getError();

      String starname2 = aggregatedRecords.get( 1 ).getStarName();
      PosDistance pd2 = new PosDistance();
      pd2.pos = posMap.get( starname2 );
        if( posMap.get( starname2 ) == null ) {
            return null;
        }
      pd2.distance = rssi2Distance( aggregatedRecords.get( 1 ).getRssi() );
        pd2.rssiError = aggregatedRecords.get( 1 ).getError();

      String starname3 = aggregatedRecords.get( 2 ).getStarName();
      PosDistance pd3 = new PosDistance();
      pd3.pos = posMap.get( starname3 );
        if( posMap.get( starname3 ) == null ) {
            return null;
        }
      pd3.distance = rssi2Distance( aggregatedRecords.get( 2 ).getRssi() );
        pd3.rssiError = aggregatedRecords.get( 2 ).getError();

      Location location = calculateLocation( pd1, pd2, pd3 );

      double delta = 0.1f;
      Location newLocation = location;
      int times = 0;
      do {
        location = newLocation;
        double l1 = distance( location.getX(), location.getY(), pd1.pos.getX(), pd1.pos.getY() );
        double l2 = distance(location.getX(), location.getY(), pd2.pos.getX(), pd2.pos.getY());
        double l3 = distance(location.getX(), location.getY(), pd3.pos.getX(), pd3.pos.getY());
        double d1 = pd1.distance;
        double d2 = pd2.distance;
        double d3 = pd3.distance;
        double theta = Math.sqrt((l1 * l1 + l2 * l2 + l3 * l3) / (d1 * d1 + d2 * d2 + d3 * d3));
        pd1.distance = d1 * theta;
        pd2.distance = d2 * theta;
        pd3.distance = d3 * theta;
        newLocation = calculateLocation(pd1, pd2, pd3);
        times++;
      }
      while ( times < 10 && distance(location.getX(), location.getY(), newLocation.getX(), newLocation.getY()) > delta);
      return newLocation;
    }
  }

  private Location calculateLocation( PosDistance pd1, PosDistance pd2, PosDistance pd3 ) {

    Location location1 = intersect( pd1, pd2, pd3 );
    Location location2 = intersect( pd1, pd3, pd2 );
    Location location3 = intersect( pd2, pd3, pd1 );
    double x = (location1.getX() + location2.getX() + location3.getX()) / 3;
    double y = (location1.getY() + location2.getY() + location3.getY()) / 3;

      ArrayList<ReciprocalWeightValuePair> pairs = new ArrayList<ReciprocalWeightValuePair>();
      ReciprocalWeightValuePair pair1 = new ReciprocalWeightValuePair(pd1.distance, pd1.rssiError);
      ReciprocalWeightValuePair pair2 = new ReciprocalWeightValuePair(pd2.distance, pd2.rssiError);
      ReciprocalWeightValuePair pair3 = new ReciprocalWeightValuePair(pd3.distance, pd3.rssiError);
      pairs.add(pair1);
      pairs.add(pair2);
      pairs.add(pair3);
      double error = getWeightAverage(pairs);

    Location location = new Location();
    location.setX( (float) x);
    location.setY( (float) y);
    location.setError((float)error);


    return location;
  }

  private Location intersect( PosDistance pd1, PosDistance pd2, PosDistance pd3 ) {
    if ( doubleEquals( pd1.pos.getX(), pd2.pos.getX() )
        && doubleEquals( pd1.pos.getY(), pd2.pos.getY() ) ) {
      Location location = new Location();
      location.setX( pd1.pos.getX() );
      location.setY( pd1.pos.getY() );
        location.setError((float)Math.max(pd1.rssiError, pd2.rssiError));
      return location;
    }

    double d = distance( pd1.pos.getX(), pd1.pos.getY(), pd2.pos.getX(), pd2.pos.getY() );
    // 外离
    if (d > pd1.distance + pd2.distance) {
      return getWeightLocation(pd1, pd2);
    }
    // 内含
    else if (d < Math.abs(pd1.distance - pd2.distance)) {
      pd2.distance = -pd2.distance;
      Location location = getWeightLocation(pd1, pd2);
      pd2.distance = -pd2.distance;
      return location;
    }
    // 相交
    else {

      double[] cosValues = new double[2];
      double[] sinValues = new double[2];
      Location[] intersections = new Location[2];
      intersections[0] = new Location();
      intersections[1] = new Location();
      double a = 2f * pd1.distance * (pd1.pos.getX() - pd2.pos.getX());
      double b = 2f * pd1.distance * (pd1.pos.getY() - pd2.pos.getY());
      double c = pd2.distance * pd2.distance - pd1.distance * pd1.distance - distanceSquare(pd1.pos.getX(), pd1.pos.getY(), pd2.pos.getX(), pd2.pos.getY());
      double p = a * a + b * b;
      double q = -2f * a * c;
      double r = c * c - b * b;
      double delta = q * q - 4 * p * r;

      // 如果交点只有一个
      if (delta < 0 || doubleEquals(d, pd1.distance + pd2.distance)
          || doubleEquals(d, Math.abs(pd1.distance - pd2.distance))) {
        cosValues[0] = -q / p / 2f;
        sinValues[0] = Math.sqrt(1 - cosValues[0] * cosValues[0]);

        intersections[0].setX( (float)(pd1.distance * cosValues[0] + pd1.pos.getX()) );
        intersections[0].setY( (float)(pd1.distance * sinValues[0] + pd1.pos.getY()) );

        // 在这里验证解是否正确, 如果不正确, 则将纵坐标符号进行变换
        if (!doubleEquals(distanceSquare(intersections[0].getX(), intersections[0].getY(),
            pd2.pos.getX(), pd2.pos.getY()), pd2.distance * pd2.distance)) {
          intersections[0].setY( (float)(pd1.pos.getY() - pd1.distance * sinValues[0]) );
        }
        return intersections[0];
      }

      cosValues[0] = (Math.sqrt(delta) - q) / p / 2f;
      cosValues[1] = (-Math.sqrt(delta) - q) / p / 2f;
      sinValues[0] = Math.sqrt(1 - cosValues[0] * cosValues[0]);
      sinValues[1] = Math.sqrt(1 - cosValues[1] * cosValues[1]);

      intersections[0].setX( (float)(pd1.distance * cosValues[0] + pd1.pos.getX()) );
      intersections[1].setX( (float)(pd1.distance * cosValues[1] + pd1.pos.getX()) );
      intersections[0].setY( (float)(pd1.distance * sinValues[0] + pd1.pos.getY()) );
      intersections[1].setY( (float)(pd1.distance * sinValues[1] + pd1.pos.getY()) );

      // 验证解是否正确, 两个解都需要验证.
      if (!doubleEquals(distanceSquare(intersections[0].getX(),
          intersections[0].getY(),
          pd2.pos.getX(),
          pd2.pos.getY()), pd2.distance * pd2.distance)) {
        intersections[0].setY( (float)(pd1.pos.getY() - pd1.distance * sinValues[0]) );
      }
      if (!doubleEquals(distanceSquare(intersections[1].getX(),
          intersections[1].getY(),
          pd2.pos.getX(),
          pd2.pos.getY()), pd2.distance * pd2.distance)) {
        intersections[1].setY( (float)(pd1.pos.getY() - pd1.distance * sinValues[1]) );
      }
      // 如果求得的两个点坐标相同, 则必然其中一个点的纵坐标反号可以求得另一点坐标
      if (doubleEquals(intersections[0].getY(), intersections[1].getY())
          && doubleEquals(intersections[0].getX(), intersections[1].getX())) {
        if (intersections[0].getY() > 0) {
          intersections[1].setY(-intersections[1].getY());
        } else {
          intersections[0].setY(-intersections[0].getY());
        }
      }

      double D1 = distanceSquare(intersections[0].getX(), intersections[0].getY(),
          pd3.pos.getX(), pd3.pos.getY());
      double D2 = distanceSquare(intersections[1].getX(), intersections[1].getY(),
          pd3.pos.getX(), pd3.pos.getY());

      return intersections[D1 < D2 ? 0 : 1];
    }
  }

  private Location getWeightLocation( PosDistance pd1, PosDistance pd2 ) {
    Location location = new Location();
    double x = (pd1.pos.getX() * pd2.distance + pd2.pos.getX() * pd1.distance) / (pd1.distance + pd2.distance);
    double y = (pd1.pos.getY() * pd2.distance + pd2.pos.getY() * pd1.distance) / (pd1.distance + pd2.distance);
    double error = (pd1.rssiError * pd2.distance + pd2.rssiError * pd1.distance) / (pd1.distance + pd2.distance);
    location.setX( (float)x );
    location.setY( (float)y );
    location.setError( (float)error );
    return location;
  }

  private double distanceSquare(double x1, double y1, double x2, double y2) {
    return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
  }

  private double distance(double x1, double y1, double x2, double y2) {
    return Math.sqrt(distanceSquare(x1, y1, x2, y2));
  }

  private double rssi2Distance(int rssi) {
    return Math.pow(2, (-50 - rssi) / 6.0);
  }

  private boolean doubleEquals(double f1, double f2) {
    final double ZERO = 0.0001f;
    return Math.abs(f1 - f2) < ZERO;
  }
}
