package com.codingbingo.fastreader.view.readview;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codingbingo.fastreader.R;
import com.codingbingo.fastreader.utils.ScreenUtils;
import com.codingbingo.fastreader.view.readview.interfaces.OnControllerStatusChangeListener;

/**
 * Author: bingo
 * Email: codingbingo@gmail.com
 * By 2017/1/11.
 */

public class ReadController extends FrameLayout implements View.OnTouchListener, Animation.AnimationListener, View.OnClickListener {
    public static final int CONTROLLER_HIDE = 0;
    public static final int CONTROLLER_SHOW = 1;
    public static final int CONTROLLER_STYLE = -1;

    private Context mContext;
    private View statusBarBg;
    private LinearLayout controllerTopBar;
    private LinearLayout controllerBottomBar;
    private LinearLayout controllerStyle;
    //书籍目录
    private RelativeLayout mBookContents;
    //阅读模式：黑夜模式、白天
    private RelativeLayout mBookMode;
    //书籍样式
    private RelativeLayout mBookFonts;

    private ImageView backBtn;

    //阅读亮度
    private AppCompatSeekBar brightness;
    //文字大小
    private TextView fontSizeSmaller, fontSizeLarger;
    //阅读背景
    private RecyclerView readingBackground;

    private Animation topOutAnimation;
    private Animation topInAnimation;
    private Animation bottomOutAnimation;
    private Animation bottomInAnimation;

    private OnControllerStatusChangeListener onControllerStatusChangeListener;
    private OnClickListener onClickListener;
    private int currentStatus = CONTROLLER_HIDE;//当前controller页面显示状态

    private int statusBarHeight;

    public ReadController(Context context) {
        this(context, null, 0);
    }

    public ReadController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        //获取对应的高度
        statusBarHeight = ScreenUtils.getStatusBarHeight(context);

        init();
        initView();

        this.setOnTouchListener(this);
        currentStatus = CONTROLLER_HIDE;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int width = v.getWidth();
        int height = v.getHeight();

        if (currentStatus == CONTROLLER_SHOW) {
            hideController();
            currentStatus = CONTROLLER_HIDE;
        } else if (currentStatus == CONTROLLER_HIDE) {
            showController();
            currentStatus = CONTROLLER_SHOW;
        } else if (currentStatus == CONTROLLER_STYLE){
            //此时页面显示的页面样式的几个选项
            hideControllerStyle();
            currentStatus = CONTROLLER_HIDE;
        }

        //返回controller状态
        if (currentStatus == CONTROLLER_SHOW) {
            if (onControllerStatusChangeListener != null) {
                onControllerStatusChangeListener.onControllerStatusChange(true);
            }
        }
        return false;
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.read_controller, this);

        topOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.top_out_animation);
        topInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.top_in_animation);
        bottomInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.bottom_in_animation);
        bottomOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.bottom_out_animation);

        topOutAnimation.setFillAfter(true);
        topInAnimation.setFillAfter(true);
        bottomInAnimation.setFillAfter(true);
        bottomOutAnimation.setFillAfter(true);

        topOutAnimation.setAnimationListener(this);
        topInAnimation.setAnimationListener(this);
        bottomInAnimation.setAnimationListener(this);
        bottomOutAnimation.setAnimationListener(this);
    }

    private void initView() {
        statusBarBg = findViewById(R.id.status_bar_bg);
        statusBarBg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));//调整高度，让界面变得流畅点

        controllerTopBar = (LinearLayout) findViewById(R.id.controllerTopBar);
        controllerBottomBar = (LinearLayout) findViewById(R.id.controllerBottomBar);
        controllerStyle = (LinearLayout) findViewById(R.id.controllerStyle);

        backBtn = (ImageView) findViewById(R.id.backBtn);
        mBookFonts = (RelativeLayout) findViewById(R.id.book_fonts);
        mBookContents = (RelativeLayout) findViewById(R.id.book_contents);
        mBookMode = (RelativeLayout) findViewById(R.id.book_mode);

        brightness = (AppCompatSeekBar) findViewById(R.id.brightness);
        fontSizeSmaller = (TextView) findViewById(R.id.fontSizeSmaller);
        fontSizeLarger = (TextView) findViewById(R.id.fontSizeLarger);
        readingBackground = (RecyclerView) findViewById(R.id.readingBackground);

        //开始进来之后就要隐藏控制栏
        hideController();
    }

    private void hideController() {
        controllerTopBar.startAnimation(topOutAnimation);
        controllerBottomBar.startAnimation(bottomOutAnimation);
    }

    private void showController() {
        controllerTopBar.startAnimation(topInAnimation);
        controllerBottomBar.startAnimation(bottomInAnimation);
    }

    public void setOnControllerStatusChangeListener(OnControllerStatusChangeListener onControllerStatusChangeListener) {
        this.onControllerStatusChangeListener = onControllerStatusChangeListener;
    }

    public void setOnViewClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;

        backBtn.setOnClickListener(onClickListener);
        mBookContents.setOnClickListener(onClickListener);
        mBookFonts.setOnClickListener(this);
        mBookMode.setOnClickListener(onClickListener);
    }

    private void invalidClickListener(){
        backBtn.setOnClickListener(null);
        mBookContents.setOnClickListener(null);
        mBookFonts.setOnClickListener(null);
        mBookMode.setOnClickListener(null);
    }

    private void hideControllerStyle(){
        controllerStyle.setVisibility(GONE);

        if (onControllerStatusChangeListener != null) {
            onControllerStatusChangeListener.onControllerStatusChange(false);
        }
    }

    @Override
    public void onClick(View v) {
        currentStatus = CONTROLLER_STYLE;
        hideController();
        controllerStyle.setVisibility(VISIBLE);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (onControllerStatusChangeListener != null) {
            onControllerStatusChangeListener.onControllerStatusChange(
                    ((currentStatus == CONTROLLER_SHOW) || (currentStatus == CONTROLLER_STYLE)) ? true : false);
        }

        if (currentStatus == CONTROLLER_SHOW){
            setOnViewClickListener(onClickListener);
        } else{
            invalidClickListener();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
