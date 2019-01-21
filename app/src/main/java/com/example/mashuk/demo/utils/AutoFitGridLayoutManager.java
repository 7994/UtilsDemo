package com.example.mashuk.demo.utils;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by anupamchugh on 05/03/17.
 */

public class AutoFitGridLayoutManager extends GridLayoutManager {

    private int columnWidth;
    private boolean columnWidthChanged = true;
    private int numberOfColumns;

    public AutoFitGridLayoutManager(Context context, int columnWidth, int numberOfColumns) {
        super(context, numberOfColumns);
        this.numberOfColumns = numberOfColumns;
        setColumnWidth(columnWidth);
    }


    public void setColumnWidth(int newColumnWidth) {
        if (newColumnWidth > 0 && newColumnWidth != columnWidth) {
            columnWidth = newColumnWidth;
            columnWidthChanged = true;
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (columnWidthChanged && columnWidth > 0) {
            int totalSpace;
            if (getOrientation() == VERTICAL) {
                totalSpace = getWidth() - getPaddingRight() - getPaddingLeft();
            } else {
                totalSpace = getHeight() - getPaddingTop() - getPaddingBottom();
            }
            int spanCount = Math.max(numberOfColumns, totalSpace / columnWidth);
            setSpanCount(spanCount);
            columnWidthChanged = false;
        }
        super.onLayoutChildren(recycler, state);
    }
}