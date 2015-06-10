package com.harmazing.aixiumama.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

import com.harmazing.aixiumama.utils.NetUtil;
import com.harmazing.aixiumama.utils.ThreadPoolManager;
import com.harmazing.aixiumama.utils.UIUtils;
import com.harmazing.aixiumama.utils.RequestVo;


public abstract class BaseGActivity extends Activity implements OnClickListener {


    protected static final String TAG = "BaseGActivity";

    protected static final int NET_FAILD = 1;
    protected static final int FAILD = 2;
    protected static final int SUCCESS = 3;

    protected Context context;
    protected Activity activity;
    protected View view;
    protected ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView();
        initExtraData();
        initView();
        initRequestVo();
        initCallBack();
        initData();
        setListeners();
    }


    protected abstract void initContentView();


    private void initExtraData() {
        this.activity = this;
        this.context = activity;
        progressDialog = new ProgressDialog(activity);
    }

    /*
     * 初始化请求参数
     */
    protected abstract void initRequestVo();

    /*
     * 初始化回调函数
     */
    protected abstract void initCallBack();

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
        }

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
                    UIUtils.showToast(BaseGActivity.this, "连接网络失败..");
                    break;
                case FAILD:
                    UIUtils.showToast(BaseGActivity.this, "解析数据失败..");
                    break;
                case SUCCESS:
                    System.out.println("请求成功...");
                    if(msg.obj != null) {
                        if(callback!=null){
                            callback.processData(msg.obj);
                        }
                    } else {
                        UIUtils.showToast(BaseGActivity.this, "获取网络数据失败...");
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
