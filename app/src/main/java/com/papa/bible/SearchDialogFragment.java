package com.papa.bible;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Papa on 2016/4/29.
 */
public class SearchDialogFragment extends DialogFragment {

    private EditText mSearchEdit;

    public static void showSearchDialog(Fragment fragment) {
        SearchDialogFragment dialogFragment = new SearchDialogFragment();
        dialogFragment.setTargetFragment(fragment, 1);
        dialogFragment.show(fragment.getFragmentManager(), SearchDialogFragment.class
                .getSimpleName());
    }

    public static void showSearchDialog(FragmentActivity activity) {
        SearchDialogFragment dialogFragment = new SearchDialogFragment();
        dialogFragment.show(activity.getSupportFragmentManager(), SearchDialogFragment.class
                .getSimpleName());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_search, null);
        mSearchEdit = (EditText) view.findViewById(R.id.edit);
        builder.setView(view);
        builder.setTitle(R.string.search).setPositiveButton(R.string.confirm, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OnSearchCompleteListener listener = getListener();
                if(listener!=null){
                    String content = mSearchEdit.getText().toString();
                    if (!TextUtils.isEmpty(content))
                        listener.onSearchComplete(content);
                }

            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();
    }

    private OnSearchCompleteListener getListener(){
        OnSearchCompleteListener listener = null;
        if (getTargetFragment() instanceof OnSearchCompleteListener) {
            listener = (OnSearchCompleteListener) getTargetFragment();

        }else if(getActivity() instanceof OnSearchCompleteListener){
            listener = (OnSearchCompleteListener) getActivity();
        }
        return listener;
    }

    public interface OnSearchCompleteListener {
        public void onSearchComplete(String content);
    }
}
