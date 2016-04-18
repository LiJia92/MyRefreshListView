package com.android.lovesixgod.myrefreshlistview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 自定义下拉刷新ListView
 * Created by Jaceli on 2016-04-18.
 */
public class RefreshListView extends ListView implements ListView.OnScrollListener {
    /**
     * 刷新四种状态
     */
    public final static int DONE = 0; // 刷新完成
    public final static int PULL_TO_REFRESH = 1; // 下拉刷新
    public final static int RELEASE_TO_REFRESH = 2; // 松开刷新
    public final static int REFRESHING = 3; // 正在刷新

    private static final int RATIO = 3; // 滑动的比例值
    private int state; // 当前的状态
    private boolean isRefreshable; // 是否可刷新
    private boolean isRecord; // 是否开始准备下拉刷新
    private int mFirstVisibleItem; // 当前第一个可见Item
    private LinearLayout headerView; // 头布局
    private TextView refreshText; // 刷新文字说明
    private RefreshFirstView firstView; // 下拉刷新
    private RefreshSecondView secondView; // 松开刷新
    private RefreshThirdView thirdView; // 正在刷新
    private AnimationDrawable secondAnim; // secondView对应的动画
    private AnimationDrawable thirdAnim; // thirdView对应的动画
    private int headerViewHeight; // 头布局的高度
    private float startY; // 起始Y坐标
    private float offsetY; // Y坐标上的偏移量
    private OnRefreshListener refreshListener; // 回调接口
    // 滑动类
//    private Scroller mScroller = null;

    public RefreshListView(Context context) {
        super(context);
        init(context);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        isRefreshable = true;
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setOnScrollListener(this);

//        mScroller = new Scroller(context);

        headerView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.list_header, this, false);
        refreshText = (TextView) headerView.findViewById(R.id.pull_to_refresh);
        firstView = (RefreshFirstView) headerView.findViewById(R.id.first_view);
        secondView = (RefreshSecondView) headerView.findViewById(R.id.second_view);
        thirdView = (RefreshThirdView) headerView.findViewById(R.id.third_view);
        secondAnim = (AnimationDrawable) secondView.getBackground();
        thirdAnim = (AnimationDrawable) thirdView.getBackground();

        measureView(headerView);
        addHeaderView(headerView);
        headerViewHeight = headerView.getMeasuredHeight();
        headerView.setPadding(0, -headerViewHeight, 0, 0); // 全局通过设置headerView的paddingTop来控制内部View的显示

        state = DONE;
        isRefreshable = false;
        isRecord = false;
    }

//    @Override
//    public void computeScroll() {
//        if (mScroller.computeScrollOffset()) {
//            setPaddingTop(mScroller.getCurrY());
//        }
//    }
//
//    private void setPaddingTop(int paddingTop) {
//        headerView.setPadding(0, paddingTop, 0, 0);
//        headerView.postInvalidate();
//    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isRefreshable) {//如果现在是可刷新状态   在setOnMeiTuanListener中设置为true
            switch (ev.getAction()) {
                //用户按下
                case MotionEvent.ACTION_DOWN:
                    //如果当前是在listview顶部并且没有记录y坐标
                    if (mFirstVisibleItem == 0 && !isRecord) {
                        //将isRecord置为true，说明现在已记录y坐标
                        isRecord = true;
                        //将当前y坐标赋值给startY起始y坐标
                        startY = ev.getY();
                    }
                    break;
                //用户滑动
                case MotionEvent.ACTION_MOVE:
                    //再次得到y坐标，用来和startY相减来计算offsetY位移值
                    float tempY = ev.getY();
                    //再起判断一下是否为listview顶部并且没有记录y坐标
                    if (mFirstVisibleItem == 0 && !isRecord) {
                        isRecord = true;
                        startY = tempY;
                    }
                    //如果当前状态不是正在刷新的状态，并且已经记录了y坐标
                    if (state != REFRESHING && isRecord) {
                        //计算y的偏移量
                        offsetY = tempY - startY;
                        //计算当前滑动的高度
                        float currentHeight = (-headerViewHeight + offsetY / 3);
                        //用当前滑动的高度和头部headerView的总高度进行比 计算出当前滑动的百分比 0到1
                        float currentProgress = 1 + currentHeight / headerViewHeight;
                        //如果当前百分比大于1了，将其设置为1，目的是让第一个状态的椭圆不再继续变大
                        if (currentProgress >= 1) {
                            currentProgress = 1;
                        }
                        //如果当前的状态是放开刷新，并且已经记录y坐标
                        if (state == RELEASE_TO_REFRESH && isRecord) {
                            setSelection(0);
                            //如果当前滑动的距离小于headerView的总高度
                            if (-headerViewHeight + offsetY / RATIO < 0) {
                                //将状态置为下拉刷新状态
                                state = PULL_TO_REFRESH;
                                //根据状态改变headerView，主要是更新动画和文字等信息
                                changeHeaderByState(state);
                                //如果当前y的位移值小于0，即为headerView隐藏了
                            } else if (offsetY <= 0) {
                                //将状态变为done
                                state = DONE;
                                //根据状态改变headerView，主要是更新动画和文字等信息
                                changeHeaderByState(state);
                            }
                        }
                        //如果当前状态为下拉刷新并且已经记录y坐标
                        if (state == PULL_TO_REFRESH && isRecord) {
                            setSelection(0);
                            //如果下拉距离大于等于headerView的总高度
                            if (-headerViewHeight + offsetY / RATIO >= 0) {
                                //将状态变为放开刷新
                                state = RELEASE_TO_REFRESH;
                                //根据状态改变headerView，主要是更新动画和文字等信息
                                changeHeaderByState(state);
                                //如果当前y的位移值小于0，即为headerView隐藏了
                            } else if (offsetY <= 0) {
                                //将状态变为done
                                state = DONE;
                                //根据状态改变headerView，主要是更新动画和文字等信息
                                changeHeaderByState(state);
                            }
                        }
                        //如果当前状态为done并且已经记录y坐标
                        if (state == DONE && isRecord) {
                            //如果位移值大于0
                            if (offsetY >= 0) {
                                //将状态改为下拉刷新状态
                                state = PULL_TO_REFRESH;
                            }
                        }
                        //如果为下拉刷新状态
                        if (state == PULL_TO_REFRESH) {
                            //则改变headerView的padding来实现下拉的效果
                            headerView.setPadding(0, (int) (-headerViewHeight + offsetY / RATIO), 0, 0);
                            //给第一个状态的View设置当前进度值
                            firstView.setCurrentProgress(currentProgress);
                            //重画
                            firstView.postInvalidate();
                        }
                        //如果为放开刷新状态
                        if (state == RELEASE_TO_REFRESH) {
                            //改变headerView的padding值
                            headerView.setPadding(0, (int) (-headerViewHeight + offsetY / RATIO), 0, 0);
                            //给第一个状态的View设置当前进度值
                            firstView.setCurrentProgress(currentProgress);
                            //重画
                            firstView.postInvalidate();
                        }
                    }
                    break;
                //当用户手指抬起时
                case MotionEvent.ACTION_UP:
                    //如果当前状态为下拉刷新状态
                    if (state == PULL_TO_REFRESH) {
                        //平滑的隐藏headerView
                        this.smoothScrollBy((int) (-headerViewHeight + offsetY / RATIO) + headerViewHeight, 500);
                        //根据状态改变headerView
                        changeHeaderByState(state);
                    }
                    //如果当前状态为放开刷新
                    if (state == RELEASE_TO_REFRESH) {
                        //平滑的滑到正好显示headerView
                        this.smoothScrollBy((int) (-headerViewHeight + offsetY / RATIO), 500);
                        //将当前状态设置为正在刷新
                        state = REFRESHING;
                        //回调接口的onRefresh方法
                        refreshListener.onRefresh();
                        //根据状态改变headerView
                        changeHeaderByState(state);
                    }
                    //这一套手势执行完，一定别忘了将记录y坐标的isRecord改为false，以便于下一次手势的执行
                    isRecord = false;
                    break;
            }

        }
        return super.onTouchEvent(ev);
    }

    /**
     * 根据state改变HeaderView
     *
     * @param state
     */
    private void changeHeaderByState(int state) {
        switch (state) {
            case DONE:
                headerView.setPadding(0, -headerViewHeight, 0, 0);
                firstView.setVisibility(VISIBLE);
                secondView.setVisibility(GONE);
                secondAnim.stop();
                thirdView.setVisibility(GONE);
                thirdAnim.stop();
                break;
            case PULL_TO_REFRESH:
                refreshText.setText("下拉刷新");
                firstView.setVisibility(View.VISIBLE);
                secondView.setVisibility(View.GONE);
                secondAnim.stop();
                thirdView.setVisibility(View.GONE);
                thirdAnim.stop();
                break;
            case RELEASE_TO_REFRESH:
                refreshText.setText("放开刷新");
                firstView.setVisibility(View.GONE);
                secondView.setVisibility(View.VISIBLE);
                secondAnim.start();
                thirdView.setVisibility(View.GONE);
                thirdAnim.stop();
                break;
            case REFRESHING:
                refreshText.setText("正在刷新");
                firstView.setVisibility(View.GONE);
                thirdView.setVisibility(View.VISIBLE);
                secondView.setVisibility(View.GONE);
                secondAnim.stop();
                thirdAnim.start();
                break;
            default:
                break;

        }
    }

    /**
     * 在init时View尚未绘制，为了获取HeaderView的高度，需要此方法提前绘制
     *
     * @param child
     */
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * 刷新结束后的回调，重置状态
     */
    public void setOnRefreshComplete() {
        state = DONE;
        changeHeaderByState(state);

//        mScroller.startScroll(0, 0, 0, -headerViewHeight, 500);
    }

    /**
     * 刷新回调接口
     */
    public interface OnRefreshListener {
        void onRefresh();
    }
}
