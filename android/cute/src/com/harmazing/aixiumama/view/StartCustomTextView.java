package com.harmazing.aixiumama.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;

import android.graphics.Paint.FontMetrics;
import android.view.Gravity;

public class StartCustomTextView extends TextView {
    private Paint paint1 = new Paint();
    private float textSize;

    public StartCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textSize = super.getTextSize();
        paint1.setTextSize(textSize);
        int textColor = super.getTextColors().getColorForState(
                getDrawableState(), 0);
        paint1.setColor(textColor);
        paint1.setAntiAlias(true);
    }
    /**
     *  返回每行能显示字符串的集合
     * @param content 所有字符
     * @param p  用于测量每个字符的宽度
     * @param width   文本框有效的宽度 去掉左右边距
     * @return
     */

    private ArrayList<String> autoSplit(String content, Paint p, float width) {
        ArrayList<String> as = new ArrayList<String>();
        //字符口中 的个数
        int length = content.length();
        float textWidth = p.measureText(content);
        if (textWidth < width) {
            as.add(content);
            return as;
        }

        int start = 0, end = 1;
        while (start < length) {
            // 一定个数的文本宽度小于控件宽度
            // 且 个数加1个(防止越界)文本宽度大于控件宽度时
            if (p.measureText(content, start, (end + 1) > length ? length
                    : (end + 1)) >= width
                    && p.measureText(content, start, end) <= width) {

                as.add((String) content.subSequence(start, end));
                start = end;
            }
            // 不足一行的文本 最后一行处理
            if (end == length) {
                as.add((String) content.subSequence(start, end));
                break;
            }
            // 对换行符的识别
            if (content.charAt(end) == '\n') {
                as.add((String) content.subSequence(start, end));
                // end +1 去掉'\n'
                start = end + 1;
            }
            end += 1;
        }
        return as;
    }



    @Override
        protected void onDraw(Canvas canvas) {

        String txt = super.getText().toString();
        FontMetrics fm = paint1.getFontMetrics();
        float baseline = fm.descent - fm.ascent;
        float x = getPaddingLeft();
        float y = baseline; // 由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。
        // 一行的内容长度
        float lineLenth = getWidth() - getPaddingLeft() - getPaddingRight();
        ArrayList<String> texts = autoSplit(txt, paint1, lineLenth);

        for (String string : texts) {
   //         System.out.println("string-->" + string);
        }

        for (int i=0;i<texts.size();i++) {
            String text=texts.get(i);
            if (text == null) {
                continue;
            }
            // 左对齐
            if (getGravity() == getFormatGravity(Gravity.CENTER)) {
                float textLength = paint1.measureText(text, 0, text.length());
                canvas.drawText(text, x+(lineLenth - textLength)/2, y, paint1);

            }
            // 右对齐
            else if (getGravity() == getFormatGravity(Gravity.RIGHT)) {
                float textLength = paint1.measureText(text, 0, text.length());
                canvas.drawText(text, x+lineLenth - textLength, y, paint1);
            }
            //居中对齐
            else{
                canvas.drawText(text, x, y, paint1); // 坐标以控件左上角为原点
            }
            //最后一行不添加间距
            if(i!=texts.size()-1){
                y += baseline + fm.leading;}  // 添加字体行间距
        }

    }
    private int getFormatGravity(int gravity){
//		if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
//            gravity |= Gravity.START;
//        }
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
            gravity |= Gravity.TOP;
        }
        return gravity;
    }
}
