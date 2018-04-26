package com.studentloan.white.mode.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.studentloan.white.R;
import com.studentloan.white.utils.ConvertUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/****
 * fu
 */
@EActivity(R.layout.activity_help_detail_layout)
public class HelpDetailActivity extends BaseActivity {
	@ViewById
	public ExpandableListView elistView;

	@Extra
	public int index;

	private String[] renzhenTitle = new String[]{"1.申请租赁需要哪些认证？",
	"2.如何更改身份认证信息？","3.目前平台支持的银行有哪些？","4.银行卡绑定不成功有哪些原因？","5.如何更换收款银行卡？",
	"6.什么是手机卡运营商服务密码？","7.手机运营商认证失败的原因有哪些",
	"8.申请租赁的年龄范围？"};
	private String[][] renzhen = new String[][]{
			{"答：用户注册急借白卡后.需要完成信息认证才可申请租赁"},
			{"答：身份信息保存后.用户无法自行更改."},
			{"答：目前支持：中国工商银行.中国农业银行.中国建设银行.招商银行.华夏银行.中信实业银行.中国光大银行.广东发展银行.中国民生银行.兴业银行.国家邮政局邮政储蓄局.平安银行.中国银行.上海浦东发展银行.交通银行.后续还会支持更多银行"},
			{"答：可能有以下3种原因：",
					"（1) 银行卡信息填写有误.请核实后重新填写",
					"（2) 银行卡预留手机号或验证码填写有误.请核实后重新填写",
					"（3) 绑定的银行卡不是你本人的.请用本人的银行卡进行绑定"},
			{"答：你的手机回购后.可在-（收款银行卡）-（重新绑定一张卡）在切换成主卡即可"},
			{"答：手机卡运营商服务密码.是你的手机卡在运营商获取服务时的认证密码.修改或找回手机运营商服务密码.请联系你的手机服务商"},
			{"答：可能有以下5原因：",
					"（1）你当前的注册手机号不是本人在运营商的实名手机号.请更换为本人实名的手机号",
					"（2）你填写的手机运营商服务密码不正确.请核实后重新填写",
					"（3）你在运营商哪里开启了账单保护功能.联系运营商关闭该功能即可",
					"（4）运营商出账期无法进行手机认证.请改日再试",
					"（5）由于网路不稳定或繁忙导致认证失败.请稍后再试"},
			{"答：本公司暂时只提供满18周岁到36周岁的租赁活动"}};

	private String[] jiekuanTitle = new String[]{"1.如何使用本软件租赁？","2.租赁期限是多久？",
			"3.是否可以续租？","4.提交出售租赁多久打款？","5.审核被拒绝的原因有哪些？",
			"6.绑定银行卡无法收到打款怎么办？"};
	private String[][] jiekuan = new String[][]{
			{"答：用户认证完信息提交审核.审核通过.就可以租赁了."},
			{"答：目前租赁期限为15天."},
			{"答：可以续租.续租最多2次.每次8天"},
			{"答：提交出售租赁后会立即打款.一般半小时内完成.请耐心等待"},
			{"答：可能有以下3种原因：",
					"（1）认证信息不全面.请将认证信息填写完整",
					"（2）认证信息有误.请核实后重新填写",
					"（3）综合评分不足也可能被拒绝"},
			{"答：银行卡收不到打款.租赁记录会提醒放款失败.请更换银行卡重新提交申请"},
			};

	private String[] huankuanTitle = new String[]{"1.可以通过哪些方式进行回购？","2.每种方式需要多久更新回购状态？",
			"3.如何进行主动回购","4.如何进行支付宝回购？","5.如何确认支付宝回购是否成功？？","6.如何查看到期回购日和回购金额？",
	"7.可以提前回购吗"};
	private String[][]huankuan = new String[][]{
			{"答：目前平台支持2种方式回购.主动充值回购和支付宝回购"},
			{"答：（1）主动充值回购.一般情况下还款成功会立即更新还款状态",
			"（2）支付宝回购.转账成功后.一般1小时之内更新回购状态.支付宝回购请在早上9:00-下午7:00之内进行以免对回购状态更新不及时.对你造成不便请谅解"},
			{"答：你可在立即回购页面点击回购手机.选择一张银行卡输入交易密码即可完成回购"},
			{"答：进入支付宝首页.点击（转账）选择（转到支付宝账户）输入支付宝账户.点击（下一步）输入转账金额.并备注姓名+注册手机号.点击（确认转账）.输入支付密码即可完成回购.支付宝转账金额须与回购页面提示的回购金额一致.否则将无法成功回购.转账成功后.请及时关注回购状态"},
			{"答：还款后请及时到（租赁记录）查看回购状态.当页面显示“已回购”时.则表示回购成功"},
			{"答：你可在回购页面查看你的到期回购日和回购金额"},
			{"答：可以提前回购.回购金额不变你可在回购页面点击(立即回购).即可操作回购"}};

	private String[] feiyongTitle = new String[]{"1.租赁费用如何收取？","2.续租的费用如何收取？","3.违约后的费用如何收取？"};
	private String[][]feiyong = new String[][]{
			{"答：用户在租赁手机时.平台会收取租赁费用.租赁费用在租赁时收取"},
			{"答；目前续租可以续2次.每次8天.续租费用将按照签署合同上每天租赁费用共同收取.续租只能在租赁未到期之前续租.已但违约将不能续租"},
			{"答：违约回购.平台会收取违约费用.平台按16元/每天.收取违约金"}};

	private String[] qitaTitle = new String[]{"1.收不到验证码怎么办？","2.如何更改手机号？"};
	private String[][] qita = new String[][]{
			{"答：（1）重启手机或清理手机缓存",
			"（2）检查一下是否被手机安全软件拦截了",
			"（3）检查一下是否退订过短信.如果退订过短信.联系客服重新接受短信"},
			{"答：目前不支持用户自行更改注册手机号.请联系客服.我们将在核实身份后为您更改"}};

	String group[];
	String groupList[][];

	@Override
	@AfterViews
	public void initViews() {
		super.initViews();

		setTitleText("帮助中心");

		elistView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {

			}
		});

		elistView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {

			}
		});

		elistView.setGroupIndicator(null);


		if(index == 0){
			group = renzhenTitle;
			groupList = renzhen;
			elistView.setAdapter(new Adapter());
		}else if(index == 1){
			group = jiekuanTitle;
			groupList = jiekuan;
			elistView.setAdapter(new Adapter());
		}else if(index == 2){
			group = huankuanTitle;
			groupList = huankuan;
			elistView.setAdapter(new Adapter());
		}else if(index == 3){
			group = feiyongTitle;
			groupList = feiyong;
			elistView.setAdapter(new Adapter());
		}else if(index == 4){
			group = qitaTitle;
			groupList = qita;
			elistView.setAdapter(new Adapter());
		}
	}

	//自定义适配器
	class Adapter extends BaseExpandableListAdapter {
		//获取子元素对象
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}
		//获取子元素Id
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}
		//加载子元素并显示
		@Override
		public View getChildView(final int groupPosition, final int childPosition,
								 boolean isLastChild, View convertView, ViewGroup parent) {
			View view=null;
			ChildHolder childholder = null;
			if(convertView!=null){
				view = convertView;
				childholder = (ChildHolder) view.getTag();
			}else{
				view = View.inflate(HelpDetailActivity.this,R.layout.help_child_layout, null);
				childholder = new ChildHolder();

				childholder.content = (TextView) view.findViewById(R.id.childTv);
				view.setTag(childholder);
			}

			childholder.content.setText(groupList[groupPosition][childPosition]);

			if(groupList[groupPosition].length == 1){
				childholder.content.setPadding(0, ConvertUtils.dip2px(HelpDetailActivity.this,12f),0,ConvertUtils.dip2px(HelpDetailActivity.this,12f));
			}else{
				if(childPosition == 0){
					childholder.content.setPadding(0, ConvertUtils.dip2px(HelpDetailActivity.this,12f),0,0);
				}else {
					if (childPosition != groupList[groupPosition].length - 1) {
						childholder.content.setPadding(0,0,0,0);
					}
				}
				if(childPosition == groupList[groupPosition].length -1){
					childholder.content.setPadding(0,0,0, ConvertUtils.dip2px(HelpDetailActivity.this,12f));
				}else{
					if(childPosition != 0){
						childholder.content.setPadding(0,0,0,0);
					}
				}
			}
			return view;
		}
		//获取子元素数目
		@Override
		public int getChildrenCount(int groupPosition) {
			return groupList[groupPosition].length;
		}
		//获取组元素对象
		@Override
		public Object getGroup(int groupPosition) {
			return groupList[groupPosition];
		}
		//获取组元素数目
		@Override
		public int getGroupCount() {
			return groupList.length;
		}
		//获取组元素Id
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}
		//加载并显示组元素
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
								 View convertView, ViewGroup parent) {
			View view=null;
			GroupHolder groupholder = null;
			if(convertView!=null){
				view = convertView;
				groupholder = (GroupHolder) view.getTag();
			}else{
				view = View.inflate(HelpDetailActivity.this,R.layout.help_group_layout, null);
				groupholder =new GroupHolder();
				groupholder.name = (TextView) view.findViewById(R.id.groupTv);
				groupholder.mImage = (ImageView) view.findViewById(R.id.groupImg);

				view.setTag(groupholder);
			}
			groupholder.name.setText(group[groupPosition]);

			if(isExpanded){
				groupholder.mImage.setImageResource(R.drawable.icon_help_arrow_down);
			}else{
				groupholder.mImage.setImageResource(R.drawable.icon_help_arrow_right);
			}
			return view;
		}

		@Override
		public boolean hasStableIds() {

			return true;
		}


		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {

			return false;
		}

	}

	class GroupHolder{
		TextView name;
		ImageView mImage;
	}

	class ChildHolder{
		TextView content;
	}

}
