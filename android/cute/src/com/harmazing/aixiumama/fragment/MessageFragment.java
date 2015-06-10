package com.harmazing.aixiumama.fragment;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabWidget;

import com.harmazing.aixiumama.activity.TabHostActivity;
import com.harmazing.aixiumama.R;
import com.harmazing.aixiumama.utils.LogUtil;


/**
 * Created by liujinghui on 11/2/14.
 */
public class MessageFragment extends Fragment {

   View msg_line,cute_line,favorite_line;
    private  FragmentTabHost mTabHost;
    private TabWidget mTabWidget;
    BroadcastReceiver receiver;
    int cuteNum = 0;
    int messNum = 0;
    Button newMessage,newCute;
    Handler mHandler;
    private final String MY_ACTION = "com.cute.broadcast";
    private final int NEW_RECOMMEND = 1;
    private final int NEW_MESSAGE = 2;
    private final int NEW_CUTE = 3;
    public static int newCuteNum = 0;
    public static int newMessageNum = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        newMessageResp();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fragment msgFragment = new MsgFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_msg_container,msgFragment);
        transaction.commit();
//        mHandler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == NEW_CUTE){
//                    newCute.setVisibility(View.VISIBLE);
//                    newCute.setText(msg.arg1+"");
//                    newCuteNum = msg.arg1;
//                    LogUtil.v("cute圆圈",msg.arg1+"");
//                }
//            }
//        };
        LogUtil.v("messageFragment","onCreateView");
        return inflater.inflate(R.layout.fragment_message, container, false);

    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newMessageResp();
        msg_line = getView().findViewById(R.id.mes_line);
        cute_line = getView().findViewById(R.id.cute_line);
        favorite_line = getView().findViewById(R.id.favorates_line);
        newMessage = (Button) getView().findViewById(R.id.msg_btn_new);
        newCute = (Button) getView().findViewById(R.id.cute_btn_new);
        if (newCuteNum != 0) {
            LogUtil.v("newCute", "setVisibility");
            newCute.setText(newCuteNum + "");
            newCute.setVisibility(View.VISIBLE);
        }
        if (newMessageNum != 0) {
            LogUtil.v("newCute", "setVisibility");
            newMessage.setText(newMessageNum + "");
            newMessage.setVisibility(View.VISIBLE);
        }

            getView().findViewById(R.id.msg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment msgFragment = new MsgFragment();
                    replaceFragment(msgFragment);
                    msg_line.setVisibility(View.VISIBLE);
                    cute_line.setVisibility(View.INVISIBLE);
                    favorite_line.setVisibility(View.INVISIBLE);
                }
            });

            getView().findViewById(R.id.cute).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment cuteFragment = new CuteFragment();
                    replaceFragment(cuteFragment);
//                newCute.setVisibility(View.INVISIBLE);
                    msg_line.setVisibility(View.INVISIBLE);
                    cute_line.setVisibility(View.VISIBLE);
                    favorite_line.setVisibility(View.INVISIBLE);

                }
            });

            getView().findViewById(R.id.favorites).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment favFragment = new FavFragment();
                    replaceFragment(favFragment);
                    msg_line.setVisibility(View.INVISIBLE);
                    cute_line.setVisibility(View.INVISIBLE);
                    favorite_line.setVisibility(View.VISIBLE);
                }
            });
//       mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == NEW_CUTE){
//                newCute.setVisibility(View.VISIBLE);
//                newCute.setText(msg.arg1+"");
//                isHaveNewCute = true;
//                LogUtil.v("cute圆圈",msg.arg1+""+isHaveNewCute);
//            }
//
//        }
//    };
//            receiver = new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    LogUtil.v("receiver",intent.getAction());
//                    if (MY_ACTION.equals(intent.getAction())) {
//                        Log.i("获取cute广播", "com.cute.broadcast");
//                        int action = intent.getIntExtra("action", 0);
//                        if (action == NEW_CUTE) {
//                            Message message = new Message();
//                            message.what = action;
//                            message.arg1 = intent.getIntExtra("count", 1);
//                            cuteNum = intent.getIntExtra("count", 1);
//                            mHandler.sendMessage(message);
//                            Log.i("获取cute数", "" + cuteNum);
//                        }
//                        if (action == NEW_MESSAGE) {
//                            Message message = new Message();
//                            message.what = action;
//                            message.arg1 = intent.getIntExtra("count", 1);
//
//                            mHandler.sendMessage(message);
//                            Log.i("获取新消息广播MessageFre", "com.cute.broadcast");
//                        }
//                    }
//                }
//            };
//            IntentFilter filter = new IntentFilter();
//            filter.addAction("com.cute.broadcast");
//            getActivity().registerReceiver(receiver, filter);
            super.onActivityCreated(savedInstanceState);
        }

    @Override
    public void onResume() {
        super.onResume();
        if (newCuteNum <= 0){
            newCute.setVisibility(View.INVISIBLE);
        }
       else {
            LogUtil.v("newCute", "setVisibility");
            newCute.setText(newCuteNum + "");
            newCute.setVisibility(View.VISIBLE);
        }
        if (newMessageNum <= 0){
            newMessage.setVisibility(View.INVISIBLE);
        }
        else {
            LogUtil.v("newCute", "setVisibility");
            newMessage.setText(newMessageNum + "");
            newMessage.setVisibility(View.VISIBLE);
        }

    }

    private void replaceFragment(Fragment newFragment) {

        FragmentTransaction trasection =
                getFragmentManager().beginTransaction();
        if(!newFragment.isAdded()){
            try{
                //FragmentTransaction trasection =
                getFragmentManager().beginTransaction();
                trasection.replace(R.id.fl_msg_container, newFragment);
                trasection.addToBackStack(null);
                trasection.commit();

            }catch (Exception e) {
                e.printStackTrace();
            }
        }else
            trasection.show(newFragment);
    }
    public void newMessageResp(){
//        CuteService.isResetMsg = true;
        mTabHost = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);
        mTabWidget = mTabHost.getTabWidget();
        if (TabHostActivity.newMessageNum == 0)
        mTabWidget.getChildTabViewAt(3).findViewById(R.id.red_button).setVisibility(View.INVISIBLE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        getActivity().unregisterReceiver(receiver);
    }
}
