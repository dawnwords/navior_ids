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
 * @date 13-11-20
 */
package com.navior.ids.android.view.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.navior.ids.android.R;
import com.navior.ips.model.City;

import java.util.LinkedList;
import java.util.List;

public class CityIndexListView extends FrameLayout {
  private TextView floatingTitle;
  private ListView listView;
  private LinkedList<LinkedList<City>> alphabetList;

  public CityIndexListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initAlphabetList();

    inflate(context, R.layout.index_list_view_city, this);

    listView = (ListView) findViewById(R.id.index_list_view_list);
    listView.setOnScrollListener(new AbsListView.OnScrollListener() {
      private int currentFirst = -1;

      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (currentFirst != firstVisibleItem) {
          currentFirst = firstVisibleItem;
          ListAdapter adapter = view.getAdapter();
          if (adapter != null) {
            ItemHolder holder = (ItemHolder) adapter.getItem(firstVisibleItem);
            floatingTitle.setText(holder.title);
          }
        }
      }
    });

    floatingTitle = (TextView) findViewById(R.id.index_list_view_index_floating);
  }

  private void initAlphabetList() {
    alphabetList = new LinkedList<LinkedList<City>>();
    for (int i = 0; i < 28; i++) {
      alphabetList.add(new LinkedList<City>());
    }
  }

  public void setList(List<City> topCities, List<City> cities, final OnCitySelectedListener listener) {
    alphabetList.get(0).addAll(topCities);
    for (City city : cities) {
      alphabetList.get(city.getAbbr().charAt(0) - 'A' + 1).add(city);
    }

    final BaseAdapter adapter = new BaseAdapter() {
      @Override
      public int getCount() {
        int count = 0;
        for (LinkedList<City> list : alphabetList) {
          int size = list.size();
          count += size == 0 ? 0 : size + 1;
        }
        return count;
      }

      @Override
      public ItemHolder getItem(int position) {
        int i = -1;
        for (LinkedList<City> list : alphabetList) {
          int size = list.size();
          if (size == 0) {
            i++;
            continue;
          }
          if (position <= size) {
            String title = (i < 0) ? getResources().getString(R.string.top_city) : "" + (char) ('A' + i);
            if (position == 0) {
              return new ItemHolder(title);
            } else {
              return new ItemHolder(list.get(position - 1), title);
            }
          } else {
            position -= size + 1;
          }
          i++;
        }
        return null;
      }

      @Override
      public long getItemId(int position) {
        return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        final ItemHolder holder = getItem(position);
        if (convertView == null) {
          convertView = inflate(getContext(), R.layout.index_list_view_city_item, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.index_list_view_item_text);
        textView.setText(holder.cityName);
        textView.setTag(holder);
        if (holder.type == ItemType.INDEX) {
          textView.setBackgroundColor(getResources().getColor(R.color.light_gray));
        } else {
          textView.setBackgroundResource(R.drawable.city_item);
          convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              listener.onCitySelected(holder.cityId);
            }
          });
        }


        return convertView;
      }
    };
    listView.setAdapter(adapter);
    floatingTitle.setText(((ItemHolder) adapter.getItem(0)).title);
  }

  private enum ItemType {
    INDEX, ITEM
  }

  private class ItemHolder {
    int cityId;
    String cityName;
    String title;
    ItemType type;

    public ItemHolder(City city, String title) {
      this.cityName = city.getName();
      this.cityId = city.getId();
      this.title = title;
      this.type = ItemType.ITEM;
    }

    public ItemHolder(String title) {
      this.cityName = title;
      this.title = title;
      this.type = ItemType.INDEX;
    }
  }

  public interface OnCitySelectedListener {
    void onCitySelected(int cityId);
  }
}
