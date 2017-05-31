package com.tsiro.dogvip.app;

import android.util.SparseIntArray;

import com.tsiro.dogvip.R;

/**
 * Created by giannis on 20/5/2017.
 */

public class AppConfig {

    public static final String BASE_URL = "http://dogvip.votingsystem.gr/api/";
    //TAGS
    public static final String FRAGMENT_CREATED = "fragment_created"; //login activity: check if fragments are created on button click

    //SUBJECT TYPES
    public static final int PUBLISH_SUBJ = 0;
    public static final int REPLAY_SUBJ = 1;
    public static final int BEHAVIOR_SUBJ = 2;
    //APPLICATION EVENTS
    public static final int FRAGMENT_ANIMATION = 1000;
    public static final int USER_ACCOUNT_DATA = 900;
    //REQUEST STATE
    public static final int REQUEST_NONE = 0;
    public static final int REQUEST_RUNNING = 10;
    public static final int REQUEST_SUCCEEDED = 11;
    public static final int REQUEST_FAILED = -11;
    //GOOGLE SIGN IN REQUEST CODE
    public static final int GOOGLE_REQ_CODE = 9001;
    //STATUS CODES
    public static final int STATUS_OK = 200;
    public static final int STATUS_ERROR = -200;
    private static final int ERROR_EMPTY_REQUIRED_FLDS = -210;
    private static final int ERROR_INVALID_EMAIL = -220;
    private static final int ERROR_EMAIL_EXISTS = -230;
    private static final int ERROR_INVALID_PASSWORD = -240;
    private static final int ERROR_PASSWORDS_MISMATCH = -250;
    private static final int ERROR_INVALID_USERNAME_PASSWORD = -260;
    private static final int ERROR_ACCOUNT_NOT_VERIFIED_YET = -270;
    private static final int ERROR_EMAIL_USED = -280;
    private static final int ERROR_EMAIL_NOT_FOUND = -290;
    private static final int ERROR_NEW_PASSWORD_IS_SAME = -300;

    private static final SparseIntArray inputValidationCodes = new SparseIntArray();

    public static SparseIntArray getCodes() {
        inputValidationCodes.put(STATUS_ERROR, R.string.error);
        inputValidationCodes.put(ERROR_EMPTY_REQUIRED_FLDS, R.string.empty_required_fields);
        inputValidationCodes.put(ERROR_INVALID_EMAIL, R.string.invalid_email);
        inputValidationCodes.put(ERROR_EMAIL_EXISTS, R.string.email_not_available);
        inputValidationCodes.put(ERROR_INVALID_PASSWORD, R.string.invalid_password);
        inputValidationCodes.put(ERROR_PASSWORDS_MISMATCH, R.string.pass_mismatch);
        inputValidationCodes.put(ERROR_INVALID_USERNAME_PASSWORD, R.string.invalid_email_password);
        inputValidationCodes.put(ERROR_ACCOUNT_NOT_VERIFIED_YET, R.string.account_not_verified);
        inputValidationCodes.put(ERROR_EMAIL_USED, R.string.email_in_use);
        inputValidationCodes.put(ERROR_EMAIL_NOT_FOUND, R.string.email_not_found);
        inputValidationCodes.put(ERROR_NEW_PASSWORD_IS_SAME, R.string.new_password_is_same);

        return inputValidationCodes;
    }
}