package com.inu.cafeteria.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.inu.cafeteria.Activity.WaitingFoodNumberActivity;
import com.inu.cafeteria.Adapter.GridviewAdapter;
import com.inu.cafeteria.R;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by ksj on 2017. 8. 10..
 */

public class CompleteFoodBoardFragment extends Fragment {

    private String socketURL = "http://117.16.231.66:3000";
    private String receiveMessage;
    private String tagCafeteria;

    private Socket mSocket;

    GridView gridCompleteFoodNumber;
    GridviewAdapter gridviewAdapter;


    // 클래스 외부에서 함수를 사용하기 위함
    public static Fragment mFragment;



    @Override
    public void onAttach(Context newBase) {
        super.onAttach(TypekitContextWrapper.wrap(newBase));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_complete_food_board, null);

        mFragment = this;

        initView(v);
        initSocket();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mSocket.connect();
        mSocket.on(tagCafeteria, onNewMessage);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSocket.disconnect();
        mSocket.off(tagCafeteria, onNewMessage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSocket.disconnect();
        mSocket.off(tagCafeteria, onNewMessage);
    }

    private void initView(View v) {
        gridCompleteFoodNumber = (GridView) v.findViewById(R.id.fcfb_grid_complete_food);
        gridviewAdapter = new GridviewAdapter(getContext());
        gridCompleteFoodNumber.setAdapter(gridviewAdapter);
        tagCafeteria = getArguments().getString("code");
    }

    private void initSocket() {
        try {
            mSocket = IO.socket(socketURL);
        }
        catch (URISyntaxException e) {

        }
        //mSocket.connect();
        //mSocket.on(tagCafeteria, onNewMessage);
    }

    private void attemptSend(String tag, String sendMessage) {
        sendMessage = "전송할 데이터";
        if (TextUtils.isEmpty(sendMessage)) {
            return;
        }

        mSocket.emit(tag, sendMessage);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        receiveMessage = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }
                    //Log.i("debug", receiveMessage);
                    // 소켓통신으로 온 완료된 음식번호 중 사용자가 대기중이었던 번호를 제외하고 미니전광판에 띄움
                    if(!((WaitingFoodNumberActivity)getActivity()).matchCompleteFoodNumber(receiveMessage)) {
                        gridviewAdapter.add(receiveMessage);
                        gridviewAdapter.notifyDataSetChanged();
                    }
                }
            });
        }


    };

    public void addCompleteFood(String number) {
        if(!mSocket.connected()) {
            gridviewAdapter.add(number);
            gridviewAdapter.notifyDataSetChanged();
        }
    }
}