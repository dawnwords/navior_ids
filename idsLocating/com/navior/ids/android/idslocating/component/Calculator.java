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
package com.navior.ids.android.idslocating.component;

import com.navior.ids.android.idslocating.data.RssiRecord;
import com.navior.ips.model.Location;
import com.navior.ips.model.POS;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Calculator {

  protected Map<String, Collection<RssiRecord>> classify(Iterable<RssiRecord> rssiRecords) {
    HashMap<String, Collection<RssiRecord>> classifiedRecords = new HashMap<String, Collection<RssiRecord>>();
    // classify the records by star name
    for (RssiRecord record : rssiRecords) {
      String starName = record.getStarName();
      if (!classifiedRecords.containsKey(starName)) {
        LinkedList<RssiRecord> recordList = new LinkedList<RssiRecord>();
        recordList.add(record);
        classifiedRecords.put(starName, recordList);
      } else {
        classifiedRecords.get(starName).add(record);
      }
    }
    return classifiedRecords;
  }

  public abstract List<RssiRecord> aggregate(Iterable<RssiRecord> filteredRecords, HashMap<String, POS> posHashMap);

  public abstract Location calculate(List<RssiRecord> aggregatedRecords, Map<String, POS> posMap);

    protected class ReciprocalWeightValuePair {
        double reciprocalWeight; // if weight is x, this field should be 1/x
        double value;

        protected ReciprocalWeightValuePair(double reciprocalWeight, double value) {
            this.reciprocalWeight = reciprocalWeight;
            this.value = value;
        }
    }

    protected double getWeightAverage(List<ReciprocalWeightValuePair> list) {
        double result = 0f;
        double divider = 0f;
        double product = 1f;
        for (int i = 0; i < list.size(); i++) {
            product *= list.get(i).reciprocalWeight;
        }
        for (int i = 0; i < list.size(); i++) {
            double weight = product / list.get(i).reciprocalWeight;
            result += weight * list.get(i).value;
            divider += weight;
        }
        return result /= divider;
    }
}
