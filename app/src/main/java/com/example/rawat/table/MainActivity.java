package com.example.rawat.table;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.cleveroad.adaptivetablelayout.OnItemClickListener;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {
    AdaptiveTableLayout adaptiveTableLayout;
    EditText editText;
    Source source;
    InputMethodManager inputMethodManager;
    Adapter adapter;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        adaptiveTableLayout = findViewById(R.id.table_layout);
        editText = findViewById(R.id.editor);
        editText.setVisibility(View.GONE);

        fab = findViewById(R.id.fab);

        source = new Source(this);
        adapter = new Adapter(source, getLayoutInflater(), this);
        adapter.setOnItemClickListener(this);

        adaptiveTableLayout.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        boolean b = source.saveInExcel();
                        Toast.makeText(MainActivity.this, "File save " + (b ? "successfull" : "unsuccessfull"), Toast.LENGTH_LONG).show();
                        return null;
                    }
                }.execute();
            }
        });
    }

    @Override
    public void onItemClick(final int row, final int column) {
        if (source.getItemProtection(column)) {
            Toast.makeText(this, "You Cannot edit this field", Toast.LENGTH_LONG).show();
            return;
        }
        editText.setVisibility(View.VISIBLE);
        editText.setInputType(source.getItemInputType(column));
        String st = source.getItemData(row, column);
        editText.setText(st);
        editText.setSelection(st.length());
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i) {
                    case EditorInfo.IME_ACTION_DONE:
                        inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                        String st = editText.getText().toString();
                        source.editItem(row, column, st);
                        adapter.notifyItemChanged(row, column);
                        editText.setVisibility(View.GONE);
                        return true;
                }
                return false;
            }
        });

        inputMethodManager.showSoftInput(
                editText,
                InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onRowHeaderClick(int row) {

    }

    @Override
    public void onColumnHeaderClick(int column) {

    }

    @Override
    public void onLeftTopHeaderClick() {

    }

    @Override
    public void onBackPressed() {
        if (editText.isShown()) {
            editText.setVisibility(View.GONE);
        } else
            super.onBackPressed();
    }
}
