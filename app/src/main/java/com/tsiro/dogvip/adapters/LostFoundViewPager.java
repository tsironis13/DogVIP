package com.tsiro.dogvip.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tsiro.dogvip.POJO.lostfound.LostFoundResponse;
import com.tsiro.dogvip.lostfound.FoundEntriesFrgmt;
import com.tsiro.dogvip.lostfound.LostEntriesFrgmt;
import com.tsiro.dogvip.lostfound.MyFoundEntriesFrgmt;
import com.tsiro.dogvip.lostfound.MyLostEntriesFrgmt;

/**
 * Created by giannis on 10/7/2017.
 */

public class LostFoundViewPager extends FragmentStatePagerAdapter {

    private Fragment[] pages = new Fragment[getCount()];
    private String[] tabText;
    private int type;//0 lost, 1 found
    private Fragment mFragment;
    private LostFoundResponse data;

    public LostFoundViewPager(FragmentManager fm, String[] tabText, int type, LostFoundResponse data) {
        super(fm);
        this.tabText = tabText;
        this.type = type;
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                mFragment = type == 0 ? LostEntriesFrgmt.newInstance(data.getData()) : FoundEntriesFrgmt.newInstance(data.getData());
                if (pages[position] == null) pages[position] = mFragment;
                return pages[position];
            case 1:
                mFragment = type == 0 ? MyLostEntriesFrgmt.newInstance(data.getMy_data(), data.getId(), data.getMy_pets()) : MyFoundEntriesFrgmt.newInstance(data.getMy_data());
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
