package com.studentloan.white.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.studentloan.white.db.data.AreaModel;
import com.studentloan.white.db.data.IndustryModel;
import com.studentloan.white.db.data.UniversityModel;
import com.studentloan.white.utils.SdUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 基础信息数据库
 * @author fu
 *
 */
public class BaseDataDB {
	private static BaseDataDB baseDataDB = new BaseDataDB();
	public static BaseDataDB getInstance(){return baseDataDB;};
	private BaseDataDB(){}

	private SQLiteDatabase liteDatabase;

	public void init(){
		if(liteDatabase != null && liteDatabase.isOpen()){
			liteDatabase.close();
		}
		if(SdUtil.getFilePath(SdUtil.DATA_DIR, "studentloan_2.db") != null){
			if(SdUtil.getFilePath(SdUtil.DATA_DIR, "studentloan_2.db").exists()){
				liteDatabase = SQLiteDatabase.openDatabase(SdUtil.getFilePath(SdUtil.DATA_DIR, "studentloan_2.db").getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
			}
		}
	}
	
	/***
	 * 所有大学信息
	 */
	public List<UniversityModel> queryUniversAll(){
		if(null == liteDatabase) return null;
		List<UniversityModel> models = new ArrayList<UniversityModel>();
		Cursor cursor =  liteDatabase.query("SchoolNew", null, null, null, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int areaCodeIndex  = cursor.getColumnIndex("areaCode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				UniversityModel model = new UniversityModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setAreaCode(cursor.getString(areaCodeIndex));
				models.add(model);
			}
			cursor.close();
		}
		return models;
	}
	
	/***
	 * 查询指定大学信息
	 * @param code
	 * @return
	 */
	public List<UniversityModel> queryUnivers(String code){
		List<UniversityModel> models = new ArrayList<UniversityModel>();
		Cursor cursor =  liteDatabase.query("UniversityModel", null, "code = ?", new String[]{code}, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int areaCodeIndex  = cursor.getColumnIndex("areaCode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				UniversityModel model = new UniversityModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setAreaCode(cursor.getString(areaCodeIndex));
				models.add(model);
			}
			cursor.close();
		}
		return models;
	}
	
	/***
	 * 查询指定大学信息
	 * @param code
	 * @return
	 */
	public UniversityModel queryUniversCode(String code){
		if(null == liteDatabase) return null;
		Cursor cursor =  liteDatabase.query("UniversityModel", null, "code = ?", new String[]{code}, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int areaCodeIndex  = cursor.getColumnIndex("areaCode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				UniversityModel model = new UniversityModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setAreaCode(cursor.getString(areaCodeIndex));
				return model;
			}
			cursor.close();
		}
		return null;
	}
	
	/***
	 * 所有行业信息
	 * @return
	 */
	public List<IndustryModel> queryIndustryAll(){
		if(null == liteDatabase) return null;
		List<IndustryModel> models = new ArrayList<IndustryModel>();
		Cursor cursor =  liteDatabase.query("IndustryModel", null, null, null, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int fcodeIndex  = cursor.getColumnIndex("fcode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				IndustryModel model = new IndustryModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setFcode(cursor.getString(fcodeIndex));
				models.add(model);
			}
			cursor.close();
		}
		return models;
	}
	
	/***
	 * 查询分类行业信息
	 * @return
	 */
	public List<IndustryModel> queryIndustryAll(String fcode){
		if(null == liteDatabase) return null;
		List<IndustryModel> models = new ArrayList<IndustryModel>();
		Cursor cursor =  liteDatabase.query("IndustryModel", null, "fcode = ?", new String[]{fcode}, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int fcodeIndex  = cursor.getColumnIndex("fcode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				IndustryModel model = new IndustryModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setFcode(cursor.getString(fcodeIndex));
				models.add(model);
			}
			cursor.close();
		}
		return models;
	}
	
	/***
	 * 查询所属行业信息
	 * @return
	 */
	public List<IndustryModel> queryIndustry(String code){
		if(null == liteDatabase) return null;
		if(TextUtils.isEmpty(code)){
			return null ;
		}
		List<IndustryModel> models = new ArrayList<IndustryModel>();
		Cursor cursor =  liteDatabase.query("IndustryModel", null, "code = ?", new String[]{code}, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int fcodeIndex  = cursor.getColumnIndex("fcode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				IndustryModel model = new IndustryModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setFcode(cursor.getString(fcodeIndex));
				models.add(model);
			}
			cursor.close();
		}
		return models;
	}
	
	/***
	 * 查询分类行业
	 * @return
	 */
	public Map<String,List<IndustryModel>> queryCateIndustry(){
		if(null == liteDatabase) return null;
		Map<String,List<IndustryModel>> map = new HashMap<String, List<IndustryModel>>();
		
		List<IndustryModel> models = new ArrayList<IndustryModel>();
		String fcode = "normal";
		
		Cursor cursor =  liteDatabase.query("IndustryModel", null, null, null, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int fcodeIndex  = cursor.getColumnIndex("fcode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				IndustryModel model = new IndustryModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setFcode(cursor.getString(fcodeIndex));
				
				if(model.getFcode().equals(fcode) || fcode.equals("normal") ){
					fcode = model.getFcode();
				}
				
				if(!model.getFcode().equals(fcode)){
					map.put(fcode, models);
					fcode = model.getFcode();
					models = new ArrayList<IndustryModel>();
					models.add(model);
				}else{
					models.add(model);
				}
				
			}
			cursor.close();
		}
		return map;
	}
	
	/***
	 * 所有地区
	 * @return
	 */
	public List<AreaModel> queryAreaAll(){
		if(null == liteDatabase) return null;
		List<AreaModel> models = new ArrayList<AreaModel>();
		Cursor cursor =  liteDatabase.query("AreaModel", null, null, null, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int fcodeIndex  = cursor.getColumnIndex("fcode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				AreaModel model = new AreaModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setFcode(cursor.getString(fcodeIndex));
				models.add(model);
			}
			cursor.close();
		}
		return models;
	}
	
	/***
	 * 查询分类地区
	 * @return
	 */
	public List<AreaModel> queryAreaAll(String fcode){
		if(null == liteDatabase) return null;
		List<AreaModel> models = new ArrayList<AreaModel>();
		Cursor cursor =  liteDatabase.query("AreaModel", null, "fcode = ?", new String[]{fcode}, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int fcodeIndex  = cursor.getColumnIndex("fcode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				AreaModel model = new AreaModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setFcode(cursor.getString(fcodeIndex));
				models.add(model);
			}
			cursor.close();
		}
		return models;
	}
	
	/***
	 * 查询分类地区
	 * @return
	 */
	public Map<String,List<AreaModel>> queryCateArea(){
		if(null == liteDatabase) return null;
		Map<String,List<AreaModel>> map = new HashMap<String, List<AreaModel>>();
		
		List<AreaModel> models = new ArrayList<AreaModel>();
		String fcode = "normal";
		
		Cursor cursor =  liteDatabase.query("AreaModel", null, null, null, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int fcodeIndex  = cursor.getColumnIndex("fcode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				AreaModel model = new AreaModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setFcode(cursor.getString(fcodeIndex));
				if(TextUtils.isEmpty(model.getFcode())){
					continue;
				}
				if(model.getFcode().equals(fcode) || fcode.equals("normal") ){
					fcode = model.getFcode();
				}
				
				if(!model.getFcode().equals(fcode)){
					map.put(fcode, models);
					fcode = model.getFcode();
					models = new ArrayList<AreaModel>();
					models.add(model);
				}else{
					models.add(model);
				}
				
			}
			cursor.close();
		}
		return map;
	}
	
	/***
	 * 获取所有省级
	 * @return
	 */
	public List<AreaModel> queryProvinceAll(){
		if(null == liteDatabase) return null;
		List<AreaModel> models = new ArrayList<AreaModel>();
		Cursor cursor =  liteDatabase.query("AreaModel", null, "fcode = ?", new String[]{""}, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int fcodeIndex  = cursor.getColumnIndex("fcode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				AreaModel model = new AreaModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setFcode(cursor.getString(fcodeIndex));
				models.add(model);
			}
			cursor.close();
		}
		return models;
	}
	
	
	/***
	 * 获取单个省级
	 * @return
	 */
	public List<AreaModel> queryProvince(String code){
		if(null == liteDatabase) return null;
		List<AreaModel> models = new ArrayList<AreaModel>();
		Cursor cursor =  liteDatabase.query("AreaModel", null, "fcode = ?", new String[]{code}, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int fcodeIndex  = cursor.getColumnIndex("fcode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				AreaModel model = new AreaModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setFcode(cursor.getString(fcodeIndex));
				models.add(model);
			}
			cursor.close();
		}
		return models;
	}
	
	/***
	 * 获取城市
	 * @return
	 */
	public AreaModel queryCity(String code){
		if(null == liteDatabase) return null;
		Cursor cursor =  liteDatabase.query("AreaModel", null, "code = ?", new String[]{code}, null, null, null);
		int codeIndex = cursor.getColumnIndex("code");
		int nameIndex = cursor.getColumnIndex("name");
		int fcodeIndex  = cursor.getColumnIndex("fcode");
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				AreaModel model = new AreaModel();
				model.setCode(cursor.getString(codeIndex));
				model.setName(cursor.getString(nameIndex));
				model.setFcode(cursor.getString(fcodeIndex));
				return model;
			}
			cursor.close();
		}
		return null;
	}
	
	public void closeDb(){
		if(liteDatabase != null){
			liteDatabase.close();
		}
	}
}
