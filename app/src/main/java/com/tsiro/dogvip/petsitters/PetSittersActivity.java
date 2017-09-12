package com.tsiro.dogvip.petsitters;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tsiro.dogvip.R;
import com.tsiro.dogvip.adapters.LostFoundViewPager;
import com.tsiro.dogvip.adapters.PetSittersViewPager;
import com.tsiro.dogvip.app.BaseActivity;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.databinding.ActivityPetSittersBinding;
import com.tsiro.dogvip.petsitters.petsitter.PetSitterActivity;
import com.tsiro.dogvip.petsitters.sitter_assignment.SearchSitterFiltersActivity;

/**
 * Created by giannis on 3/9/2017.
 */

public class PetSittersActivity extends BaseActivity {

    private static final String debugTag = PetSittersActivity.class.getSimpleName();
    private ActivityPetSittersBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pet_sitters);
        setSupportActionBar(mBinding.incltoolbar.toolbar);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.pet_sitters_title));
        }

        PetSittersViewPager viewPager = new PetSittersViewPager(getSupportFragmentManager(), getResources().getStringArray(R.array.pet_sitters_tabs));
        mBinding.viewPgr.enablePaging(false);
        mBinding.viewPgr.setAdapter(viewPager);
        mBinding.tabs.setupWithViewPager(mBinding.viewPgr);
    }

    @Override
    public Lifecycle.ViewModel getViewModel() {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pet_sitters_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_item:
                startActivity(new Intent(this, SearchSitterFiltersActivity.class));
//                finish();
                return true;
            case R.id.sitter_item:
                startActivity(new Intent(this, PetSitterActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
