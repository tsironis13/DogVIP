package com.tsiro.dogvip.utilities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import java.util.Calendar;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.tsiro.dogvip.POJO.DialogActions;
import com.tsiro.dogvip.R;
import com.tsiro.dogvip.app.AppConfig;
import com.tsiro.dogvip.app.Lifecycle;
import com.tsiro.dogvip.utilities.eventbus.RxEventBus;

/**
 * Created by giannis on 4/7/2017.
 */

public class DialogPicker extends DialogFragment implements DatePickerDialog.OnDateSetListener, DatePickerDialog.OnCancelListener, TimePickerDialog.OnTimeSetListener {

    private DialogActions dialogActions;
    private int dialogType;

    //type 0 -> date picker dialog
    //type 1 -> time picker dialog

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialogActions = new DialogActions();
        dialogType = getArguments().getInt(getResources().getString(R.string.dialog_type));
        final Calendar c = Calendar.getInstance();
        if (dialogType == 0) {
            // Use the current date as the default date in the picker
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        } else {
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int current_year = Calendar.getInstance().get(Calendar.YEAR);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.MONTH, month);
        long epoch = cal.getTimeInMillis();
        dialogActions.setAction(getResources().getString(R.string.date_pick_action));
        if (epoch/1000L > System.currentTimeMillis()/1000L) {
            dialogActions.setCode(AppConfig.STATUS_ERROR);
        } else {
            dialogActions.setCode(AppConfig.STATUS_OK);
        }
        SimpleDateFormat datefrmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String displayDate= datefrmt.format(new Date(epoch));
        dialogActions.setDisplay_date(displayDate);
        dialogActions.setDate(epoch/1000L);
        RxEventBus.createSubject(AppConfig.DIALOG_ACTION, 0).post(dialogActions);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        dialogActions.setAction(getResources().getString(R.string.time_pick_action));
        String sminute = String.valueOf(minute);
        if (minute <= 9) sminute = String.format(Locale.getDefault(), "%02d", minute);
        dialogActions.setDisplay_date(hourOfDay+":"+sminute);

        RxEventBus.createSubject(AppConfig.DIALOG_ACTION, 0).post(dialogActions);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (dialogType == 0) {
            dialogActions.setAction(getResources().getString(R.string.dialog_cancel_date_action));
        } else {
            dialogActions.setAction(getResources().getString(R.string.dialog_cancel_time_action));
        }
        RxEventBus.createSubject(AppConfig.DIALOG_ACTION, 0).post(dialogActions);
    }
}
