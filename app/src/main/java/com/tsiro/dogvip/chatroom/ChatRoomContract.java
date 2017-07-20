package com.tsiro.dogvip.chatroom;

import com.tsiro.dogvip.POJO.chat.FetchChatRoomRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomResponse;
import com.tsiro.dogvip.POJO.chat.SendMessageRequest;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 16/7/2017.
 */

public class ChatRoomContract {

    interface View extends Lifecycle.View {
        void onSuccess(FetchChatRoomResponse response);
        void onError(int resource);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void setRequestState(int state);
        void getChatRoomMsgs(FetchChatRoomRequest request);
        void sendMsg(SendMessageRequest request);
        void updateReadMsgs(SendMessageRequest request);
    }

}
