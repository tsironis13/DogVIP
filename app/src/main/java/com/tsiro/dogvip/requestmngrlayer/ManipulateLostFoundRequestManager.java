package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.lostfound.LostFoundRequest;
import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPet;
import com.tsiro.dogvip.POJO.lostfound.ManipulateLostFoundPetResponse;
import com.tsiro.dogvip.lostfound.LostFoundViewModel;
import com.tsiro.dogvip.lostfound.manipulatefoundpet.ManipulateFoundPetViewModel;
import com.tsiro.dogvip.lostfound.manipulatelostpet.ManipulateLostPetViewModel;
import com.tsiro.dogvip.mypets.owner.OwnerViewModel;
import com.tsiro.dogvip.networklayer.LostFoundAPIService;
import com.tsiro.dogvip.networklayer.ManipulateLostFoundAPIService;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 11/7/2017.
 */

public class ManipulateLostFoundRequestManager {

    private static ManipulateLostFoundRequestManager mInstance;
    private ManipulateLostFoundAPIService mManipulateLostFoundAPIService;

    private ManipulateLostFoundRequestManager() {
        this.mManipulateLostFoundAPIService = new ManipulateLostFoundAPIService();
    }

    public static ManipulateLostFoundRequestManager getInstance() {
        if (mInstance == null) mInstance = new ManipulateLostFoundRequestManager();
        return mInstance;
    }

    public Flowable<ManipulateLostFoundPetResponse> manipulateLostFound(ManipulateLostFoundPet request, ManipulateLostPetViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mManipulateLostFoundAPIService.manipulateLostFound(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<ManipulateLostFoundPetResponse> manipulateFoundFound(ManipulateLostFoundPet request, ManipulateFoundPetViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mManipulateLostFoundAPIService.manipulateFoundFound(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<Image> uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part file, ManipulateFoundPetViewModel viewModel) {
        return mManipulateLostFoundAPIService.uploadImage(action, token, id, file, viewModel);
    }

    public Flowable<Image> deleteImage(Image image, ManipulateFoundPetViewModel viewModel) {
        return mManipulateLostFoundAPIService.deleteImage(image, viewModel);
    }

}
