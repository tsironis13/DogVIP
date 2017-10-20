package com.tsiro.dogvip.di;

import com.tsiro.dogvip.chatroom.ChatRoomActivity;
import com.tsiro.dogvip.dashboard.DashboardActivity;
import com.tsiro.dogvip.login.LoginActivityModule;
import com.tsiro.dogvip.login.signin.SignInFragmentModule;
import com.tsiro.dogvip.lostfound.LostActivity;
import com.tsiro.dogvip.lostfound.manipulatefoundpet.ManipulateFoundPetActivity;
import com.tsiro.dogvip.lostfound.manipulatelostpet.ManipulateLostPetActivity;
import com.tsiro.dogvip.lovematch.LoveMatchActivityModule;
import com.tsiro.dogvip.di.scope.PerActivity;
import com.tsiro.dogvip.login.LoginActivity;
import com.tsiro.dogvip.lovematch.LoveMatchActivity;
import com.tsiro.dogvip.mychatrooms.MyChatRoomsActivity;
import com.tsiro.dogvip.mypets.MyPetsActivity;
import com.tsiro.dogvip.mypets.owner.OwnerActivity;
import com.tsiro.dogvip.mypets.owner.OwnerModule;
import com.tsiro.dogvip.mypets.ownerprofile.OwnerProfileActivity;
import com.tsiro.dogvip.mypets.ownerprofile.OwnerProfileModule;
import com.tsiro.dogvip.mypets.pet.PetActivity;
import com.tsiro.dogvip.ownerpets.OwnerPetsActivity;
import com.tsiro.dogvip.petlikes.PetLikesActivity;
import com.tsiro.dogvip.petprofile.PetProfileActivity;
import com.tsiro.dogvip.petsitters.ManipulateNewSitterBookingActivity;
import com.tsiro.dogvip.petsitters.PendingSitterBookingsActivity;
import com.tsiro.dogvip.petsitters.PetSittersActivity;
import com.tsiro.dogvip.petsitters.RateSitterActivity;
import com.tsiro.dogvip.petsitters.SitterCommentsActivity;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterActivity;
import com.tsiro.dogvip.petsitters.petsitter.other_details.PetSitterOtherDtlsActivity;
import com.tsiro.dogvip.petsitters.sitter_assignment.BookingDetailsActivity;
import com.tsiro.dogvip.petsitters.sitter_assignment.SearchSitterFiltersActivity;
import com.tsiro.dogvip.petsitters.sitter_assignment.SearchedSittersListActivity;
import com.tsiro.dogvip.petsitters.sitter_assignment.SitterProfileActivity;
import com.tsiro.dogvip.profs.ProfModule;
import com.tsiro.dogvip.profs.SearchedProfsActivity;
import com.tsiro.dogvip.profs.prof.ProfActivity;
import com.tsiro.dogvip.profs.profprofile.ProfProfileActivity;
import com.tsiro.dogvip.uploadimagecontrol.ImageUploadControlActivity;
import com.tsiro.dogvip.uploadimagecontrol.petsitter_place.PetSitterPlaceUploadControlActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by giannis on 24/9/2017.
 */
@Module
abstract class ActivityBuilder {

    @PerActivity
    @ContributesAndroidInjector(modules = {LoginActivityModule.class})
    abstract LoginActivity bindLoginActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract DashboardActivity bindDashboardActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract MyPetsActivity bindMyPetsActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {OwnerProfileModule.class})
    abstract OwnerProfileActivity bindOwnerProfileActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {OwnerModule.class})
    abstract OwnerActivity bindOwnerActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract OwnerPetsActivity bindOwnerPetsActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract PetActivity bindPetActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract PetProfileActivity bindPetProfileActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract PetLikesActivity bindPetLikesActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {LoveMatchActivityModule.class})
    abstract LoveMatchActivity bindLoveMatchActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract LostActivity bindLostActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract ManipulateLostPetActivity bindManipulateLostPetActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract ManipulateFoundPetActivity bindManipulateFoundPetActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract PetSittersActivity bindPetSittersActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract PetSitterActivity bindPetSitterActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract PetSitterOtherDtlsActivity bindPetSitterOtherDtlsActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract PetSitterPlaceUploadControlActivity bindPetSitterPlaceUploadControlActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract RateSitterActivity bindRateSitterActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract SearchSitterFiltersActivity bindSearchSitterFiltersActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract SearchedSittersListActivity bindSearchedSittersListActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract SitterProfileActivity bindSitterProfileActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract BookingDetailsActivity bindBookingDetailsActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract ManipulateNewSitterBookingActivity bindManipulateNewSitterBookingActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract SitterCommentsActivity bindSitterCommentsActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract ProfProfileActivity bindProfProfileActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {ProfModule.class})
    abstract ProfActivity bindProfActivity();

    @PerActivity
    @ContributesAndroidInjector(modules = {ProfModule.class})
    abstract SearchedProfsActivity bindSearchedProfsActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract MyChatRoomsActivity bindMyChatRoomsActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract ChatRoomActivity bindChatRoomActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract PendingSitterBookingsActivity bindPendingSitterBookingsActivity();

    @PerActivity
    @ContributesAndroidInjector
    abstract ImageUploadControlActivity bindImageUploadControlActivity();
}
