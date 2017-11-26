package com.example.rawat.table;

import android.content.Context;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WIN10 on 11/24/2017.
 */

public class Source implements TableDataSource<String, String, String, String, Boolean, Integer> {
    private static final String COLUMN_NAME_LABEL = "col";
    private static final String PROTECTION_LABEL = "protected";
    private static final String INPUT_TYPE_LABEL = "inputType";

    public static final String INPUT_TYPE_NUMBER = "number";
    public static final String INPUT_TYPE_TEXT = "text";

    int mRowsCount;
    int mColsCount;
    JSONArray coldata;
    JSONArray data;
    Context context;

    private String leftTopHeader;
    private List<String> rowHeader;
    private List<String> columnHeader;
    private List<String> itemData;
    private List<Boolean> columnProtection;
    private List<String> columnInputType;

    private void setLeftTopHeader() {
        leftTopHeader = "#";
        try {
            JSONObject jsonObject = coldata.getJSONObject(0);
            leftTopHeader = jsonObject.getString(COLUMN_NAME_LABEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRowHeader() {
        rowHeader = new ArrayList<>();
        try {
            for (int i = 0; i < mRowsCount; i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                String st = coldata.getJSONObject(0).getString(COLUMN_NAME_LABEL);
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
                columnHeader.add(jsonObject.getString(COLUMN_NAME_LABEL));
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
                    String col = coldata.getJSONObject(i).getString(COLUMN_NAME_LABEL);
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
                columnProtection.add(coldata.getJSONObject(i).getBoolean(PROTECTION_LABEL));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setColumnInputType() {
        columnInputType = new ArrayList<>();
        try {
            for (int i = 1; i < mColsCount; i++) {
                columnInputType.add(coldata.getJSONObject(i).getString(INPUT_TYPE_LABEL));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String TAG = "dexter";

    public Source(Context context) {
        this.context = context;
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
        setColumnInputType();

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

    @Override
    public Integer getItemInputType(int columnIndex) {
        columnIndex--;
        String st = columnInputType.get(columnIndex);
        if (st.equals(INPUT_TYPE_NUMBER))
            return InputType.TYPE_NUMBER_FLAG_DECIMAL;
        else if (st.equals(INPUT_TYPE_TEXT))
            return InputType.TYPE_CLASS_TEXT;
        return InputType.TYPE_NULL;
    }

    public boolean saveInExcel() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState) || !Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return false;
        }
        boolean success = false;

        Cell c = null;

        //New Workbook
        Workbook wb = new HSSFWorkbook();
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Tap2Trade");
        sheet1.protectSheet("com.labs.r2.pinalarm");

        CellStyle unlockedCellStyle = wb.createCellStyle();
        unlockedCellStyle.setLocked(false);

        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue(leftTopHeader);

        for (int i = 0; i < mColsCount; i++) {
            c = row.createCell(i + 1);
            c.setCellValue(columnHeader.get(i));
        }

        for (int i = 0; i < rowHeader.size(); i++) {
            row = sheet1.createRow(i + 1);
            c = row.createCell(0);
            c.setCellValue(rowHeader.get(i));
            int rowIndex = columnHeader.size() * i;
            for (int j = 0; j < columnHeader.size(); j++) {
                c = row.createCell(j + 1);
                c.setCellValue(itemData.get(rowIndex + j));
                if (columnProtection.get(j))
                    c.setCellStyle(unlockedCellStyle);
            }
        }

        File file = new File(context.getExternalFilesDir(null), "Tap2Trade.xls");
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }
}
