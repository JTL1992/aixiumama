package com.harmazing.aixiumama.base;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.harmazing.aixiumama.utils.ThreadPoolManager;
import com.harmazing.aixiumama.utils.UIUtils;
import com.harmazing.aixiumama.utils.RequestVo;
import com.harmazing.aixiumama.utils.NetUtil;


/**
 * Created by gyw on 2014/12/16.
 */
public abstract class BaseFragment extends Fragment implements OnClickListener{

    protected static final String TAG = "BaseFragment";

    protected static final int NET_FAILD = 1;
    protected static final int FAILD = 2;
    protected static final int SUCCESS = 3;

    protected Context context;
    protected Activity activity;
    protected View view;
    protected ProgressDialog progressDialog;

    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	this.activity = activity;
		this.context = activity;
        progressDialog = new ProgressDialog(activity);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(setMyContentView(),container, false);
        return view;
    }


	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
		initRequestVo();
		initCallBall();
        initData();
		setListeners();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        initView();
//        initRequestVo();
//        initCallBall();
//        initData();
//        setListeners();
//    }

    protected abstract int setMyContentView();
	/*
	 * 初始化请求参数
	 */
	protected abstract void initRequestVo();

	/*
     * 初始化回调函数
     */
	protected abstract void initCallBall();
	
	 /*
	  * 方法描述：初始化视图
	  */
	protected abstract void initView();
	
    /*
	 * 方法描述：初始化数据
	 */
	protected abstract void initData();
	/*
	 * 方法描述：设置监听 
	 */
	protected abstract void setListeners();
    /**
     * 
     * 回调接口 用于处理parser解析完后的数据
     */
    protected  interface DataCallback<T> {
		public abstract void processData(T data);
	}


    /**
     * TODO 关闭提示框
     */
    protected void closeProgressDialog() {
        if (this.progressDialog != null && progressDialog.isShowing())
            this.progressDialog.dismiss();
    }
    /**
     * TODO 显示提示框
     */
    protected void showProgressDialog() {

        if (this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(activity);
//            this.progressDialog.setCanceledOnTouchOutside(false);
//            this.progressDialog.setCancelable(false);
        }
        this.progressDialog.setCancelable(false);
        this.progressDialog.setCanceledOnTouchOutside(false);
        //this.progressDialog.setTitle(getString(R.string.loadTitle));
        this.progressDialog.setMessage("正在加载");
        this.progressDialog.show();

    }
    /**
     * TODO 显示自定义内容提示框
     */
    protected void showProgressDialog(String message) {
        if (this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(activity);
        }
        if(!progressDialog.isShowing()){
            //this.progressDialog.setTitle(getString(R.string.loadTitle));
            this.progressDialog.setMessage(message);
            this.progressDialog.show();
        }
    }


    
    /**
     * 获取服务器数据
     */
    protected void getDataFromServer(RequestVo vo, DataCallback<?> callback) {
        if(vo.isShowDialog()){
            showProgressDialog();
        }
    	//获取线程池管理器
    	ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
    	BaseHandler handler = new BaseHandler(callback);
    	threadPoolManager.addTask(new BaseTask(handler, vo));
    }
    
    
    /**
     * handler处理和发送消息
     */
     private class BaseHandler extends android.os.Handler {
    	
    	 private DataCallback callback;
    	 
    	 public BaseHandler(DataCallback callback) {
    		 	this.callback = callback;
    	 }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case NET_FAILD:
                    closeProgressDialog();
                    UIUtils.showToast(getActivity(), "连接网络失败..");
                    break;
                case FAILD:
                    closeProgressDialog();
                    UIUtils.showToast(getActivity(), "解析数据失败..");
                    break;
                case SUCCESS:
                    System.out.println("请求成功...");
                	if(msg.obj != null) {
                        closeProgressDialog();
                		if(callback!=null){
    						callback.processData(msg.obj);
    					}
                	} else {
                        closeProgressDialog();
//                		UIUtils.showToast(getActivity(), "获取网络数据失败...");
//                        ToastUtil.show(getActivity(),"获取网络数据失败...");
                        System.out.println("获取网络数据失败...");
                	}
                    break;
            }
        }
    };

    /**
     * 在子线程中请求网络
     */
    private class BaseTask implements Runnable {

        private BaseHandler handler;
        private RequestVo vo;
        
        public BaseTask(BaseHandler handler, RequestVo vo) {
            this.handler = handler;
            this.vo = vo;
        }

        @Override
        public void run() {
            if (!NetUtil.hasConnectedNetwork(context)) {
            	handler.sendEmptyMessage(NET_FAILD);
            } else {
                try {
                    //String result = responseInfo.result;
                    Message msg = Message.obtain();
                    msg.what = SUCCESS;
                    msg.obj = NetUtil.get(vo);
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(FAILD);
                }
            }
        }
    }
}
