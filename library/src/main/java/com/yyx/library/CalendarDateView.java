package com.yyx.library;

import com.yyx.library.utils.ResUtil;
import ohos.agp.components.*;

import ohos.agp.utils.Color;
import ohos.app.Context;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Calendar sliding control
 */

public class CalendarDateView extends PageSlider implements PageSlider.PageChangedListener {
    //attributes
    private static final String CURRENT_DAY_COLOR = "current_day_color";
    private static final String DAY_COLOR = "day_color";
    private static final String SELECTED_DAY_COLOR = "selected_day_color";
    private static final String SELECTED_DAY_BG_COLOR = "selected_day_bg_color";
    private static final String TIP_COLOR = "tip_color";
    private static final String SHAPE_TYPE = "shape_type";


    private MonthDateView.ShapeType mBgShape = MonthDateView.ShapeType.circle;
    private Color mDayColor;
    private Color mSelectDayColor;
    private Color mSelectBGColor;
    private Color mCurrentColor;
    private Color mTipColor;

    private MonthDateView.DateClick onDateClickListener;
    HashMap<Integer, MonthDateView> views = new HashMap<>();
    private LinkedList<MonthDateView> cache = new LinkedList();

    private OnItemClickListener onItemClickListener;

    private int mCurrentPos = Integer.MAX_VALUE / 2;
    private Text mTvDate;
    private int mSelYear, mSelMonth, mSelDay;

    public CalendarDateView(Context context) {
        this(context, null);
    }

    public CalendarDateView(Context context, AttrSet attrs) {
        super(context, attrs);
        Color DEF_CURRENT_DAY_COLOR = ResUtil.getColor(getContext(), ResourceTable.Color_default_current_day_color);
        mCurrentColor = attrs.getAttr(CURRENT_DAY_COLOR).isPresent() ?
                attrs.getAttr(CURRENT_DAY_COLOR).get().getColorValue() :
                DEF_CURRENT_DAY_COLOR;

        Color DEF_DAY_COLOR = ResUtil.getColor(getContext(), ResourceTable.Color_default_day_color);
        mDayColor = attrs.getAttr(DAY_COLOR).isPresent() ?
                attrs.getAttr(DAY_COLOR).get().getColorValue() :
                DEF_DAY_COLOR;

        Color DEF_SEL_DAY_COLOR = ResUtil.getColor(getContext(), ResourceTable.Color_default_selected_day_color);
        mSelectDayColor = attrs.getAttr(SELECTED_DAY_COLOR).isPresent() ?
                attrs.getAttr(SELECTED_DAY_COLOR).get().getColorValue() :
                DEF_SEL_DAY_COLOR;

        Color DEF_SEL_BG_COLOR = ResUtil.getColor(getContext(), ResourceTable.Color_default_tip_color);
        mSelectBGColor = attrs.getAttr(SELECTED_DAY_BG_COLOR).isPresent() ?
                attrs.getAttr(SELECTED_DAY_BG_COLOR).get().getColorValue() :
                DEF_SEL_BG_COLOR;

        Color DEF_TIP_COLOR = ResUtil.getColor(getContext(), ResourceTable.Color_default_tip_color);
        mTipColor = attrs.getAttr(TIP_COLOR).isPresent() ?
                attrs.getAttr(TIP_COLOR).get().getColorValue() :
                DEF_TIP_COLOR;

        if (attrs.getAttr(SHAPE_TYPE).isPresent()) {
            mBgShape = MonthDateView.ShapeType.valueOf(attrs.getAttr(SHAPE_TYPE).get().getStringValue());
        }

        init();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setDateTextView(Text date) {
        this.mTvDate = date;
        mTvDate.setText(geSelectedDate());
    }

    private void init() {
        Calendar calendar = Calendar.getInstance();
        mSelYear = calendar.get(Calendar.YEAR);
        mSelMonth = calendar.get(Calendar.MONTH);
        mSelDay = calendar.get(Calendar.DATE);

        onDateClickListener = (year, month, day) -> {
            mSelYear = year;
            mSelMonth = month;
            mSelDay = day;
            if (mTvDate != null) {
                mTvDate.setText(geSelectedDate());
            }
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(mSelYear, mSelMonth, mSelDay);
            }
        };

        setProvider(new PageSliderProvider() {
            @Override
            public int getCount() {
                //for infinite scrolling
                return Integer.MAX_VALUE;
            }

            @Override
            public Object createPageInContainer(ComponentContainer componentContainer, int position) {
                MonthDateView view;
                if (!cache.isEmpty()) {
                    view = cache.removeFirst();
                } else {

                    view = new MonthDateView(componentContainer.getContext());
                    view.setmDayColor(mDayColor);
                    view.setmSelectBGColor(mSelectBGColor);
                    view.setmSelectDayColor(mSelectDayColor);
                    view.setmCurrentColor(mCurrentColor);
                    view.setmTipColor(mTipColor);
                    view.setSelectedBgShape(mBgShape);
                }
                view.setDateClick(onDateClickListener);
                view.setSelectDate(mSelYear, mSelMonth, mSelDay);
                componentContainer.addComponent(view);
                views.put(position, view);
                return view;
            }

            @Override
            public void destroyPageFromContainer(ComponentContainer componentContainer, int position, Object object) {
                componentContainer.removeComponent((Component) object);
                cache.addLast((MonthDateView) object);
                views.remove(position);
            }

            @Override
            public boolean isPageMatchToObject(Component component, Object object) {
                return component == object;
            }
        });

        //Set the initial position at 1/2 of Integer.MAXVALUE, because the calendar can slide forward and backward
        setCurrentPage(mCurrentPos);

        addPageChangedListener(this);

    }

    /**
     * Get the currently selected date
     *
     * @return
     */
    private String geSelectedDate() {
        StringBuilder sb = new StringBuilder();
        sb.append(mSelYear);
        sb.append("-");
        sb.append(mSelMonth + 1);
        sb.append("-");
        sb.append(mSelDay);
        return sb.toString();
    }

    /**
     * Next month
     *
     * @param view
     */
    private void nextMonth(MonthDateView view) {
        int year = mSelYear;
        int month = mSelMonth;
        int day = mSelDay;
        if (month == 11) {//若果是12月份，则变成1月份
            year = mSelYear + 1;
            month = 0;
        } else {
            month++;
        }
        if (day > DateUtils.getMonthDays(year, month)) {
            day = DateUtils.getMonthDays(year, month);
        }
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
        view.setSelectDate(mSelYear, mSelMonth, mSelDay);
    }

    /**
     * Previous Month
     *
     * @param view
     */
    private void previousMonth(MonthDateView view) {
        int year = mSelYear;
        int month = mSelMonth;
        int day = mSelDay;
        if (month == 0) {//若果是1月份，则变成12月份
            year = mSelYear - 1;
            month = 11;
        } else {
            month--;
        }
        if (day > DateUtils.getMonthDays(year, month)) {
            day = DateUtils.getMonthDays(year, month);
        }
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
        view.setSelectDate(mSelYear, mSelMonth, mSelDay);
    }


    @Override
    public void onPageSliding(int i, float v, int i1) {

    }

    @Override
    public void onPageSlideStateChanged(int i) {

    }

    @Override
    public void onPageChosen(int position) {
        MonthDateView view = views.get(position);
        if (position > mCurrentPos) {
            nextMonth(view);
        } else if (position < mCurrentPos) {
            previousMonth(view);
        }

        mCurrentPos = position;
        if (mTvDate != null) {
            mTvDate.setText(geSelectedDate());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int year, int month, int day);
    }

    public int getSelYear() {
        return mSelYear;
    }

    public void setSelYear(int mSelYear) {
        this.mSelYear = mSelYear;
    }

    public int getSelMonth() {
        return mSelMonth;
    }

    public void setSelMonth(int mSelMonth) {
        this.mSelMonth = mSelMonth;
    }

    public int getSelDay() {
        return mSelDay;
    }

    public void setSelDay(int mSelDay) {
        this.mSelDay = mSelDay;
    }

    /**
     * Set the selected date background shape
     *
     * @param mBgShape
     */
    public void setBgShape(MonthDateView.ShapeType mBgShape) {
        this.mBgShape = mBgShape;
    }

    /**
     * Set other date colors
     *
     * @param mDayColor
     */
    public void setDayColor(Color mDayColor) {
        this.mDayColor = mDayColor;
    }

    /**
     * Set the color of the selected date
     *
     * @param mSelectDayColor
     */
    public void setSelectDayColor(Color mSelectDayColor) {
        this.mSelectDayColor = mSelectDayColor;
    }

    /**
     * Set the background color of the selected date
     *
     * @param mSelectBGColor
     */
    public void setSelectBGColor(Color mSelectBGColor) {
        this.mSelectBGColor = mSelectBGColor;
    }

    /**
     * Set the color of today's date
     *
     * @param mCurrentColor
     */
    public void setCurrentColor(Color mCurrentColor) {
        this.mCurrentColor = mCurrentColor;
    }

    /**
     * Set cue point color
     *
     * @param mTipColor
     */
    public void setTipColor(Color mTipColor) {
        this.mTipColor = mTipColor;
    }
}
