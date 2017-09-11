package com.tsiro.dogvip.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tsiro.dogvip.POJO.petsitter.PetSitterObj;
import com.tsiro.dogvip.lostfound.FoundEntriesFrgmt;
import com.tsiro.dogvip.lostfound.LostEntriesFrgmt;
import com.tsiro.dogvip.lostfound.MyFoundEntriesFrgmt;
import com.tsiro.dogvip.lostfound.MyLostEntriesFrgmt;
import com.tsiro.dogvip.petsitters.petsitter.other_details.BaseInfoFrgmt;
import com.tsiro.dogvip.petsitters.petsitter.other_details.PlaceInfoFrgmt;
import com.tsiro.dogvip.petsitters.petsitter.other_details.ServicesFrgmt;

/**
 * Created by giannis on 8/9/2017.
 */

public class PetSitterOtherDetailsViewPager extends FragmentStatePagerAdapter {

    private Fragment[] pages = new Fragment[getCount()];
    private Fragment mFragment;
    private PetSitterObj petSitterObj;

    public PetSitterOtherDetailsViewPager(FragmentManager fm, PetSitterObj petSitterObj) {
        super(fm);
        this.petSitterObj = petSitterObj;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                mFragment = BaseInfoFrgmt.newInstance(petSitterObj);
                if (pages[position] == null) pages[position] = mFragment;
                return pages[position];
            case 1:
                mFragment = ServicesFrgmt.newInstance(petSitterObj);
                if (pages[position] == null) pages[position] = mFragment;
                return pages[position];
            case 2:
                mFragment = PlaceInfoFrgmt.newInstance(petSitterObj);
                if (pages[position] == null) pages[position] = mFragment;
                return pages[position];
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
