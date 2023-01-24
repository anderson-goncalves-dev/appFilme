package com.example.appfilme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.appfilme.R;

public class LoadingAlert {
   private Activity activity;
    private AlertDialog dialog;
    LoadingAlert(Activity myActivity){
        activity = myActivity;

    }
    void startAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_layout, null));

        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }
    void closeAlertDialog(){
        dialog.dismiss();
    }
}
