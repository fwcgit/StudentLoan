package com.qudaozhang.white.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlDataHelp extends SQLiteOpenHelper {
	private static int version = 11;
	private static String DbName = "xyb.db";

	private SQLiteDatabase liteDatabase;

	// 好友表
	public static final String ALL_FRIEND_NOTE_TABLE = "friends_";

	public static final String FRIEND_ID_KEY = "friendid";
	public static final String HX_USER_NAME_KEY = "hx_username";
	public static final String OBID_KEY = "obid";
	public static final String FRIEND_DATA_KEY = "data";
	public static final String FRIEND_TYPE_KEY = "friendtype";
			

	// 群组表
	public static final String GROUP_TABLE = "group_";
	public static final String GROUP_ID_KEY = "groupid";
	public static final String GROUP_DATA_KEY = "data";
	public static final String GROUP_NAME_KEY = "groupname";
	public static final String GROUP_HEAD_KEY = "group_head";

	
	//好友用户信息表
	public static final String FRIEND_USER_INFO_TABLE = "userinfo_";
	public static final String FRIEND_USER_INFO_DATA_KEY = "data";
	public static final String FRIEND_USER_INFO_HEAD_KEY = "head";
	public static final String FRIEDN_USER_NAME_KEY = "name";
			
	private String userTable = "";

	public SqlDataHelp(Context context) {
		super(context, DbName, null, version);
		//根据账户得到表名
	}

	public void resetTable(){
		//重置表名
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		this.liteDatabase = db;
//		createTable();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//		db.execSQL("DROP TABLE IF EXISTS " + ALL_FRIEND_NOTE_TABLE + userTable);
//		db.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE + userTable);
//		db.execSQL("DROP TABLE IF EXISTS " + FRIEND_USER_INFO_TABLE + userTable);
		
		onCreate(db);
	}

	private synchronized void createTable() {
		
		createAllFriendTable();
		
		createUserFriendTable();
		
		createGroupTable();

	}

	
	public void createAllFriendTable(){
		StringBuffer allChatNoteSb = new StringBuffer();
		allChatNoteSb.append("(");
		allChatNoteSb.append("id integer primary key,");
		allChatNoteSb.append(FRIEND_ID_KEY + " text,");
		allChatNoteSb.append(HX_USER_NAME_KEY + " text,");
		allChatNoteSb.append(OBID_KEY + " text,");
		allChatNoteSb.append(FRIEND_TYPE_KEY + " text,");
		allChatNoteSb.append(FRIEND_DATA_KEY + " text,");
		allChatNoteSb.append(FRIEND_USER_INFO_HEAD_KEY + " text,");
		allChatNoteSb.append(FRIEDN_USER_NAME_KEY+ " text");
		allChatNoteSb.append(")");
		this.liteDatabase.execSQL("CREATE TABLE " + ALL_FRIEND_NOTE_TABLE
				+ userTable + allChatNoteSb.toString());
	}
	
	public void createUserFriendTable(){
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append("id integer primary key,");
		sb.append(HX_USER_NAME_KEY + " text,");
		sb.append(OBID_KEY + " text,");
		sb.append(FRIEND_DATA_KEY + " text,");
		sb.append(FRIEND_USER_INFO_HEAD_KEY + " text,");
		sb.append(FRIEDN_USER_NAME_KEY+ " text");
		sb.append(")");
		this.liteDatabase.execSQL("CREATE TABLE " + FRIEND_USER_INFO_TABLE
				+ userTable + sb.toString());
	}
	
	public void createGroupTable(){
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append("id integer primary key,");
		sb.append(GROUP_ID_KEY + " text,");
		sb.append(GROUP_DATA_KEY + " text,");
		sb.append(GROUP_NAME_KEY + " text,");
		sb.append(GROUP_HEAD_KEY + " text");
		sb.append(")");
		this.liteDatabase.execSQL("CREATE TABLE " + GROUP_TABLE
				+ userTable + sb.toString());
	}
	public synchronized void insert(String tableName, ContentValues values) {
		liteDatabase = getWritableDatabase();
		liteDatabase.close();
	}

	public synchronized void update(String tableName, ContentValues values, String whereId,
			String zhuid) {
		liteDatabase = getWritableDatabase();
		liteDatabase.update(tableName + userTable, values, whereId + " = ? ",
				new String[] { zhuid });
		liteDatabase.close();
	}

	// public Cursor query(String tableName,String zhuid,String whereId) {
	// liteDatabase = getReadableDatabase();
	// Cursor c = null;
	// String sql = null;
	// if(zhuid == null){
	// sql = "SELECT * FROM " + tableName+userTable;
	//
	// }else{
	// sql = "SELECT * FROM " + tableName +userTable+" WHERE "+ whereId + "='"+
	// zhuid +"'";
	// }
	// c = liteDatabase.rawQuery(sql, null);
	// return c;
	// }

	// 上面的方法,如果查找的table不存在会直接抛出异常,
	// 主要是onCreate(SQLiteDatabase db)只会执行一次,切换帐号再登录的时候不会再次执行,除非删除数据库
	// 这里就是在在查找不到表的时候创建一个新表
	// 这里就是在rawQuery的方法加个try,在异常抛出时创建新表
	public  synchronized Cursor query(String tableName, String zhuid, String whereId) {
		liteDatabase = getReadableDatabase();
		Cursor c = null;
		String sql = null;
		if (zhuid == null) {
			sql = "SELECT * FROM " + tableName + userTable;

		} else {
			sql = "SELECT * FROM " + tableName + userTable + " WHERE "
					+ whereId + "='" + zhuid + "'";
		}
		try {
			c = liteDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
			createTable();// 手动调用创建表的方法
			c = liteDatabase.rawQuery(sql, null);
		}
		return c;
	}
	
	public synchronized Cursor queryWhere(String tableName, String[] zhuid, String[] whereId) {
		liteDatabase = getReadableDatabase();
		Cursor c = null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < whereId.length; i++) {
			if(i == whereId.length-1){
				sb.append(whereId[i]+" = ?");
			}else{
				sb.append(whereId[i]+" = ?"+" and ");
			}
		}
		try {
			c = liteDatabase.query(tableName+ userTable, null, sb.toString(), zhuid, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			createTable();// 手动调用创建表的方法
			c = liteDatabase.query(tableName+ userTable, null, sb.toString(), zhuid, null, null, null);
		}
		return c;
	}

	public void delAll(String tableName) {

		liteDatabase = getWritableDatabase();

		liteDatabase.delete(tableName + userTable, null, null);

		liteDatabase.close();
	}

	public void del(String tableName, String whereId, String zhuid) {

		liteDatabase = getWritableDatabase();

		liteDatabase.delete(tableName + userTable, whereId + "=?",
				new String[] { zhuid });
		liteDatabase.close();
	}
	
	public void del(String tableName, String[] whereId, String[] zhuid) {

		liteDatabase = getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < whereId.length; i++) {
			if(i == whereId.length-1){
				sb.append(whereId[i]+" = '"+zhuid[i]+" ' ");
			}else{
				sb.append(whereId[i]+" = ' "+zhuid[i]+" ' and ");
			}
		}
//		liteDatabase.delete(tableName + userTable, whereId + "=?",zhuid );
		liteDatabase.execSQL("delete from "+ userTable + " where " + sb.toString());
		liteDatabase.close();
	}

	public void close() {
		if (liteDatabase != null)
			liteDatabase.close();
	}

	public SQLiteDatabase getLiteDatabase() {
		return liteDatabase;
	}

	public void setLiteDatabase(SQLiteDatabase liteDatabase) {
		this.liteDatabase = liteDatabase;
	}

}
