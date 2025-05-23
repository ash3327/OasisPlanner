package com.aurora.oasisplanner.presentation.panels.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.util.styling.Resources;

public class PaddingItemDecoration extends RecyclerView.ItemDecoration {

    private int mPaddingPx;
    private int mPaddingEdgesPx;

    public PaddingItemDecoration(@DimenRes int padding, @DimenRes int paddingEdge) {
        mPaddingPx = (int) Resources.getDimension(padding);
        mPaddingEdgesPx = (int) Resources.getDimension(paddingEdge);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }
        int orientation = getOrientation(parent);
        final int itemCount = state.getItemCount();

        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        /** Horizontal */
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            /** All positions */
            left = mPaddingPx;
            right = mPaddingPx;

            /** First position */
            if (itemPosition == 0) {
                left = mPaddingEdgesPx;
            }
            /** Last position */
            if (itemCount > 0 && itemPosition == itemCount - 1) {
                right = mPaddingEdgesPx;
            }
        }
        /** Vertical */
        else {
            /** All positions */
            top = mPaddingPx;
            bottom = mPaddingPx;

            /** First position */
            if (itemPosition == 0) {
                top = mPaddingEdgesPx;
            }
            /** Last position */
            if (itemCount > 0 && itemPosition == itemCount - 1) {
                bottom = mPaddingEdgesPx;
            }
        }

        if (!isReverseLayout(parent)) {
            outRect.set(left, top, right, bottom);
        } else {
            outRect.set(right, bottom, left, top);
        }
    }

    private boolean isReverseLayout(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            return layoutManager.getReverseLayout();
        } else {
            throw new IllegalStateException("PaddingItemDecoration can only be used with a LinearLayoutManager.");
        }
    }

    private int getOrientation(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            return layoutManager.getOrientation();
        } else {
            throw new IllegalStateException("PaddingItemDecoration can only be used with a LinearLayoutManager.");
        }
    }
}
