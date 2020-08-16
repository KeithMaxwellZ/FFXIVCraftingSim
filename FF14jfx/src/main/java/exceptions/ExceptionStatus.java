package exceptions;

public enum ExceptionStatus
{
	Craft_Failed("制作失败"), 
	Craft_Success("制作成功"), 
	Not_HQ("现在无法使用（不是高品质状态）"), 
	No_Inner_Quiet("现在无法使用（没有内静/层数不足）"), 
	Inner_Quiet_Exists("现在无法使用（内静已经存在）"),
	Not_Turn_One("现在无法使用（只能在首次作业时使用）"),
	Waste_Not_Exist("现在无法使用（无法在俭约buff存在时使用俭约加工）"),
	No_Enough_CP("CP不足"),
	Maximun_Reached("现在无法使用（已达到最大使用次数）"),
	Now_Crafting("正在制作中，无法编辑技能位置"),
	Code_Error("代码错误，无法导入"),
	Replay_loading_Error("文件错误，无法正确导入"),
	;
	
	private String message;
	private ExceptionStatus(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
