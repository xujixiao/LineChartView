package cn.test.myapplication.view;

/**
 * Created by xujixiao on 2016/12/14.13:29
 * 邮箱：ji-xiao.xu@utsoft.cn
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.test.myapplication.R;

public class SingleLineChartView extends View {

    private static final int lineCount = 7;
    private String title = "自定义标题";

    private boolean noTitle;
    private int paintColor;

    private float marginLeft;// 左边距
    private float marginRight;// 右边距
    private float marginTop;// 上边距
    private float marginBottom;//下边距
    private float widthInterval;// 单位宽
    private float heightInterval;// 单位高
    private float viewHeight = 0;// 控件高度
    private float viewWidth = 0;// 控件宽度

    private List<Double> values;// 折线值
    private List<String> dates;// 日期
    private List<String> stringValues = new ArrayList<String>();//折线值String
    private double maxV;// 最大值
    private double minV;// 最小值

    private float valueInterval;// 每格像素的value值
    private List<String> numbers;// 纵坐标

    private int pointNum = 0;// 选中的是哪一个点
    private float textSize = 0;// 字体大小
    private float titleSize;
    private float xSize;
    private float ySize;
    private int fillCircleRadio = 14;// 实心圆的半径
    private int strokeCircleRadio = 16;//空心圆半径
    private int lineSize = 10;
    private int fillPointSize = 5;//实心圆
    private int strokePointSize = 10;//空心圆粗细

    private TextPaint uFontPaint;

    // 构造方法
    public SingleLineChartView(Context context) {
        this(context, null, 0);
    }

    public SingleLineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineChartView);
        if (typedArray != null) {
            paintColor = typedArray.getColor(R.styleable.LineChartView_paint_color, getResources().getColor(R.color.custom_color));
            String tempTitle = typedArray.getString(R.styleable.LineChartView_custom_title);
            if (!TextUtils.isEmpty(tempTitle)) {
                title = tempTitle;
            }
            typedArray.recycle();
        }
        init();
    }

    private Paint tablePaint;//表格
    private Paint linePaint;//折线
    private Paint xPaint;
    private Paint fillPaint;
    private Paint fillPaintWhite;
    private Paint paintStroke;
    private Paint paint;
    private Paint paintText;
    private Paint titlePaint;//标题

    private int skewingWidth;

    private int tipsX, tipsY;

    private void init() {
        tablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tablePaint.setColor(getResources().getColor(R.color.custom_color));
        tablePaint.setStrokeWidth(2);
        tablePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        linePaint = new Paint();
        linePaint.setColor(paintColor);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);

        xPaint = new Paint();
        xPaint.setColor(getResources().getColor(R.color.custom_color));
        xPaint.setStrokeWidth(1);
        xPaint.setAntiAlias(true);
        xPaint.setStyle(Paint.Style.FILL);

        fillPaint = new Paint();
        fillPaint.setColor(paintColor);
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);

        fillPaintWhite = new Paint();
        fillPaintWhite.setColor(Color.WHITE);
        fillPaintWhite.setAntiAlias(true);
        fillPaintWhite.setStyle(Paint.Style.FILL);

        paintStroke = new Paint();
        paintStroke.setColor(paintColor);
        paintStroke.setAntiAlias(true);
        paintStroke.setStyle(Paint.Style.STROKE);

        // 画圆角矩形
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);// 充满
        paint.setColor(paintColor);
        paint.setAntiAlias(true);// 设置画笔的锯齿效果

        // 画文字
        paintText = new Paint();
        paintText.setStyle(Paint.Style.FILL);// 充满
        paintText.setColor(Color.WHITE);

        titlePaint = new Paint();
        titlePaint.setColor(getResources().getColor(R.color.custom_color));
        titlePaint.setStrokeWidth(1);
        titlePaint.setAntiAlias(true);
        titlePaint.setStyle(Paint.Style.FILL);

        lineSize = dip2px(getContext(), 3f);//折线粗细
        fillCircleRadio = dip2px(getContext(), 4.5f);//实心圆半径
        strokeCircleRadio = dip2px(getContext(), 5.25f);//空心圆半径
        fillPointSize = dip2px(getContext(), 1.5f);//实心圆粗细
        strokePointSize = dip2px(getContext(), 3f);//空心圆粗细
        skewingWidth = dip2px(getContext(), 5f);
        tipsX = dip2px(getContext(), 7.7f);
        tipsY = dip2px(getContext(), 5.5f);

        uFontPaint = new TextPaint();
    }

    public static int dip2px(Context context, float dipValue) {
        if (context != null) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        }
        return 0;
    }

    public void initEmptyData() {
        List<Double> list = new ArrayList<>();
        List<String> date = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            list.add(0.0);
            date.add("--");
        }
        setData(list, date, false);
    }

    /**
     * 设置折线的值
     *
     * @param values 折线值
     * @param dates  日期（七天）
     */
    public void setData(List<Double> values, List<String> dates, boolean noTitle) {

        if (values == null || values.size() == 0 || dates == null || dates.size() == 0) {
            return;
        }

        // 保存传入的参数
        this.values = values;
        this.dates = dates;
        this.noTitle = noTitle;

        reInitData();

        // 重绘
        this.invalidate();
    }

    // 设置字体大小
    public void setTextSize(int textSize, int titleSize, int xSize, int ySize) {
        this.textSize = textSize;
        this.titleSize = titleSize;
        this.xSize = xSize;
        this.ySize = ySize;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        clearCanvas(canvas);// 清空画布
        if (values == null || values.size() == 0 || dates == null || dates.size() == 0) {
            return;
        }
        initData();
        if (!noTitle) {
            drawTitle(canvas);// 画"title"
        } else {
            marginTop = viewHeight / 8.5F;
            //marginTop = marginBottom;
        }

        drewTable(canvas);// 画表格虚线

        drawX(canvas);// 画日期
        drawY(canvas);// 画纵坐标数字

        drawDetail(canvas);// 画详情信息(折线,圆点,圆角矩形)

        setClickable(true);// 设置控件为可点击
    }

    private void reInitData() {
        maxV = Collections.max(values);
        minV = Collections.min(values);

        int tempSize = lineCount - 1;

        if (maxV == minV) {
            if (minV == 0) {
                maxV = tempSize;
                minV = 0;
            } else {
                maxV = maxV + tempSize * 2;
                minV = minV - tempSize;
            }
        }
        if (minV >= 0) {
            minV = 0;
        } else {
            minV = minV * 1.2;
        }

        if (maxV < 0) {
            maxV = 0;
        } else if (maxV == 0) {
            maxV = Math.abs(minV) / 4;
        } else {
            maxV = maxV * 1.2;
        }
        maxV = Math.ceil(maxV);
        minV = Math.floor(minV);

        if (maxV > 10) {
            double maxOffset = (maxV % tempSize != 0) ? (tempSize - maxV % tempSize) : 0;//最大值偏移量
            maxV = maxV + maxOffset;
        }

        double absMinv = Math.abs(minV);
        double minOffset = (absMinv % tempSize != 0) ? (tempSize - absMinv % tempSize) : 0;//最小值偏移量
        minV = minV - minOffset;
        stringValues.clear();
        //得到最长数,和最短数
        for (double d : values) {
            String str = String.valueOf(d);
            stringValues.add(str);
        }

        pointNum = (dates.size() - 1);

        // 计算出纵坐标显示的值
        numbers = new ArrayList<>();
        for (int i = 0; i < lineCount; i++) {
            double temp = (maxV - minV) * (tempSize - i) / tempSize + minV;
            numbers.add(String.format("%.1f", temp));
        }
    }

    private void initData() {

        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        // 计算出字体大小
        if (textSize == 0 || titleSize == 0 || xSize == 0 || ySize == 0) {
            textSize = viewWidth / 36;// 8.88
            titleSize = textSize;
            ySize = xSize = textSize * 2 / 3;
        }

        if (null == numbers || numbers.size() == 0) {
            return;
        }

        // 计算出左边距
        marginLeft = viewWidth / 30 + getTextWidth(ySize, numbers.get(0)) + strokeCircleRadio + strokePointSize;
        // 计算出右边距
        marginRight = viewWidth / 12;
        // 计算出上边距
        marginTop = getTextHeight(titleSize, title) * 4;
        //计算出下边距
        marginBottom = viewHeight / 10;

        // 计算出单位宽
        widthInterval = (viewWidth - marginLeft - marginRight) / (dates.size() - 1);
        // 计算出单位高
        heightInterval = (viewHeight - marginTop - marginBottom) / (lineCount - 1);

        // 计算出每格像素的value值
        valueInterval = ((float) (maxV - minV)) / ((lineCount - 1) * heightInterval);
    }

    // 清空画布
    private void clearCanvas(Canvas canvas) {
        canvas.drawARGB(0, 0, 0, 0);
    }

    // 画"title"
    private void drawTitle(Canvas canvas) {

        titlePaint.setTextSize(titleSize);

        float textHeight = getTextHeight(titleSize, title);
        textHeight = marginTop - textHeight * 5 / 2;
        canvas.drawText(title, getTextWidth(titleSize, "----"), textHeight, titlePaint);
    }

    // 画表格虚线
    private void drewTable(Canvas canvas) {

        // 横线
        final float horizontalEnd = marginLeft + (dates.size() - 1) * widthInterval;
        for (int i = 0; i < lineCount; i++) {
            Path path = new Path();
            float uY = marginTop + i * heightInterval;
            path.moveTo(marginLeft, uY);
            path.lineTo(horizontalEnd, uY);
            canvas.drawPath(path, tablePaint);
        }

        // 竖线
        final float verticalEnd = marginTop + (lineCount - 1) * heightInterval;
        for (int i = 0; i < dates.size(); i++) {
            Path path = new Path();
            float uX = marginLeft + i * widthInterval;
            path.moveTo(uX, marginTop);
            path.lineTo(uX, verticalEnd);
            canvas.drawPath(path, tablePaint);
        }
    }

    private void drawDetail(Canvas canvas) {
        if (values == null || values.size() == 0 || textSize == 0) {
            return;
        }

        drawFoldLine(canvas);// 画折线
        drawCircle(canvas);// 画圆圈
        drawRoundRect(canvas);// 画圆角矩形
    }

    // 画折线
    private void drawFoldLine(Canvas canvas) {

        linePaint.setStrokeWidth(lineSize);// 数据线宽度（粗细）

        Path pathFoldLine = new Path();
        for (int i = 0; i < dates.size(); i++) {
            float uY = marginTop + Float.parseFloat(String.valueOf(maxV - values.get(i))) / valueInterval;
            if (i == 0) {
                pathFoldLine.moveTo(marginLeft, uY);
            } else {
                pathFoldLine.lineTo(marginLeft + i * widthInterval, uY);
            }
        }
        canvas.drawPath(pathFoldLine, linePaint);
    }

    // 画圆圈
    private void drawCircle(Canvas canvas) {

        fillPaint.setStrokeWidth(fillPointSize);
        fillPaintWhite.setStrokeWidth(strokePointSize);
        paintStroke.setStrokeWidth(strokePointSize);

        for (int i = 0; i < dates.size(); i++) {
            float sPointWidth = marginLeft + i * widthInterval;
            float sPointHeight = marginTop + Float.parseFloat(String.valueOf(maxV - values.get(i))) / valueInterval;

            if (i != pointNum) {
                canvas.drawCircle(sPointWidth, sPointHeight, fillCircleRadio, fillPaint);
            } else {
                canvas.drawCircle(sPointWidth, sPointHeight, strokeCircleRadio, fillPaintWhite);
                canvas.drawCircle(sPointWidth, sPointHeight, strokeCircleRadio, paintStroke);
            }
        }
    }

    // 画圆角矩形
    private void drawRoundRect(Canvas canvas) {

        paintText.setTextSize(textSize);

        float textWidth = getTextWidth(textSize, stringValues.get(pointNum));
        float textHeight = getTextHeight(textSize, stringValues.get(pointNum));

        float left;
        float leftText;
        float right;

        float top;
        float bottom;

        float skewing = strokeCircleRadio + strokePointSize / 2;

        if (pointNum == 0) {
            left = marginLeft - skewing;
            right = marginLeft + textWidth + 2 * skewingWidth - skewing;
        } else if (pointNum == (dates.size() - 1)) {

            left = marginLeft + pointNum * widthInterval - textWidth - 2 * skewingWidth + skewing;
            right = marginLeft + pointNum * widthInterval + skewing;
        } else {

            left = marginLeft + pointNum * widthInterval - textWidth * 1 / 2 - skewingWidth;
            right = marginLeft + pointNum * widthInterval + textWidth * 1 / 2 + skewingWidth;
        }
        leftText = left + skewingWidth;

        String uValueOf = String.valueOf(maxV - values.get(pointNum));
        top = marginTop + Float.parseFloat(uValueOf) / valueInterval - textHeight * 7 / 2;
        bottom = marginTop + Float.parseFloat(uValueOf) / valueInterval - textHeight * 3 / 2;

        RectF oval3 = new RectF(left, top, right, bottom);// 设置个新的长方形

        canvas.drawRoundRect(oval3, tipsX, tipsY, paint);// 第二个参数是x半径，第三个参数是y半径

        canvas.drawText(stringValues.get(pointNum), leftText, top + textHeight * 3 / 2, paintText);
    }

    // 画日期(横坐标)
    private void drawX(Canvas canvas) {

        xPaint.setTextSize(xSize);

        float textHeight = getTextHeight(xSize, dates.get(0));
        float height = marginTop + (lineCount - 1) * heightInterval + textHeight;

        for (int i = 0; i < dates.size(); i++) {
            Path path = new Path();
            float uY = height + textHeight * 3 / 2;
            float textWidth = getTextWidth(xSize, dates.get(i)) / 2;
            path.moveTo(marginLeft + i * widthInterval - textWidth, uY);// 只用于移动移动画笔。
            path.lineTo(marginLeft + i * widthInterval + textWidth, uY);// 用于进行直线绘制。
            canvas.drawTextOnPath(dates.get(i), path, 0, 0, xPaint);
        }
    }

    // 画纵坐标数字
    private void drawY(Canvas canvas) {
        if (numbers == null || numbers.size() == 0 || ySize == 0) {
            return;
        }

        xPaint.setTextSize(ySize);

        float width = marginLeft - getTextWidth(ySize, numbers.get(0)) - strokeCircleRadio - strokePointSize;
        float height = marginTop + getTextHeight(ySize, numbers.get(0)) / 2;

        for (int i = 0; i < numbers.size(); i++) {
            canvas.drawText(numbers.get(i), width, i * heightInterval + height, xPaint);
        }
    }

    // 点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != values && values.size() != 0) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // 得到坐标
                float fx = event.getX();
                float fy = event.getY();

                for (int i = 0; i < values.size(); i++) {

                    float width = marginLeft + i * widthInterval;
                    float height = marginTop + Float.parseFloat(String.valueOf(maxV - values.get(i))) / valueInterval;

                    if (fx > (width - widthInterval / 2) && fx < (width + widthInterval / 2)) {
                        if (fy > (height - heightInterval) && fy < (height + heightInterval)) {
                            pointNum = i;
                            this.invalidate();
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private float getTextWidth(float Size, String text) {
        uFontPaint.setTextSize(Size);
        return uFontPaint.measureText(text);
    }

    private int getTextHeight(float Size, String text) {
        uFontPaint.setTextSize(Size);
        Rect bounds = new Rect();
        uFontPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.bottom + bounds.height();
    }
}

