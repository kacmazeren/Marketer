package com.example.marketer10;

import android.provider.BaseColumns;

public final class ShoppingListContract {

    private ShoppingListContract() {
    }

    /* Inner class that defines the table contents */
    public static class ShoppingListEntry implements BaseColumns {
        public static final String TABLE_NAME = "shopping_list";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_SUPERMARKET = "supermarket";
        public static final String COLUMN_NAME_KIND = "kind";
        public static final String COLUMN_NAME_GOODS = "goods";
        public static final String COLUMN_NAME_PRODUCTTYPE = "productType";
        public static final String COLUMN_NAME_BRAND = "brand";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_UNITPRICE = "unitPrice";
        public static final String COLUMN_NAME_MEMBER_NUMBER = "memberNumber";

    }
}
