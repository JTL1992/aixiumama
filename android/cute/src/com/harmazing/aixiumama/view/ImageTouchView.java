package com.harmazing.aixiumama.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF; 
import android.util.AttributeSet; 
import android.util.FloatMath; 
import android.view.MotionEvent;
import android.widget.ImageView;

public class ImageTouchView extends ImageView { 
                                                                                                                                                                                                                                                                                                                                                                                                                             
    private PointF startPoint = new PointF(); 
    private Matrix matrix = new Matrix(); 
    private Matrix currentMaritx = new Matrix(); 

    private int mode = 0;//���ڱ��ģʽ 
    private static final int DRAG = 1;//�϶� 
    private static final int ZOOM = 2;//�Ŵ� 
    private float startDis = 0; 
    private PointF midPoint;//���ĵ� 
    float oldRotation = 0;
    ImageOnClickListener imageOnClickListener;
    /** 
     * Ĭ�Ϲ��캯�� 
     * @param context 
     */
    public ImageTouchView(Context context){ 
        super(context); 
    } 
    
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	 

	}
    /** 
     * �ù��췽���ھ�̬����XML�ļ����Ǳ���� 
     * @param context 
     * @param paramAttributeSet 
     */
    public ImageTouchView(Context context,AttributeSet paramAttributeSet){ 
        super(context,paramAttributeSet);

    }



    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) { 
        case MotionEvent.ACTION_DOWN:

            mode = DRAG; 
            currentMaritx.set(this.getImageMatrix());//��¼ImageView���ڵ��ƶ�λ�� 
            startPoint.set(event.getX(),event.getY());//��ʼ�� 
            break; 
        case MotionEvent.ACTION_MOVE://�ƶ��¼� 
                                                                                                                                                                                                                                                                                                                                                                                                                              
            if (mode == DRAG) {//ͼƬ�϶��¼�

                float dx = event.getX() - startPoint.x;//x���ƶ����� 
                float dy = event.getY() - startPoint.y;

                    matrix.set(currentMaritx);
                    matrix.postTranslate(dx, dy);

                                                                                                                                                                                                                                                                                                                                                                                                                                        
            } else if(mode == ZOOM){//ͼƬ�Ŵ��¼� 
            	float rotation = rotation(event) - oldRotation;
            	
                float endDis = distance(event);//������� 
                if(endDis > 10f){ 
                    float scale = endDis / startDis;//�Ŵ��� 
                    //Log.v("scale=", String.valueOf(scale)); 
                    matrix.set(currentMaritx); 
                    matrix.postScale(scale, scale, midPoint.x, midPoint.y); 
                    matrix.postRotate(rotation, midPoint.x, midPoint.y);// ���D
                } 
                                                                                                                                                                                                                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                                                                                                                                                                                        
            } 
            break; 
                                                                                                                                                                                                                                                                                                                                                                                                                                     
        case MotionEvent.ACTION_UP: 
            mode = 0; 
            break; 
        //����ָ�뿪��Ļ������Ļ���д���(��ָ) 
        case MotionEvent.ACTION_POINTER_UP: 
            mode = 0; 
            break; 
        //����Ļ���Ѿ��д��㣨��ָ��,����һ����ָѹ����Ļ 
        case MotionEvent.ACTION_POINTER_DOWN: 
        	oldRotation = rotation(event);
         
            mode = ZOOM; 
            startDis = distance(event); 
                                                                                                                                                                                                                                                                                                                                                                                                                                     
            if(startDis > 10f){//������ָ���������� 
                midPoint = mid(event); 
                currentMaritx.set(this.getImageMatrix());//��¼��ǰ�����ű��� 
            } 
                                                                                                                                                                                                                                                                                                                                                                                                                                     
            break; 
        } 
        this.setImageMatrix(matrix); 
        return true;
    }

    public static interface ImageOnClickListener{
        public void onClick();
    }

    public void setImageOnClickListener(ImageOnClickListener onClickListener){
        this.imageOnClickListener = onClickListener;
    }
    
 // ȡ��ת�Ƕ�
 	private float rotation(MotionEvent event) {
 		double delta_x = (event.getX(0) - event.getX(1));
 		double delta_y = (event.getY(0) - event.getY(1));
 		double radians = Math.atan2(delta_y, delta_x);
 		return (float) Math.toDegrees(radians);
 	}
                          
 	
	// ȡ�������ĵ�
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
    /** 
     * ����֮��ľ��� 
     * @param event 
     * @return 
     */
    private static float distance(MotionEvent event){ 
        //�����ߵľ��� 
        float dx = event.getX(1) - event.getX(0); 
        float dy = event.getY(1) - event.getY(0); 
        return FloatMath.sqrt(dx*dx + dy*dy); 
    } 
    /** 
     * ��������֮�����ĵ�ľ��� 
     * @param event 
     * @return 
     */
    private static PointF mid(MotionEvent event){ 
        float midx = event.getX(1) + event.getX(0); 
        float midy = event.getY(1) - event.getY(0); 
                                                                                                                                                                                                                                                                                                                                                                                                                                 
        return new PointF(midx/2, midy/2); 
    } 
}