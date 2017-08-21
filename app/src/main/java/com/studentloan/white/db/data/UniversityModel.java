package com.studentloan.white.db.data;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class UniversityModel{
	private String code;
	private String name;
	private String areaCode;
	private String c = "";
	private boolean type;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		String c = textTopinyin(name);
		if(c != null){
			String sName = name.substring(0,1);
			if(sName.equals("重") || sName.equals("长")){
				this.c = "C";
			}else{
				this.c = c.substring(0,1);
			}
		}
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public boolean isType() {
		return type;
	}
	public void setType(boolean type) {
		this.type = type;
	}
	
	
	
	public String textTopinyin(String str){
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();  
		  
		// UPPERCASE：大写  (ZHONG)  
		// LOWERCASE：小写  (zhong)  
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);  
		  
		// WITHOUT_TONE：无音标  (zhong)  
		// WITH_TONE_NUMBER：1-4数字表示英标  (zhong4)  
		// WITH_TONE_MARK：直接用音标符（必须WITH_U_UNICODE否则异常）  (zhòng)  
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
		  
		// WITH_V：用v表示ü  (nv)  
		// WITH_U_AND_COLON：用"u:"表示ü  (nu:)  
		// WITH_U_UNICODE：直接用ü (nü)  
		format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);  
		
		try {
			String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(str.charAt(0), format);
			
			return pinyin[0];
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return "";
	}
}
