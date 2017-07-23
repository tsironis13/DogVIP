package com.tsiro.dogvip.dashboard;

import com.tsiro.dogvip.POJO.chat.FetchChatRoomRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomResponse;
import com.tsiro.dogvip.POJO.chat.SendMessageRequest;
import com.tsiro.dogvip.POJO.dashboard.DashboardRequest;
import com.tsiro.dogvip.POJO.dashboard.DashboardResponse;
import com.tsiro.dogvip.app.Lifecycle;

/**
 * Created by giannis on 21/7/2017.
 */

public class DashboardContract {

    interface View extends Lifecycle.View {
        void onSuccess(DashboardResponse response);
        void onError(int resource);
    }

    interface ViewModel extends Lifecycle.ViewModel {
        void setRequestState(int state);
        void logoutUser(DashboardRequest request);
        void getTotelUnreadMsgs(DashboardRequest request);
    }

}
