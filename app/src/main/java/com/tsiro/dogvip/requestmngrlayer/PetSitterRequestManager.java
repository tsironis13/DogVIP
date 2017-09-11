package com.tsiro.dogvip.requestmngrlayer;

import com.tsiro.dogvip.POJO.Image;
import com.tsiro.dogvip.POJO.mypets.OwnerRequest;
import com.tsiro.dogvip.POJO.mypets.owner.OwnerObj;
import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.mypets.GetOwnerViewModel;
import com.tsiro.dogvip.mypets.owner.OwnerViewModel;
import com.tsiro.dogvip.networklayer.MyPetsAPIService;
import com.tsiro.dogvip.networklayer.PetSitterAPIService;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by giannis on 6/9/2017.
 */

public class PetSitterRequestManager {

    private static PetSitterRequestManager mInstance;
    private PetSitterAPIService mPetSitterAPIService;

    private PetSitterRequestManager() {
        this.mPetSitterAPIService = new PetSitterAPIService();
    }

    public static PetSitterRequestManager getInstance() {
        if (mInstance == null) mInstance = new PetSitterRequestManager();
        return mInstance;
    }

    public Flowable<PetSitterObj> petSitterRelatedActions(PetSitterObj request, PetSitterViewModel petSitterViewModel) {
        //in case server response is faster than activity lifecycle callback methods
        return mPetSitterAPIService.petSitterRelatedActions(request, petSitterViewModel).delay(500, TimeUnit.MILLISECONDS);
    }

    public Flowable<Image> uploadImage(RequestBody action, RequestBody token, RequestBody id, MultipartBody.Part file, PetSitterViewModel viewModel) {
        return mPetSitterAPIService.uploadImage(action, token, id, file, viewModel);
    }

    public Flowable<Image> deleteImage(Image image, PetSitterViewModel viewModel) {
        return mPetSitterAPIService.deleteImage(image, viewModel);
    }

}
