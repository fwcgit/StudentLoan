package com.qudaozhang.white.db;


import android.content.Context;
import android.database.Cursor;

public class SQLDataManager {
	private static SQLDataManager dataManager;
	private Context mContext;
	private SqlDataHelp sqlDataHelp;

	public static SQLDataManager getInstance() {
		if (dataManager == null) {
			dataManager = new SQLDataManager();
		}
		return dataManager;
	}

	public SqlDataHelp getSqlDataHelp() {
		return sqlDataHelp;
	}

	public void init(Context context) {
		mContext = context;
		sqlDataHelp = new SqlDataHelp(mContext);

	}

	/**
	 * 检查数据是否存在
	 * 
	 * @param tableName
	 * @param id
	 * @return
	 */
	public boolean isExistData(String tableName, String id, String whereId) {
		boolean b = false;
		Cursor cursor = sqlDataHelp.query(tableName, id, whereId);
		if (cursor.getCount() > 0) {
			b = true;
		}
		cursor.close();
		return b;
	}
	
	/**
	 * 检查数据是否存在
	 * 
	 * @param tableName
	 * @param id
	 * @return
	 */
	public boolean isExistData(String tableName, String[] id, String[] whereId) {
		boolean b = false;
		Cursor cursor = sqlDataHelp.queryWhere(tableName, id, whereId);
		if (cursor.getCount() > 0) {
			b = true;
		}
		cursor.close();
		return b;
	}

	
	
}
