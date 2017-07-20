package com.tsiro.dogvip.networklayer;

import com.tsiro.dogvip.POJO.chat.FetchChatRoomRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomResponse;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomsRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomsResponse;
import com.tsiro.dogvip.POJO.chat.SendMessageRequest;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.chatroom.ChatRoomViewModel;
import com.tsiro.dogvip.mychatrooms.MyChatRoomsViewModel;
import com.tsiro.dogvip.retrofit.RetrofitFactory;
import com.tsiro.dogvip.retrofit.ServiceAPI;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by giannis on 17/7/2017.
 */

public class ChatAPIService {

    private ServiceAPI serviceAPI;

    public ChatAPIService() {
        serviceAPI = RetrofitFactory.getInstance().getServiceAPI();
    }

    public Flowable<FetchChatRoomResponse> getChatRoomMsgs(final FetchChatRoomRequest request, final ChatRoomViewModel viewModel) {

        return serviceAPI.getChatRoomMsgs(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<FetchChatRoomResponse>() {
                    @Override
                    public void accept(@NonNull FetchChatRoomResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<FetchChatRoomResponse> sendMsg(final SendMessageRequest request, final ChatRoomViewModel viewModel) {

        return serviceAPI.sendMsg(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<FetchChatRoomResponse>() {
                    @Override
                    public void accept(@NonNull FetchChatRoomResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

    public Flowable<FetchChatRoomsResponse> getChatRooms(final FetchChatRoomsRequest request, final MyChatRoomsViewModel viewModel) {

        return serviceAPI.getChatRooms(request)
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(@NonNull Subscription subscription) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_RUNNING);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_FAILED);
                    }
                })
                .doOnNext(new Consumer<FetchChatRoomsResponse>() {
                    @Override
                    public void accept(@NonNull FetchChatRoomsResponse response) throws Exception {
                        viewModel.setRequestState(AppConfig.REQUEST_SUCCEEDED);
                    }
                });
    }

}
