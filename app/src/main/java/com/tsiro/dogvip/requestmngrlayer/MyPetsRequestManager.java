package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.mypets.pet.PetObj;
import com.tsiro.dogvip.image_states.ImageUploadViewModel;
import com.tsiro.dogvip.mypets.GetOwnerViewModel;
import com.tsiro.dogvip.mypets.owner.OwnerViewModel;
import com.tsiro.dogvip.mypets.ownerprofile.OwnerProfileViewModel;
import com.tsiro.dogvip.mypets.pet.PetViewModel;
import com.tsiro.dogvip.networklayer.MyPetsAPIService;
import com.tsiro.dogvip.ownerpets.OwnerPetsViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 4/6/2017.
 */

public class MyPetsRequestManager {

    private static MyPetsRequestManager mInstance;
    private MyPetsAPIService mMyPetsAPIService;

    private MyPetsRequestManager() {
        this.mMyPetsAPIService = new MyPetsAPIService();
    }

    public static MyPetsRequestManager getInstance() {
        if (mInstance == null) mInstance = new MyPetsRequestManager();
        return mInstance;
    }

    public Flowable<OwnerObj> getOwnerDetails(OwnerRequest request, GetOwnerViewModel getOwnerViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mMyPetsAPIService.getOwnerDetails(request, getOwnerViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<OwnerObj> submitOwner(OwnerObj request, OwnerViewModel ownerViewModel) {
        return mMyPetsAPIService.submitOwner(request, ownerViewModel);
    }

    public Flowable<Image> uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part file, ImageUploadViewModel viewModel) {
        return mMyPetsAPIService.uploadImage(action, token, id, file, viewModel);
    }

    public Flowable<Image> deleteImage(Image image, ImageUploadViewModel viewModel) {
        return mMyPetsAPIService.deleteImage(image, viewModel);
    }

    public Flowable<OwnerRequest> deleteOwner(OwnerRequest request, OwnerProfileViewModel ownerProfileViewModel) {
        return mMyPetsAPIService.deleteOwner(request, ownerProfileViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<OwnerObj> submitPet(PetObj request, PetViewModel petViewModel) {
        return mMyPetsAPIService.submitPet(request, petViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<OwnerObj> getOwnerDetails(OwnerRequest request, OwnerPetsViewModel viewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mMyPetsAPIService.getOwnerDetails(request, viewModel).delay(500, TimeUnit.MILLISECONDS);
    }
}
