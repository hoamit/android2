package hoanguyen.nguyenduchoa.assigment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ProductManagement.db";
    private static final int DATABASE_VERSION = 1;

    // Table NguoiDung (User)
    public static final String TABLE_NGUOI_DUNG = "NguoiDung";
    public static final String COLUMN_TEN_DANG_NHAP = "tendangnhap";
    public static final String COLUMN_MAT_KHAU = "matkhau";
    public static final String COLUMN_HO_TEN = "hoten";

    // Table SanPham (Product)
    public static final String TABLE_SAN_PHAM = "SanPham";
    public static final String COLUMN_MA_SP = "masp";
    public static final String COLUMN_TEN_SP = "tensp";
    public static final String COLUMN_GIA_BAN = "giaban";
    public static final String COLUMN_SO_LUONG = "soluong";

    // Create table NguoiDung
    private static final String CREATE_TABLE_NGUOI_DUNG =
            "CREATE TABLE " + TABLE_NGUOI_DUNG + " (" +
                    COLUMN_TEN_DANG_NHAP + " TEXT PRIMARY KEY, " +
                    COLUMN_MAT_KHAU + " TEXT NOT NULL, " +
                    COLUMN_HO_TEN + " TEXT NOT NULL)";

    // Create table SanPham
    private static final String CREATE_TABLE_SAN_PHAM =
            "CREATE TABLE " + TABLE_SAN_PHAM + " (" +
                    COLUMN_MA_SP + " TEXT PRIMARY KEY, " +
                    COLUMN_TEN_SP + " TEXT NOT NULL, " +
                    COLUMN_GIA_BAN + " INTEGER NOT NULL, " +
                    COLUMN_SO_LUONG + " INTEGER NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NGUOI_DUNG);
        db.execSQL(CREATE_TABLE_SAN_PHAM);
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NGUOI_DUNG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAN_PHAM);
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Sample products
        insertProduct(db, "SP001", "Bánh quy bơ LU Pháp", 45000, 10);
        insertProduct(db, "SP002", "Snack mực lăn muối ớt", 15000, 25);
        insertProduct(db, "SP003", "Snack khoai tây Lays", 25000, 15);
        insertProduct(db, "SP004", "Bánh gạo One One", 12000, 30);
        insertProduct(db, "SP005", "Kẹo sữa sô cô la", 8000, 50);
    }

    private void insertProduct(SQLiteDatabase db, String maSP, String tenSP, int giaBan, int soLuong) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MA_SP, maSP);
        values.put(COLUMN_TEN_SP, tenSP);
        values.put(COLUMN_GIA_BAN, giaBan);
        values.put(COLUMN_SO_LUONG, soLuong);
        db.insert(TABLE_SAN_PHAM, null, values);
    }

    // User registration
    public boolean registerUser(String username, String password, String fullName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEN_DANG_NHAP, username);
        values.put(COLUMN_MAT_KHAU, password);
        values.put(COLUMN_HO_TEN, fullName);

        long result = db.insert(TABLE_NGUOI_DUNG, null, values);
        return result != -1;
    }

    // Check user login
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_TEN_DANG_NHAP};
        String selection = COLUMN_TEN_DANG_NHAP + " = ?" + " AND " + COLUMN_MAT_KHAU + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_NGUOI_DUNG, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();

        return cursorCount > 0;
    }

    // Check if username exists
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_TEN_DANG_NHAP};
        String selection = COLUMN_TEN_DANG_NHAP + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_NGUOI_DUNG, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();

        return cursorCount > 0;
    }

    // Product methods
    public boolean addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MA_SP, product.getMaSP());
        values.put(COLUMN_TEN_SP, product.getName());
        values.put(COLUMN_GIA_BAN, product.getPrice());
        values.put(COLUMN_SO_LUONG, product.getQuantity());

        long result = db.insert(TABLE_SAN_PHAM, null, values);
        return result != -1;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_MA_SP, COLUMN_TEN_SP, COLUMN_GIA_BAN, COLUMN_SO_LUONG};

        Cursor cursor = db.query(TABLE_SAN_PHAM, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getString(cursor.getColumnIndex(COLUMN_TEN_SP)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_GIA_BAN)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_SO_LUONG))
                );
                product.setMaSP(cursor.getString(cursor.getColumnIndex(COLUMN_MA_SP)));
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }

    public boolean updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEN_SP, product.getName());
        values.put(COLUMN_GIA_BAN, product.getPrice());
        values.put(COLUMN_SO_LUONG, product.getQuantity());

        String whereClause = COLUMN_MA_SP + " = ?";
        String[] whereArgs = {product.getMaSP()};

        int result = db.update(TABLE_SAN_PHAM, values, whereClause, whereArgs);
        return result > 0;
    }

    public boolean deleteProduct(String maSP) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_MA_SP + " = ?";
        String[] whereArgs = {maSP};

        int result = db.delete(TABLE_SAN_PHAM, whereClause, whereArgs);
        return result > 0;
    }

    public Product getProductByMaSP(String maSP) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_MA_SP, COLUMN_TEN_SP, COLUMN_GIA_BAN, COLUMN_SO_LUONG};
        String selection = COLUMN_MA_SP + " = ?";
        String[] selectionArgs = {maSP};

        Cursor cursor = db.query(TABLE_SAN_PHAM, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            Product product = new Product(
                    cursor.getString(cursor.getColumnIndex(COLUMN_TEN_SP)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_GIA_BAN)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_SO_LUONG))
            );
            product.setMaSP(cursor.getString(cursor.getColumnIndex(COLUMN_MA_SP)));
            cursor.close();
            return product;
        }
        cursor.close();
        return null;
    }
}
