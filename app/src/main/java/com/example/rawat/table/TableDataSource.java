package com.example.rawat.table;

/**
 * Created by WIN10 on 11/24/2017.
 */

public interface TableDataSource<TFirstHeaderDataType, TRowHeaderDataType, TColumnHeaderDataType, TItemDataType,TItemDataProtection> {

    int getRowsCount();

    int getColumnsCount();

    TFirstHeaderDataType getFirstHeaderData();

    TRowHeaderDataType getRowHeaderData(int index);

    TColumnHeaderDataType getColumnHeaderData(int index);

    TItemDataType getItemData(int rowIndex, int columnIndex);

    TItemDataProtection getItemProtection(int columnIndex);
}
