package ru.cpc.smartflatview;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Вик on 023. 23.03.16.
 */
public class MediaLibraryDialog extends DialogFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.titleMediaLibrary);
        View v = inflater.inflate(R.layout.medialibrary_dialog, null);

        return v;
    }
}
