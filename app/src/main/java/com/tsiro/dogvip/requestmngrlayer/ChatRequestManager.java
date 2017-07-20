package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.chat.FetchChatRoomRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomResponse;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomsRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomsResponse;
import com.tsiro.dogvip.POJO.chat.SendMessageRequest;
import com.tsiro.dogvip.chatroom.ChatRoomViewModel;
import com.tsiro.dogvip.mychatrooms.MyChatRoomsViewModel;
import com.tsiro.dogvip.networklayer.ChatAPIService;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by giannis on 17/7/2017.
 */

public class ChatRequestManager {

    private static ChatRequestManager mInstance;
    private ChatAPIService mFcmAPIService;

    private ChatRequestManager() {
        this.mFcmAPIService = new ChatAPIService();
    }

    public static ChatRequestManager getInstance() {
        if (mInstance == null) mInstance = new ChatRequestManager();
        return mInstance;
    }

    public Flowable<FetchChatRoomResponse> getChatRoomMsgs(FetchChatRoomRequest request, ChatRoomViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mFcmAPIService.getChatRoomMsgs(request, viewModel).delay(200, TimeUnit.MILLISECONDS);
    }

    public Flowable<FetchChatRoomResponse> sendMsg(SendMessageRequest request, ChatRoomViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mFcmAPIService.sendMsg(request, viewModel);
    }

    public Flowable<FetchChatRoomsResponse> getChatRooms(FetchChatRoomsRequest request, MyChatRoomsViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mFcmAPIService.getChatRooms(request, viewModel).delay(200, TimeUnit.MILLISECONDS);
    }

}
