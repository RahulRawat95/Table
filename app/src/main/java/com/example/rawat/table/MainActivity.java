package com.example.rawat.table;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        adaptiveTableLayout = (AdaptiveTableLayout) findViewById(R.id.table_layout);
        editText = findViewById(R.id.editor);
        editText.setVisibility(View.GONE);

        source = new Source(this);
        adapter = new Adapter(source, getLayoutInflater(), this);
        adapter.setOnItemClickListener(this);

        adaptiveTableLayout.setAdapter(adapter);
    }

    @Override
    public void onItemClick(final int row, final int column) {
        if (!source.getItemProtection(column)) {
            Toast.makeText(this, "You Cannot edit this field", Toast.LENGTH_LONG).show();
            return;
        }
        editText.setVisibility(View.VISIBLE);
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
                        if (!TextUtils.isEmpty(st)) {
                            source.editItem(row, column, st);
                            adapter.notifyItemChanged(row, column);
                        } else {
                            Toast.makeText(MainActivity.this, "Cannot Leave it Blank", Toast.LENGTH_LONG).show();
                        }
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
        }
        else
            super.onBackPressed();
    }
}
