package com.jiyouliang.fmap.view.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class DrawView extends View {

    private static final String TAG = "DrawView";

    /**
     * 画笔颜色数组
     */
    private final int[] COLOR_ARRAY = {0xffEA4335, 0xff4285F4,
            0xffFBBC05, 0xff34A853, 0xff42BD17, 0xff90BD0E, 0xff18BD8D,
            0xff27BDAD, 0xff2098BD, 0xffA96FBD, 0xff86B9BD, 0xff3DBDA4};

    /**
     * 绘制画笔
     */
    private Paint mPaint = new Paint();
    /**
     * 历史路径
     */
    private List<DrawPath> mDrawMoveHistory = new ArrayList<>();
    /**
     * 用于生成随机数，随机取出颜色数组中的颜色
     */
    private Random random = new Random();

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(dip2px(getContext(), 5));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //多指触控需要使用 getActionMasked
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                //处理点击事件
                performClick();
                //重置所有 PointerId 为 -1
                clearTouchRecordStatus();
                //新增一个轨迹
                addNewPath(event);
                //重绘
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mDrawMoveHistory.size() > 0) {
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        //遍历当前屏幕上所有手指
                        int itemPointerId = event.getPointerId(i);//获取到这个手指的 ID
                        for (DrawPath itemPath : mDrawMoveHistory) {
                            //遍历绘制记录表，通过 ID 找到对应的记录
                            if (itemPointerId == itemPath.pointerId) {
                                int pointerIndex = event.findPointerIndex(itemPointerId);
                                //通过 pointerIndex 获取到此次滑动事件的所有历史轨迹
                                List<PointF> recordList = readPointList(event, pointerIndex);
                                if (!listEquals(recordList, itemPath.record.peek())) {
                                    //判断该 List 是否已存在，不存在则添加进去
                                    itemPath.record.push(recordList);
                                    addPath(recordList, itemPath.path);
                                }
                            }
                        }
                    }
                    invalidate();
                }
                return true;
            }
            case MotionEvent.ACTION_POINTER_UP:
                //屏幕上有一根指头抬起，但有别的指头未抬起时的事件
                int pointerId = event.getPointerId(event.getActionIndex());
                for (DrawPath item : mDrawMoveHistory) {
                    if (item.pointerId == pointerId) {
                        //该手指已绘制结束，将此 PointerId 重置为 -1
                        item.pointerId = -1;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //屏幕上已经有了手指，此时又有别的手指点击时事件
                addNewPath(event);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //最后一根手指抬起，重置所有 PointerId
                clearTouchRecordStatus();
                break;
            case MotionEvent.ACTION_CANCEL:
                //事件被取消
                clearTouchRecordStatus();
                break;
        }
        return true;
    }

    private void addNewPath(MotionEvent event) {
        int pointerId = event.getPointerId(event.getActionIndex());
        float x = event.getX(event.findPointerIndex(pointerId));
        float y = event.getY(event.findPointerIndex(pointerId));
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x, y);
        DrawPath drawPath = new DrawPath(pointerId, getPathColor(), path);
        List<PointF> pointList = new ArrayList<>();
        pointList.add(new PointF(x, y));
        pointList.add(new PointF(x, y));
        drawPath.record.push(pointList);
        mDrawMoveHistory.add(drawPath);
    }

    private List<PointF> readPointList(MotionEvent event, int pointerIndex) {
        List<PointF> list = new ArrayList<>();
        for (int j = 0; j < event.getHistorySize(); j++) {
            list.add(new PointF(event.getHistoricalX(pointerIndex, j), event.getHistoricalY(pointerIndex, j)));
        }
        return list;
    }

    /**
     * 判断两个列表中所有的数据是否相同
     */
    private boolean listEquals(List<PointF> lis1, List<PointF> list2) {
        if (lis1.equals(list2)) {
            return true;
        }
        if (lis1.size() != list2.size()) {
            return false;
        }
        if (lis1.isEmpty()) {
            return true;
        }
        for (int i = 0; i < lis1.size(); i++) {
            PointF point1 = lis1.get(i);
            PointF point2 = list2.get(i);
            if (!point1.equals(point2)) {
                return false;
            }
        }
        return true;
    }

    private void addPath(List<PointF> list, Path path) {
        for (PointF item : list) {
            path.lineTo(item.x, item.y);
        }
    }

    /**
     * 清除记录触摸事件的状态
     */
    private void clearTouchRecordStatus() {
        for (DrawPath item : mDrawMoveHistory) {
            item.pointerId = -1;
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawMoveHistory == null || mDrawMoveHistory.isEmpty()) {
            return;
        }
        for (DrawPath item : mDrawMoveHistory) {
            mPaint.setColor(item.drawColor);
            canvas.drawPath(item.path, mPaint);
        }
    }

    /**
     * 清空画布
     */
    public void clear() {
        mDrawMoveHistory.clear();
        invalidate();
    }

    /**
     * 获取绘制图案的 Bitmap
     */
    public Bitmap getDrawBitmap() {
        Bitmap bitmap;
        try {
            setDrawingCacheEnabled(true);
            buildDrawingCache();
            bitmap = Bitmap.createBitmap(getDrawingCache(), 0, 0, getMeasuredWidth(), getMeasuredHeight(), null, false);
        } finally {
            setDrawingCacheEnabled(false);
            destroyDrawingCache();
        }
        return bitmap;
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((double) dipValue * (double) scale + 0.5);
    }

    private int getPathColor() {
        return COLOR_ARRAY[random.nextInt(COLOR_ARRAY.length)];
    }

    private static class DrawPath {

        /**
         * 手指 ID，默认为 -1，手指离开后置位 -1
         */
        private int pointerId = -1;
        /**
         * 曲线颜色
         */
        private int drawColor;
        /**
         * 曲线路径
         */
        private Path path;
        /**
         * 轨迹列表，用于判断目标轨迹是否已添加进来
         */
        private Stack<List<PointF>> record;

        DrawPath(int pointerId, int drawColor, Path path) {
            this.pointerId = pointerId;
            this.drawColor = drawColor;
            this.path = path;
            record = new Stack<>();
        }

    }
}
