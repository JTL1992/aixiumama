package com.harmazing.aixiumama.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.LogUtil;

public class TitleWidget extends RelativeLayout implements OnClickListener {

    private Context mContext;
    private View mView;
    private View left;
    private View center;
    private View right;
    private View title;
    private ImageView left_view;
    private TextView center_view;
    private TextView right_view;

    public TitleWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        findViews();
        initViews(attrs);
    }

    private void findViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.widget_title_bar, this);
        title = mView.findViewById(R.id.title_layout);
        left = mView.findViewById(R.id.left);
        center = mView.findViewById(R.id.center);
        right = mView.findViewById(R.id.right);

        left_view = (ImageView) mView.findViewById(R.id.left_view);
        center_view = (TextView) mView.findViewById(R.id.center_view);
        right_view = (TextView) mView.findViewById(R.id.right_view);
    }

    private Drawable leftBg;
    private Integer leftViewBg;
    private String leftViewStr;
    private String leftVisibility;

    private Drawable rightBg;
    private Integer rightViewBg;
    private String rightViewStr;
    private String rightVisibility;
    private Drawable bg_title;
    private String centerViewStr;
    private String centerVisibility;

    private void initViews(AttributeSet attrs) {
        //TypedArray是一个数组容器
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.WidgetTitleBar);
        leftBg = array.getDrawable(R.styleable.WidgetTitleBar_left_bg);
        leftViewBg = array.getResourceId(R.styleable.WidgetTitleBar_left_view_bg, R.drawable.title_back);
        leftViewStr = array.getString(R.styleable.WidgetTitleBar_left_view_text);
        leftVisibility = array.getString(R.styleable.WidgetTitleBar_left_visibility);
        bg_title = array.getDrawable(R.styleable.WidgetTitleBar_bg_title);
        setLeftBackground(leftBg);
        setLeftViewBackground(leftViewBg);
        setLeftViewText(leftViewStr);
        setLeftVisibility(leftVisibility);
        setTitleBackground(bg_title);
        rightBg = array.getDrawable(R.styleable.WidgetTitleBar_right_bg);
        rightViewBg = array.getResourceId(R.styleable.WidgetTitleBar_right_view_bg, R.color.title_bar_background);
        rightViewStr = array.getString(R.styleable.WidgetTitleBar_right_view_text);
        rightVisibility = array.getString(R.styleable.WidgetTitleBar_right_visibility);
        setRightBackground(rightBg);
        setRightViewBackground(rightViewBg);
        setRightButtonText(rightViewStr);
        setRightVisibility(rightVisibility);

        centerViewStr = array.getString(R.styleable.WidgetTitleBar_center_view_text);
        centerVisibility = array.getString(R.styleable.WidgetTitleBar_center_visibility);
        setCenterViewText(centerViewStr);
        setCenterVisibility(centerVisibility);

        left.setOnClickListener(this);
        right.setOnClickListener(this);
        center.setOnClickListener(this);

        array.recycle();
    }

    private void setLeftBackground(Drawable bg) {
        if (bg == null) {
            left.setBackgroundDrawable(getResources().getDrawable(R.drawable.pressed_transparent));
        } else {
            left.setBackgroundDrawable(bg);
        }
    }

    public void setLeftViewBackground(int resid) {
        left_view.setBackgroundResource(resid);
    }

    private void setLeftViewText(String str) {
        //left_view.setText(str);
    }

    public View getLeftView() {
        return left_view;
    }

    /**
     * @param visibility </br>
     * visibility =>"invisible"为不可见</br>
     * visibility =>"visible"为可见</br>
     */
    private void setLeftVisibility(String visibility) {
        if (visibility != null) {
            if (visibility.toString().equals("visible")) {
                left.setVisibility(View.VISIBLE);
            } else if (visibility.toString().equals("invisible")) {
                left.setVisibility(View.INVISIBLE);
            }
        } else if (leftViewBg != null || leftViewStr != null) {
            left.setVisibility(View.VISIBLE);
        } else {
            left.setVisibility(View.VISIBLE);
        }
    }
    private void setTitleBackground(Drawable bg){
        if (bg == null){
            title.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_topbg));
        }
        else{
            title.setBackgroundDrawable(bg);
        }
    }

    private void setRightBackground(Drawable bg) {
        if (bg == null) {
            right.setBackgroundDrawable(getResources().getDrawable(R.drawable.pressed_transparent));
        } else {
            right.setBackgroundDrawable(bg);
        }
    }

    public void setRightViewBackground(int resid) {
        right_view.setBackgroundResource(resid);
    }

    public void setRightButtonText(String str) {
        right_view.setText(str);
    }

    public View getRightView() {

        View v = findViewById(R.id.right_view);
        LogUtil.e("TitleWidget", "getRightView=>" + v);
        return v;
    }

    /**
     * @param visibility </br>
     * visibility =>"invisible"为不可见</br>
     * visibility =>"visible"为可见</br>
     */
    public void setRightVisibility(String visibility) {
        if (visibility != null) {
            if (visibility.toString().equals("visible")) {
                right.setVisibility(View.VISIBLE);
            } else if (visibility.toString().equals("invisible")) {
                right.setVisibility(View.INVISIBLE);
            }
//        } else if (rightViewBg != R.drawable.tran_bg || rightViewStr != null) {
//            right.setVisibility(View.VISIBLE);
        } else {
            right.setVisibility(View.INVISIBLE);
        }
    }

    public void setCenterViewText(CharSequence str) {
        center_view.setText(str);
    }

    public void setCenterViewText(Integer stringid) {
        center_view.setText(stringid);
    }

    public View getCenterView() {
        return center_view;
    }

    public View getLeftL() {
        return left;
    }

    public View getRightL() {
        return right;
    }
    public View getTitle(){
        return title;
    }

    /**
     * @param visibility </br>
     * visibility =>"invisible"为不可见</br>
     * visibility =>"visible"为可见</br>
     */
    public void setCenterVisibility(String visibility) {
        if (visibility != null) {
            if (visibility.toString().equals("visible")) {
                center.setVisibility(View.VISIBLE);
            } else if (visibility.toString().equals("invisible")) {
                center.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setOnTitleBarListener(OnTitleBarListener l) {
        this.onTitleBarListener = l;
    }

    public interface OnTitleBarListener {
       abstract void leftClick();

       abstract void rightClick();

        abstract void centerClick();
    }

    public OnTitleBarListener onTitleBarListener;

    @Override
    public void onClick(View v) {
        if (onTitleBarListener == null) return;
        int id = v.getId();

        if (id == R.id.left) {
            onTitleBarListener.leftClick();
        }
        else if (id == R.id.center) {
            onTitleBarListener.centerClick();
        }
        else if (id == R.id.right) {
            onTitleBarListener.rightClick();
        }
    }

}
