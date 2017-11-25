package com.example.rawat.table;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WIN10 on 11/24/2017.
 */

public class Source implements TableDataSource<String, String, String, String, Boolean> {
    int mRowsCount;
    int mColsCount;
    JSONArray coldata;
    JSONArray data;
    boolean wasChanged = false;

    private String leftTopHeader;
    private List<String> rowHeader;
    private List<String> columnHeader;
    private List<String> itemData;
    private List<Boolean> columnProtection;

    private void setLeftTopHeader() {
        leftTopHeader = "#";
        try {
            JSONObject jsonObject = coldata.getJSONObject(0);
            leftTopHeader = jsonObject.getString("col");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRowHeader() {
        rowHeader = new ArrayList<>();
        try {
            for (int i = 0; i < mRowsCount; i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                String st = coldata.getJSONObject(0).getString("col");
                rowHeader.add(jsonObject.getString(st));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setColumnHeader() {
        columnHeader = new ArrayList<>();
        try {
            for (int i = 1; i < mColsCount; i++) {
                JSONObject jsonObject = coldata.getJSONObject(i);
                columnHeader.add(jsonObject.getString("col"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setItemData() {
        itemData = new ArrayList<>();
        try {
            for (int j = 0; j < mRowsCount; j++)
                for (int i = 1; i < mColsCount; i++) {
                    String col = coldata.getJSONObject(i).getString("col");
                    itemData.add(data.getJSONObject(j).getString(col));
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setColumnProtection() {
        columnProtection = new ArrayList<>();
        try {
            for (int i = 1; i < mColsCount; i++) {
                columnProtection.add(coldata.getJSONObject(i).getBoolean("protected"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String TAG = "dexter";

    public Source(Context context) {
        BufferedReader reader = null;
        try {
            String mLine;
            StringBuilder sb;

            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("coldata.json")));
            sb = new StringBuilder();
            while ((mLine = reader.readLine()) != null) {
                sb.append(mLine);
            }
            coldata = new JSONArray(sb.toString());
            mColsCount = coldata.length();

            sb = new StringBuilder();
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("data.json")));
            while ((mLine = reader.readLine()) != null) {
                sb.append(mLine);
            }
            data = new JSONArray(sb.toString());
            mRowsCount = data.length();

        } catch (IOException e) {
        } catch (JSONException ex) {
            Log.d(TAG, "Source: Json Exception");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        setLeftTopHeader();
        setColumnHeader();
        setRowHeader();
        setItemData();
        setColumnProtection();

        data = null;
        coldata = null;
    }

    @Override
    public int getRowsCount() {
        return mRowsCount + 1;
    }

    @Override
    public int getColumnsCount() {
        return mColsCount;
    }

    @Override
    public String getFirstHeaderData() {
        return leftTopHeader;
    }

    @Override
    public String getRowHeaderData(int index) {
        index--;
        return rowHeader.get(index);
    }

    @Override
    public String getColumnHeaderData(int index) {
        index--;
        return columnHeader.get(index);
    }

    public void editItem(int rowIndex, int columnIndex, String data) {
        itemData.set(getItemIndexinList(rowIndex, columnIndex), data);
    }

    public int getItemIndexinList(int rowIndex, int columnIndex) {
        rowIndex--;
        columnIndex--;
        return (mColsCount - 1) * rowIndex + columnIndex;
    }

    @Override
    public String getItemData(int rowIndex, int columnIndex) {
        return itemData.get(getItemIndexinList(rowIndex, columnIndex));
    }

    @Override
    public Boolean getItemProtection(int columnIndex) {
        columnIndex--;
        return columnProtection.get(columnIndex);
    }
}
