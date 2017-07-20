package com.tsiro.dogvip.mychatrooms;

import com.tsiro.dogvip.POJO.chat.FetchChatRoomRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomResponse;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomsRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomsResponse;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 19/7/2017.
 */

public class MyChatRoomsContract {

    interface Presenter {
        void onBaseViewClick(android.view.View view);
    }

    interface View extends Lifecycle.View {
        void onSuccess(FetchChatRoomsResponse response);
        void onError(int resource);
        void onBaseViewClick(android.view.View view);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void setRequestState(int state);
        void getChatRooms(FetchChatRoomsRequest request);
    }

}
