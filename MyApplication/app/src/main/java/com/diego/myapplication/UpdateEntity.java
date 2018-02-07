package com.diego.myapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;


/**
 * Created by diego on 25/01/18.
 */

public class UpdateEntity extends DialogFragment {

    public UpdateEntity(){}

    public Dialog onCreateDialog(Bundle savedInstanceState){
        Entity entity = (Entity) getArguments().getSerializable("ENTITY");
        return createUpdateDialog();
    }

    public AlertDialog createUpdateDialog(){
        return null;
    }
}
