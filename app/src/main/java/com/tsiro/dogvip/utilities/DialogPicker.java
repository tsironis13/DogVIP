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
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
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

    private static final String debugTag = DialogPicker.class.getSimpleName();
    private DialogActions dialogActions;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialogActions = new DialogActions();
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        Log.e(debugTag, year+"");
        int current_year = Calendar.getInstance().get(Calendar.YEAR);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.MONTH, month);
        long epoch = cal.getTimeInMillis();
        SimpleDateFormat datefrmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String displayDate= datefrmt.format(new Date(epoch));
//        int age = (current_year - year);
        dialogActions.setAction(getResources().getString(R.string.date_pick_action));
        dialogActions.setDisplay_date(displayDate);
        dialogActions.setDate(epoch/1000L);
        RxEventBus.createSubject(AppConfig.DIALOG_ACTION, 0).post(dialogActions);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        dialogActions.setAction(getResources().getString(R.string.dialog_cancel_action));
        RxEventBus.createSubject(AppConfig.DIALOG_ACTION, 0).post(dialogActions);
    }
}
