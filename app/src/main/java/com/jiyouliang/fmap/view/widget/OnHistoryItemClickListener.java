package com.jiyouliang.fmap.view.widget;

import android.view.View;

/**
 * @author YouLiang.Ji
 * RecyclerView条目点击回调
 */
public interface OnHistoryItemClickListener {
    /**
     * 条目点击回调
     * @param v
     * @param position
     */
    void onHistoryItemClick(View v, int position);
}
