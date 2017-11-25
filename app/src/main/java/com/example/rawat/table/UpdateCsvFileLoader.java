package com.example.rawat.table;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by WIN10 on 11/25/2017.
 */

public class UpdateCsvFileLoader extends AsyncTaskLoader<String> {
    public UpdateCsvFileLoader(Context context) {
        super(context);
    }

    @Override
    public String loadInBackground() {
        return null;
    }
}
