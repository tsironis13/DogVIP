package com.tsiro.dogvip.profs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tsiro.dogvip.POJO.profs.ProfObj;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.chatroom.ChatRoomActivity;
import com.tsiro.dogvip.databinding.ActivityProfDetailsBinding;

/**
 * Created by giannis on 7/10/2017.
 */

public class ProfDetailsActivity extends AppCompatActivity {

    private ActivityProfDetailsBinding mBinding;
    private ProfObj profObj;
    private boolean callPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_prof_details);
        setSupportActionBar(mBinding.incltoolbar.toolbar);
        if (savedInstanceState != null) {
            profObj = savedInstanceState.getParcelable(getResources().getString(R.string.parcelable_obj));
        } else {
            if (getIntent().getExtras() != null) {
                profObj = getIntent().getExtras().getParcelable(getResources().getString(R.string.parcelable_obj));
            }
        }
        setTitle(profObj.getName());
        if (callPhone) {
            makeCall(profObj.getMobile_number());
            callPhone = false;
        }
        mBinding.setProf(profObj);
        for (Integer category: profObj.getCategories()) {
            TextView textView = new TextView(this);
            textView.setText(AppConfig.getProfCategoryHashMap().get(category));
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            mBinding.categoriesLlt.addView(textView);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getResources().getString(R.string.parcelable_obj), profObj);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.prof_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.msg_item:
                Bundle bundle = new Bundle();
                bundle.putInt(getResources().getString(R.string.role), 1);
                bundle.putInt(getResources().getString(R.string.user_role_id), profObj.getId());
                bundle.putString(getResources().getString(R.string.action), getResources().getString(R.string.get_chat_rooom_msgs_by_participants));
                bundle.putString(getResources().getString(R.string.receiver), profObj.getName());
                startActivity(new Intent(this, ChatRoomActivity.class).putExtras(bundle));
                return true;
            case R.id.phone_item:
                getPermissionToMakePhoneCall(profObj.getMobile_number());
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConfig.CALL_PHONE_PERMISSION_RESULT_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callPhone = true;
            }
        }
    }

    private void getPermissionToMakePhoneCall(String phone) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            makeCall(phone);
        } else {
            requestUserPermission();
        }
    }

    private void requestUserPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
//                showSnackBar(getResources().getString(R.string.grant_permission), "", Snackbar.LENGTH_LONG, getResources().getString(R.string.close));
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, AppConfig.CALL_PHONE_PERMISSION_RESULT_CODE);
            }
        } else {
            makeCall(profObj.getMobile_number());
        }
    }

    private void makeCall(String phone) {
        if (phone != null && !phone.isEmpty()) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+phone));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) return;
            startActivity(callIntent);
        }
    }
}
