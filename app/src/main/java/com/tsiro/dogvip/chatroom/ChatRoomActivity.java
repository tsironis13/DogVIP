package com.tsiro.dogvip.chatroom;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomResponse;
import com.tsiro.dogvip.POJO.chat.Message;
import com.tsiro.dogvip.POJO.chat.SendMessageRequest;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityChatRoomBinding;
import com.tsiro.dogvip.mychatrooms.MyChatRoomsActivity;
import com.tsiro.dogvip.requestmngrlayer.ChatRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 16/7/2017.
 */

public class ChatRoomActivity extends BaseActivity implements ChatRoomContract.View {

    private static final String debugTag = ChatRoomActivity.class.getSimpleName();
    private ActivityChatRoomBinding mBinding;
    private int role, toId, fromId, petId, chatRoomId;
    private String title, receiverName, receiverSurname, petName, action, mToken;
    private ChatRoomContract.ViewModel viewModel;
    private ArrayList<Message> msgdata;
    private boolean newChat;
    private RecyclerViewAdapter rcvAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_room);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        viewModel = new ChatRoomViewModel(ChatRequestManager.getInstance());
        mToken = getMyAccountManager().getAccountDetails().getToken();
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
//        if (savedInstanceState != null) {
//            action = savedInstanceState.getString(getResources().getString(R.string.action));
//            newChat = savedInstanceState.getBoolean(getResources().getString(R.string.new_chat));
//            role = savedInstanceState.getInt(getResources().getString(R.string.role));
//            toId = savedInstanceState.getInt(getResources().getString(R.string.user_role_id));
//            title = savedInstanceState.getString(getResources().getString(R.string.title));
//            petName = savedInstanceState.getString(getResources().getString(R.string.pet_name));
//            if (action.equals(getResources().getString(R.string.get_chat_rooom_msgs_by_participants))) {
////                receiverName = savedInstanceState.getString(getResources().getString(R.string.receiver));
//                petId = savedInstanceState.getInt(getResources().getString(R.string.pet_id));
////                if (receiverSurname != null) receiverSurname = savedInstanceState.getString(getResources().getString(R.string.receiver_surname));
//            } else {
//                chatRoomId = savedInstanceState.getInt(getResources().getString(R.string.chat_room_id));
//            }
//            setTitle(title);
//        } else {
//            if (getIntent() != null) {
//                action = getIntent().getExtras().getString(getResources().getString(R.string.action));
//                role = getIntent().getExtras().getInt(getResources().getString(R.string.role));
//                toId = getIntent().getExtras().getInt(getResources().getString(R.string.user_role_id));
//                if (getIntent().getExtras().getString(getResources().getString(R.string.pet_name)) != null) petName = getIntent().getExtras().getString(getResources().getString(R.string.pet_name));
//                if (action.equals(getResources().getString(R.string.get_chat_rooom_msgs_by_participants))) {
//                    receiverName = getIntent().getExtras().getString(getResources().getString(R.string.receiver));
//                    if (getIntent().getExtras().getString(getResources().getString(R.string.receiver_surname)) != null) receiverSurname = getIntent().getExtras().getString(getResources().getString(R.string.receiver_surname));
//                    if (getIntent().getExtras().getInt(getResources().getString(R.string.pet_id)) != 0) petId = getIntent().getExtras().getInt(getResources().getString(R.string.pet_id));
//                    setToolbarTitle(receiverName, receiverSurname, petName);
//                } else {
//                    chatRoomId = getIntent().getExtras().getInt(getResources().getString(R.string.chat_room_id));
//                    title = getIntent().getExtras().getString(getResources().getString(R.string.title));
//                    setTitle(title);
//                }
//            }
//        }
        if (savedInstanceState != null) {
            action = savedInstanceState.getString(getResources().getString(R.string.action));
            newChat = savedInstanceState.getBoolean(getResources().getString(R.string.new_chat));
            toId = savedInstanceState.getInt(getResources().getString(R.string.user_role_id));
            title = savedInstanceState.getString(getResources().getString(R.string.title));
            petName = savedInstanceState.getString(getResources().getString(R.string.pet_name));
            if (action.equals(getResources().getString(R.string.get_chat_rooom_msgs_by_participants))) {
//                receiverName = savedInstanceState.getString(getResources().getString(R.string.receiver));
                role = savedInstanceState.getInt(getResources().getString(R.string.role));
                petId = savedInstanceState.getInt(getResources().getString(R.string.pet_id));
//                if (receiverSurname != null) receiverSurname = savedInstanceState.getString(getResources().getString(R.string.receiver_surname));
            } else {
                chatRoomId = savedInstanceState.getInt(getResources().getString(R.string.chat_room_id));
                fromId = savedInstanceState.getInt(getResources().getString(R.string.from_id));
            }
            setTitle(title);
        } else {
            if (getIntent() != null) {
                action = getIntent().getExtras().getString(getResources().getString(R.string.action));
                toId = getIntent().getExtras().getInt(getResources().getString(R.string.user_role_id));
                if (getIntent().getExtras().getString(getResources().getString(R.string.pet_name)) != null) petName = getIntent().getExtras().getString(getResources().getString(R.string.pet_name));
                if (action.equals(getResources().getString(R.string.get_chat_rooom_msgs_by_participants))) {
                    role = getIntent().getExtras().getInt(getResources().getString(R.string.role));
                    receiverName = getIntent().getExtras().getString(getResources().getString(R.string.receiver));
                    if (getIntent().getExtras().getString(getResources().getString(R.string.receiver_surname)) != null) receiverSurname = getIntent().getExtras().getString(getResources().getString(R.string.receiver_surname));
                    if (getIntent().getExtras().getInt(getResources().getString(R.string.pet_id)) != 0) petId = getIntent().getExtras().getInt(getResources().getString(R.string.pet_id));
                    setToolbarTitle(receiverName, receiverSurname, petName);
                } else {
                    chatRoomId = getIntent().getExtras().getInt(getResources().getString(R.string.chat_room_id));
                    Log.e(debugTag, chatRoomId +" CHAT ROOM ID");
                    fromId = getIntent().getExtras().getInt(getResources().getString(R.string.from_id));
                    title = getIntent().getExtras().getString(getResources().getString(R.string.title));
                    setTitle(title);
                }
            }
        }
        fetchChatRoom();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.sendMsgImgv).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                if (!mBinding.msgEdt.getText().toString().isEmpty()) sendMsg();
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxEventBus.createSubject(AppConfig.PUBLISH_NOTFCTS, AppConfig.PUBLISH_SUBJ).observeEvents(Message.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Message>() {
            @Override
            public void accept(@NonNull Message message) throws Exception {
//                Log.e(debugTag, message.getChat_room_id() +" MESSAGE CHAT ROOM ID");
//                Log.e(debugTag, chatRoomId +" chat room id");
                if (message.getChat_room_id() == chatRoomId) {
//                    Log.e(debugTag, message.getUser_role_id()+" RECCCCCC");
                    SendMessageRequest request = new SendMessageRequest();
                    request.setAuthtoken(mToken);
                    request.setAction(getResources().getString(R.string.update_read_msgs));
                    request.setChat_room_id(chatRoomId);
                    if (isNetworkAvailable()) viewModel.updateReadMsgs(request);
                    msgdata.add(message);
                    rcvAdapter.notifyItemInserted(msgdata.size());
                    if (rcvAdapter.getItemCount() > 1) mBinding.rcv.getLayoutManager().scrollToPosition(rcvAdapter.getItemCount() - 1);
//                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                    notificationManager.cancelAll();
                }
            }
        });
        RxEventBus.add(this, disp1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxEventBus.unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getResources().getString(R.string.action), action);
        outState.putBoolean(getResources().getString(R.string.new_chat), newChat);
        outState.putInt(getResources().getString(R.string.role), role);
        outState.putInt(getResources().getString(R.string.user_role_id), toId);
        outState.putString(getResources().getString(R.string.title), title);
        if (petName != null) outState.putString(getResources().getString(R.string.pet_name), petName);
        if (action.equals(getResources().getString(R.string.get_chat_rooom_msgs_by_participants))) {
//            outState.putString(getResources().getString(R.string.receiver), receiverName);
            if (petId != 0) outState.putInt(getResources().getString(R.string.pet_id), petId);
//            if (receiverSurname != null) outState.putString(getResources().getString(R.string.receiver_surname), receiverSurname);
        } else {
            outState.putInt(getResources().getString(R.string.chat_room_id), chatRoomId);
            outState.putInt(getResources().getString(R.string.from_id), fromId);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!action.equals(getResources().getString(R.string.get_chat_rooom_msgs_by_participants))) startActivity(new Intent(this, MyChatRoomsActivity.class));
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onSuccess(FetchChatRoomResponse response) {
        dismissDialog();
        mBinding.setHasError(false);
//        Log.e(debugTag, " ON RESPONSE CHAT ROOM ID => " + response.getChat_room_id());
        chatRoomId = response.getChat_room_id();
//        Log.e(debugTag, response.getMy_user_id() +" FROM _ID ");
        if (response.getAction().equals(getResources().getString(R.string.send_msg))) {
            mBinding.setSendingMsg(false);
            mBinding.msgEdt.setText("");
            msgdata.add(response.getData().get(0));
            rcvAdapter.notifyItemInserted(msgdata.size());
            if (rcvAdapter.getItemCount() > 1) mBinding.rcv.getLayoutManager().scrollToPosition(rcvAdapter.getItemCount() - 1);
        } else if (response.getAction().equals(getResources().getString(R.string.update_read_msgs))) {

        } else {
            fromId = response.getMy_user_id();
            initializeRcView(response.getData(), response.getMy_user_id());
        }
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        mBinding.setSendingMsg(false);
    }

    private void setToolbarTitle(String receiverName, String receiverSurname, String petName) {
        String title = receiverName + " ";
        if (receiverSurname != null) title += receiverSurname + " ";
        if (petName != null) title += "(" + petName + ")";
        this.title = title;
        setTitle(title);
    }

    private void sendMsg() {
        if (isNetworkAvailable()) {
            mBinding.setSendingMsg(true);
            SendMessageRequest request = new SendMessageRequest();
            request.setAction(getResources().getString(R.string.send_msg));
            request.setAuthtoken(mToken);
            request.setMessage(mBinding.msgEdt.getText().toString());
            request.setPet_id(petId);
            request.setTo_id(toId);
            request.setFrom_id(fromId);
//            Log.e(debugTag, "BEFORE SENDED CHAT ROOM ID => " + chatRoomId);
            request.setChat_room_id(chatRoomId);
//            request.setRole(role);
            request.setPet_name(petName);
            if (msgdata.isEmpty())newChat = true;
            if (action.equals(getResources().getString(R.string.get_chat_rooom_msgs_by_participants))) {
            } else {
            }
            viewModel.sendMsg(request);
//            initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.retry), Snackbar.LENGTH_SHORT);
        }
    }

    private void initializeRcView(final ArrayList<Message> data, final int myid) {
        msgdata = data;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvAdapter = new RecyclerViewAdapter(-1) {
            @Override
            protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
                if (position >= 1 && data.get(position).getUser_role_id() == data.get(position - 1).getUser_role_id()) data.get(position).setHide_image(true);
                return data.get(position);
            }

            @Override
            protected int getLayoutIdForPosition(int position) {
                if (data.get(position).getUser_role_id() == myid) {
                    return AppConfig.MY_CHAT_VIEW_ITEM;
                } else {
                    return AppConfig.OTHER_CHAT_VIEW_ITEM;
                }
            }

            @Override
            protected int getTotalItems() {
                return data.size();
            }

            @Override
            protected Object getClickListenerObject() {
                return null;
            }
        };
//        mBinding.rcv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mBinding.rcv.setLayoutManager(linearLayoutManager);
        mBinding.rcv.setNestedScrollingEnabled(false);
        mBinding.rcv.setAdapter(rcvAdapter);
        if (rcvAdapter.getItemCount() > 1) mBinding.rcv.getLayoutManager().scrollToPosition(rcvAdapter.getItemCount() - 1);
    }

    private void fetchChatRoom() {
        mBinding.setHasError(true);
        if (isNetworkAvailable()) {
            FetchChatRoomRequest request = new FetchChatRoomRequest();
            request.setAuthtoken(mToken);
            request.setAction(action);
            request.setRole(role);
            if (action.equals(getResources().getString(R.string.get_chat_rooom_msgs_by_participants))) {
                request.setTo_id(toId);
                request.setPet_id(petId);
            } else {
                request.setFrom_id(fromId);
                request.setChatid(chatRoomId);
            }
            viewModel.getChatRoomMsgs(request);
            initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.retry), Snackbar.LENGTH_INDEFINITE);
        }
    }

    public void showSnackBar(final String msg, final String action, int length_code) {
        Snackbar snackbar = Snackbar
                .make(mBinding.cntFrml, msg, length_code)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (action.equals(getResources().getString(R.string.retry))) fetchChatRoom();
                    }
                });
        snackbar.setActionTextColor(ContextCompat.getColor(this, android.R.color.black));
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        sbView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        snackbar.show();
    }
}
