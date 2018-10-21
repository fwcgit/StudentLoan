package com.qudaozhang.white.net.data;

public class UserAuthenInfo {
	/***
	 * 资料是否填写完整（0-不完整、大于0完整）
	 */
	public int ismember;
	/***
	 * 拍照认证是否审核（0-未审核、大于0已审核）
	 */
	public int isrz;
	/***
	 * 紧急联系人是否审核（0-未审核、1-已审核）
	 */
	public int islxr;
	/***
	 * 是否审核
	 */
	public int isshenhe;
	//学信网是否审核(0-未审核、大于0已审核")
	public int isxj;

	//运营商是否审核（0-未审核、大于0已审核）
	public int isyys;


}
