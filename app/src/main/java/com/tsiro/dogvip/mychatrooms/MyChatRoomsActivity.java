package com.tsiro.dogvip.mychatrooms;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.tsiro.dogvip.dashboard.DashboardActivity;
import com.tsiro.dogvip.POJO.chat.ChatRoom;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomsRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomsResponse;
import com.tsiro.dogvip.POJO.chat.Message;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.RecyclerViewAdapter;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.chatroom.ChatRoomActivity;
import com.tsiro.dogvip.databinding.ActivityMyChatRoomsBinding;
import com.tsiro.dogvip.requestmngrlayer.ChatRequestManager;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 19/7/2017.
 */

public class MyChatRoomsActivity extends BaseActivity implements MyChatRoomsContract.View {

    private ActivityMyChatRoomsBinding mBinding;
    private MyChatRoomsContract.ViewModel mViewModel;
    private String mToken;
    private MyChatRoomsPresenter mPresenter;
    private ArrayList<ChatRoom> data;
    private RecyclerViewAdapter rcvAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_chat_rooms);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        mViewModel = new MyChatRoomsViewModel(ChatRequestManager.getInstance());
        mPresenter = new MyChatRoomsPresenter(this);
        mToken = getMyAccountManager().getAccountDetails().getToken();
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        fetchChatRooms();
        setTitle(getResources().getString(R.string.my_chats_title));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Disposable disp = RxView.clicks(mBinding.retryBtn).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                fetchChatRooms();
            }
        });
        RxEventBus.add(this, disp);
        Disposable disp1 = RxEventBus.createSubject(AppConfig.PUBLISH_NOTFCTS, AppConfig.PUBLISH_SUBJ).observeEvents(Message.class).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Message>() {
            @Override
            public void accept(@NonNull Message message) throws Exception {
                if (!data.isEmpty() && rcvAdapter != null) {
                    for(int i = 0; i < data.size(); i++) {
                        if(data.get(i).getId() == message.getChat_room_id()) {
                            data.get(i).setMessage(message.getMessage());
                            int total = data.get(i).getTotal() + 1;
                            data.get(i).setTotal(total);
                            data.get(i).setTimestamp(message.getCreated_at());
                            rcvAdapter.notifyItemChanged(i);
                        }
                    }
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
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, DashboardActivity.class));
    }

    @Override
    public void onBaseViewClick(View view) {
        int position = (int) view.getTag();
        Bundle bundle = new Bundle();
        bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.get_chat_rooom_msgs_by_chatid));
        bundle.putInt(getResources().getString(R.string.chat_room_id), data.get(position).getId());
        bundle.putInt(getResources().getString(R.string.from_id), data.get(position).getTo_id());
        bundle.putInt(getResources().getString(R.string.user_role_id), data.get(position).getFrom_id());
        String title = data.get(position).getName() + " " + data.get(position).getSurname() + " ";
        String petName = null;
        if (data.get(position).getPet_id() != 0) {
            title += "(" + data.get(position).getPet_name() + ")";
            petName = data.get(position).getPet_name();
        }
        bundle.putString(getResources().getString(R.string.title), title);
        bundle.putString(getResources().getString(R.string.pet_name), petName);
        startActivity(new Intent(this, ChatRoomActivity.class).putExtras(bundle));
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onSuccess(FetchChatRoomsResponse response) {
        dismissDialog();
        if (response.getData().isEmpty()) {
            mBinding.setHasError(true);
            mBinding.setErrorText(getResources().getString(R.string.no_data));
        } else {
            data = response.getData();
            mBinding.setHasError(false);
            initializeRcView(data);
        }
    }

    @Override
    public void onError(int resource) {
        dismissDialog();
        mBinding.setHasError(true);
        mBinding.setErrorText(getResources().getString(R.string.error));
    }

    private void initializeRcView(final ArrayList<ChatRoom> data) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvAdapter = new RecyclerViewAdapter(R.layout.my_chat_rooms_rcv_row) {
            @Override
            protected Object getObjForPosition(int position, ViewDataBinding mBinding) {
                return data.get(position);
            }

            @Override
            protected int getLayoutIdForPosition(int position) {
                return R.layout.my_chat_rooms_rcv_row;
            }

            @Override
            protected int getTotalItems() {
                return data.size();
            }

            @Override
            protected Object getClickListenerObject() {
                return mPresenter;
            }
        };
        mBinding.rcv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mBinding.rcv.setLayoutManager(linearLayoutManager);
        mBinding.rcv.setNestedScrollingEnabled(false);
        mBinding.rcv.setAdapter(rcvAdapter);
    }

    private void fetchChatRooms() {
        mBinding.setHasError(false);
        if (isNetworkAvailable()) {
            FetchChatRoomsRequest request = new FetchChatRoomsRequest();
            request.setAuthtoken(mToken);
            request.setAction(getResources().getString(R.string.get_chat_rooms));
            mViewModel.getChatRooms(request);
            initializeProgressDialog(getResources().getString(R.string.please_wait));
        } else {
            mBinding.setHasError(true);
            mBinding.setErrorText(getResources().getString(R.string.no_internet_connection));
//            showSnackBar(getResources().getString(R.string.no_internet_connection), getResources().getString(R.string.retry), Snackbar.LENGTH_INDEFINITE);
        }
    }

    public void showSnackBar(final String msg, final String action, int length_code) {
        Snackbar snackbar = Snackbar
                .make(mBinding.cntFrml, msg, length_code)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (action.equals(getResources().getString(R.string.retry))) fetchChatRooms();
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
