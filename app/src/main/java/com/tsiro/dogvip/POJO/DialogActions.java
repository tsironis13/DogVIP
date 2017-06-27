package com.tsiro.dogvip.POJO;

/**
 * Created by giannis on 16/6/2017.
 */

public class DialogActions {

    private String action, display_date; //either image dialog or delete image dialog etc
    private int selected_action; //0 negative action, 1 positive action
    private long date;

    public DialogActions(String action, int selected_action, long date, String display_date) {
        this.action = action;
        this.selected_action = selected_action;
        this.date = date;
        this.display_date = display_date;
    }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public int getSelected_action() { return selected_action; }

    public void setSelected_action(int selected_action) { this.selected_action = selected_action; }

    public String getDisplay_date() { return display_date; }

    public void setDisplay_date(String display_date) { this.display_date = display_date; }

    public long getDate() { return date; }

    public void setDate(long date) { this.date = date; }
}
