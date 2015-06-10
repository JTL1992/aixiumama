package com.harmazing.aixiumama.view;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.harmazing.aixiumama.utils.LogUtil;

/****
 * ????????????????????е?????? ????ImageView??????View??????.
 * onLayout??????????????????.?÷?????????View?е?layout????????У??????layout???????????????setFrame????.
 * layout??????
 * setFrame???????ж??????View?????仯???????仯??????????μ?l??t??r??b?????View??????????ж??????UI.
 * ???????ture.??б仯????false.
 *
 * invalidate????????????μ?????,
 *
 *
 * @author zhangjia
 *
 */
public class DragImageView extends ImageView {

    private Activity mActivity;

    private int screen_W, screen_H;// ???????????

    private int bitmap_W, bitmap_H;// ????????

    private int MAX_W, MAX_H, MIN_W, MIN_H;// ?????

    private int current_Top, current_Right, current_Bottom, current_Left;// ????????????????

    private int start_Top = -1, start_Right = -1, start_Bottom = -1,
            start_Left = -1;// ????????λ??.

    private int start_x, start_y, current_x, current_y;// ????λ??

    private float beforeLenght, afterLenght;// ?????????

    private float scale_temp;// ???????

    public float all_scale;
    /**
     * ?? NONE???? DRAG?????. ZOOM:????
     *
     * @author zhangjia
     *
     */
    private enum MODE {
        NONE, DRAG, ZOOM

    };

    private MODE mode = MODE.NONE;// ?????

    private boolean isControl_V = false;// ??????

    private boolean isControl_H = false;// ?????

    private ScaleAnimation scaleAnimation;// ???????

    private boolean isScaleAnim = false;// ???????

    private MyAsyncTask myAsyncTask;// ??????

    /** ?????? **/
    public DragImageView(Context context) {
        super(context);
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /** ????????? **/
    public void setScreen_W(int screen_W) {
        this.screen_W = screen_W;
    }

    /** ????????? **/
    public void setScreen_H(int screen_H) {
        this.screen_H = screen_H;
    }

    public DragImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /***
     * ?????????
     */
//	@Override
//	public void setImageBitmap(Bitmap bm) {
//		super.setImageBitmap(bm);
//		/** ???????? **/
//		bitmap_W = bm.getWidth();
//		bitmap_H = bm.getHeight();
//
//		MAX_W = bitmap_W * 3;
//		MAX_H = bitmap_H * 3;
//
//		MIN_W = bitmap_W / 2;
//		MIN_H = bitmap_H / 2;
//
//	}

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (start_Top == -1) {
            start_Top = top;
            start_Left = left;
            start_Bottom = bottom;
            start_Right = right;
        }

    }

    /***
     * touch ???
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /** ??????????? **/
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
                break;
            // ?????
            case MotionEvent.ACTION_POINTER_DOWN:
                onPointerDown(event);
                break;

            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                mode = MODE.NONE;
                break;

            // ??????
            case MotionEvent.ACTION_POINTER_UP:
                mode = MODE.NONE;
                /** ????????? **/
                if (isScaleAnim) {
                    doScaleAnim();
                }
                break;
        }

        return true;
    }

    /** ???? **/
    void onTouchDown(MotionEvent event) {
        mode = MODE.DRAG;

        current_x = (int) event.getRawX();
        current_y = (int) event.getRawY();

        start_x = (int) event.getX();
        start_y = current_y - this.getTop();

    }

    /** ??????? ???????С **/
    void onPointerDown(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            mode = MODE.ZOOM;
            beforeLenght = getDistance(event);// ???????????
        }
    }

    /** ???????? **/
    void onTouchMove(MotionEvent event) {
        int left = 0, top = 0, right = 0, bottom = 0;
        /** ??????? **/
        if (mode == MODE.DRAG) {

            /** ????????????ж?????????drag?????? **/

            /** ????????l??t,r ,b **/
            left = current_x - start_x;
            right = current_x + this.getWidth() - start_x;
            top = current_y - start_y;
            bottom = current_y - start_y + this.getHeight();




            /** ???????ж? **/
//			if (isControl_H) {
//				if (left >= 0) {
//					left = 0;
//					right = this.getWidth();
//				}
//				if (right <= screen_W) {
//					left = screen_W - this.getWidth();
//					right = screen_W;
//				}
//			} else {
//				left = this.getLeft();
//				right = this.getRight();
//			}
//			/** ????ж? **/
//			if (isControl_V) {
//				if (top >= 0) {
//					top = 0;
//					bottom = this.getHeight();
//				}
//
//				if (bottom <= screen_H) {
//					top = screen_H - this.getHeight();
//					bottom = screen_H;
//				}
//			} else {
//				top = this.getTop();
//				bottom = this.getBottom();
//			}
//			if (isControl_H || isControl_V)
                this.setPosition(left, top, right, bottom);

            current_x = (int) event.getRawX();
            current_y = (int) event.getRawY();

        }
        /** ???????? **/
        else if (mode == MODE.ZOOM) {

            afterLenght = getDistance(event);// ???????????

            float gapLenght = afterLenght - beforeLenght;// ?仯?????

            if (Math.abs(gapLenght) > 5f) {
                scale_temp = afterLenght / beforeLenght;// ???????????
                if(scale_temp < 1){
//                    if(scale_temp < all_scale)
                    all_scale = 1;
                }else {
////                    if(all_scale > 1)
//                        all_scale += scale_temp;
////                    else {
////                        all_scale = 0;
                    all_scale += scale_temp;
////                    }
                }
//                LogUtil.v("scale_temp",scale_temp);
//                LogUtil.v("all_scale",all_scale);
                this.setScale(scale_temp);

                beforeLenght = afterLenght;
            }
        }

    }

    public void setScale_temp( ){

        this.setFrame(current_Left, current_Top, current_Right,
                current_Bottom);
    }



    /** ??????????? **/
    float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return FloatMath.sqrt(x * x + y * y);
    }

    /** ????????? **/
    private void setPosition(int left, int top, int right, int bottom) {
        this.layout(left, top, right, bottom);
    }


    /** ???????? **/
    void setScale(float scale) {
        int disX = (int) (this.getWidth() * Math.abs(1 - scale)) / 4;// ?????????????
        int disY = (int) (this.getHeight() * Math.abs(1 - scale)) / 4;// ?????????????
        LogUtil.v("scale",scale+"");
        // ???
        if (scale > 1) {
//            all_scale += scale;
            current_Left = this.getLeft() - disX;
            current_Top = this.getTop() - disY;
            current_Right = this.getRight() + disX;
            current_Bottom = this.getBottom() + disY;

            this.setFrame(current_Left, current_Top, current_Right,
                    current_Bottom);
            /***
             * ??????????????????????????ж????????
             */
//			if (current_Top <= 0 && current_Bottom >= screen_H) {
//		//		Log.e("jj", "??????=" + this.getHeight());
//				isControl_V = true;// ??????????
//			} else {
//				isControl_V = false;
//			}
//			if (current_Left <= 0 && current_Right >= screen_W) {
//				isControl_H = true;// ?????????
//			} else {
//				isControl_H = false;
//			}
//            if (current_Top <= 0 && current_Bottom >= screen_H) {
//		//		Log.e("jj", "??????=" + this.getHeight());
//				isControl_V = true;// ??????????
//			} else {
//				isControl_V = false;
//			}
        }

        else if (scale < 1 && this.getWidth() >= MIN_W) {
//            all_scale -= scale;
            current_Left = this.getLeft() + disX;
            current_Top = this.getTop() + disY;
            current_Right = this.getRight() - disX;
            current_Bottom = this.getBottom() - disY;
            /***
             * ??????????????????
             */
            // ??????
            if (isControl_V && current_Top > 0) {
                current_Top = 0;
                current_Bottom = this.getBottom() - 2 * disY;
                if (current_Bottom < screen_H) {
                    current_Bottom = screen_H;
                    isControl_V = false;// ?????????
                }
            }
            // ?±????
            if (isControl_V && current_Bottom < screen_H) {
                current_Bottom = screen_H;
                current_Top = this.getTop() + 2 * disY;
                if (current_Top > 0) {
                    current_Top = 0;
                    isControl_V = false;// ?????????
                }
            }

            // ??????
            if (isControl_H && current_Left >= 0) {
                current_Left = 0;
                current_Right = this.getRight() - 2 * disX;
                if (current_Right <= screen_W) {
                    current_Right = screen_W;
                    isControl_H = false;// ???
                }
            }
            // ??????
            if (isControl_H && current_Right <= screen_W) {
                current_Right = screen_W;
                current_Left = this.getLeft() + 2 * disX;
                if (current_Left >= 0) {
                    current_Left = 0;
                    isControl_H = false;// ???
                }
            }

            if (isControl_H || isControl_V) {
                this.setFrame(current_Left, current_Top, current_Right,
                        current_Bottom);
            } else {
                this.setFrame(current_Left, current_Top, current_Right,
                        current_Bottom);
                isScaleAnim = true;// ???????????
            }

        }

    }

    /***
     * ???????????
     */
    public void doScaleAnim() {
        myAsyncTask = new MyAsyncTask(screen_W, this.getWidth(),
                this.getHeight());
        myAsyncTask.setLTRB(this.getLeft(), this.getTop(), this.getRight(),
                this.getBottom());
        myAsyncTask.execute();
        isScaleAnim = false;// ??????
    }

    /***
     * ????????????
     */
    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        private int screen_W, current_Width, current_Height;

        private int left, top, right, bottom;

        private float scale_WH;// ???????

        /** ?????λ?????? **/
        public void setLTRB(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        private float STEP = 8f;// ????

        private float step_H, step_V;// ???????????????

        public MyAsyncTask(int screen_W, int current_Width, int current_Height) {
            super();
            this.screen_W = screen_W;
            this.current_Width = current_Width;
            this.current_Height = current_Height;
            scale_WH = (float) current_Height / current_Width;
            step_H = STEP;
            step_V = scale_WH * STEP;
        }

        @Override
        protected Void doInBackground(Void... params) {

            while (current_Width <= screen_W) {

                left -= step_H;
                top -= step_V;
                right += step_H;
                bottom += step_V;

                current_Width += 2 * step_H;

                left = Math.max(left, start_Left);
                top = Math.max(top, start_Top);
                right = Math.min(right, start_Right);
                bottom = Math.min(bottom, start_Bottom);
                Log.e("jj", "top="+top+",bottom="+bottom+",left="+left+",right="+right);
                onProgressUpdate(new Integer[] { left, top, right, bottom });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(final Integer... values) {
            super.onProgressUpdate(values);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setFrame(values[0], values[1], values[2], values[3]);
                }
            });
        }
    }
}
