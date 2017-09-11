package com.tsiro.dogvip.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.lostfound.FoundEntriesFrgmt;
import com.tsiro.dogvip.lostfound.LostEntriesFrgmt;
import com.tsiro.dogvip.lostfound.MyFoundEntriesFrgmt;
import com.tsiro.dogvip.lostfound.MyLostEntriesFrgmt;
import com.tsiro.dogvip.petsitters.MyPetAssignmentsFrgmt;
import com.tsiro.dogvip.petsitters.SitterAssignmentsFrgmt;

/**
 * Created by giannis on 3/9/2017.
 */

public class PetSittersViewPager extends FragmentStatePagerAdapter {

    private Fragment[] pages = new Fragment[getCount()];
    private String[] tabText;
    private Fragment mFragment;
    private LostFoundResponse data;

    public PetSittersViewPager(FragmentManager fm, String[] tabText) {
        super(fm);
        this.tabText = tabText;
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                mFragment = SitterAssignmentsFrgmt.newInstance();
                if (pages[position] == null) pages[position] = mFragment;
                return pages[position];
            case 1:
                mFragment = MyPetAssignmentsFrgmt.newInstance();
                if (pages[position] == null) pages[position] = mFragment;
                return pages[position];
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabText[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}
