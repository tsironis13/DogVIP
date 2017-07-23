package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.chat.FetchChatRoomRequest;
import com.tsiro.dogvip.POJO.chat.FetchChatRoomResponse;
import com.tsiro.dogvip.POJO.dashboard.DashboardRequest;
import com.tsiro.dogvip.POJO.dashboard.DashboardResponse;
import com.tsiro.dogvip.chatroom.ChatRoomViewModel;
import com.tsiro.dogvip.dashboard.DashboardViewModel;
import com.tsiro.dogvip.networklayer.ChatAPIService;
import com.tsiro.dogvip.networklayer.DashboardAPIService;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by giannis on 21/7/2017.
 */

public class DashboardRequestManager {

    private static DashboardRequestManager mInstance;
    private DashboardAPIService mDashboardAPIService;

    private DashboardRequestManager() {
        this.mDashboardAPIService = new DashboardAPIService();
    }

    public static DashboardRequestManager getInstance() {
        if (mInstance == null) mInstance = new DashboardRequestManager();
        return mInstance;
    }

    public Flowable<DashboardResponse> logoutUser(DashboardRequest request, DashboardViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mDashboardAPIService.genericDashboardRequest(request, viewModel).delay(200, TimeUnit.MILLISECONDS);
    }

    public Flowable<DashboardResponse> getTotalUnreadMsgs(DashboardRequest request, DashboardViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mDashboardAPIService.genericDashboardRequest(request, viewModel);
    }

}
