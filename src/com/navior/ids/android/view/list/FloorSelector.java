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
 * @date 13-11-25
 */
package com.navior.ids.android.view.list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.navior.ids.android.R;
import com.navior.ids.android.data.Parameter;
import com.navior.ips.model.Floor;
import com.navior.ips.model.Mall;

import java.util.LinkedList;
import java.util.List;

public class FloorSelector extends FrameLayout {
  public static final int FLOOR_NUM_SIDE = 2;
  public static final int SHRINK = 1546;
  public static final int SCROLL_BY_VALUE = 1547;
  public static final int HIDE_TIME_MILLIS = 2000;

  private boolean isShrink;
  private ImageView background, pointer;
  private ListView floorListView;
  private int firstVisibleItem;
  private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case SHRINK:
          doShrink();
          break;
        case SCROLL_BY_VALUE:
          final int position = floorListView.getAdapter().getCount() - FLOOR_NUM_SIDE * 2 - 1 - msg.arg1;
          floorListView.post(new Runnable() {
            @Override
            public void run() {
              // scroll one pixel over
              floorListView.smoothScrollToPositionFromTop(position, -1);
            }
          });
          break;
      }
    }
  };

  public FloorSelector(Context context) {
    super(context);
    init(context);
  }

  public FloorSelector(Context context, AttributeSet attr) {
    super(context, attr);
    init(context);
  }

  public void setFloorList(List<Floor> floors) {
    final List<TextView> floorTextViews = new LinkedList<TextView>();
    final int height = getItemHeight();

    floorTextViews.add(getEmptyTextView(height));
    floorTextViews.add(getEmptyTextView(height));
    for (int i = floors.size() - 1; i >= 0; i--) {
      Floor floor = floors.get(i);
      TextView item = getEmptyTextView(height);
      item.setText(floor.getNm());
      item.setTag(floor);
      item.setGravity(Gravity.CENTER);
      floorTextViews.add(item);
    }
    floorTextViews.add(getEmptyTextView(height));
    floorTextViews.add(getEmptyTextView(height));

    floorListView.setAdapter(new BaseAdapter() {
      @Override
      public int getCount() {
        return floorTextViews.size();
      }

      @Override
      public TextView getItem(int position) {
        return floorTextViews.get(position);
      }

      @Override
      public long getItemId(int position) {
        return position;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position);
      }

      @Override
      public boolean isEnabled(int position) {
        return false;
      }
    });
    floorListView.setOnTouchListener(new OnTouchListener(){
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return isShrink;
      }
    });
    floorListView.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
          floorListView.smoothScrollToPosition(firstVisibleItem);
          Mall currentMall = Parameter.getInstance().getCurrentMall();
          int currentFloor = currentMall.getL().size() - 1 - firstVisibleItem;
          if (currentFloor != Parameter.getInstance().getCurrentFloorIndex()) {
            Parameter.getInstance().setCurrentFloorIndex(currentFloor);
            Parameter.getInstance().requestRender();
          }
          handler.sendEmptyMessageDelayed(SHRINK, HIDE_TIME_MILLIS);
        } else {
          doExpand();
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (FloorSelector.this.firstVisibleItem != firstVisibleItem) {
          FloorSelector.this.firstVisibleItem = firstVisibleItem;
          updateAlpha();
        }
      }
    });
    floorListView.post(new Runnable() {
      @Override
      public void run() {
        floorListView.setSelection(floorListView.getCount() - 1);
      }
    });
    updateAlpha();
    handler.sendEmptyMessageDelayed(SHRINK, HIDE_TIME_MILLIS);
  }

  public int getCurrentFloorIndex() {
    return firstVisibleItem + FLOOR_NUM_SIDE;
  }

  public void setFloorIndex(int index) {
    Message message = new Message();
    message.what = SCROLL_BY_VALUE;
    message.arg1 = index;
    handler.sendMessage(message);
  }

  private void init(Context context) {
    inflate(context, R.layout.floor_selector, this);

    floorListView = (ListView) findViewById(R.id.floor_selector_list);
    background = (ImageView) findViewById(R.id.floor_selector_background);
    pointer = (ImageView) findViewById(R.id.floor_selector_pointer);

    pointer.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        doExpand();
        return false;
      }
    });
  }

  private TextView getEmptyTextView(int height) {
    TextView empty = new TextView(getContext());
    empty.setHeight(height);
    empty.setFocusable(false);
    empty.setAlpha(0);
    return empty;
  }

  private void updateAlpha() {
    ListAdapter adapter = floorListView.getAdapter();
    float[] alphas = isShrink ? new float[]{0, 0, 1, 0, 0} : new float[]{0.1f, 0.5f, 1, 0.5f, 0.1f};
    for (int i = 0; i < alphas.length && i < adapter.getCount(); i++) {
      ((TextView) adapter.getItem(firstVisibleItem + i)).setAlpha(alphas[i]);
    }
  }

  private void doShrink() {
    isShrink = true;
    background.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
        getItemHeight(), Gravity.CENTER_VERTICAL));
    pointer.setImageDrawable(getDrawable(R.drawable.floor_selector_pointer_only));
    updateAlpha();
  }

  private void doExpand() {
    handler.removeMessages(SHRINK);
    isShrink = false;
    background.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL));
    pointer.setImageDrawable(getDrawable(R.drawable.floor_selector_pointer));
    updateAlpha();
  }

  private int getItemHeight() {
    return (int) getContext().getResources().getDimension(R.dimen.floor_selector_item_h);
  }

  private Drawable getDrawable(int resId) {
    return getResources().getDrawable(resId);
  }
}
