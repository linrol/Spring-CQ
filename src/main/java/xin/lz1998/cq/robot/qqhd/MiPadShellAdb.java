package xin.lz1998.cq.robot.qqhd;

import xin.lz1998.cq.util.JaveShellUtil;

public class MiPadShellAdb {

	public static void addFriend(String qq) throws InterruptedException {
		int random=(int)(Math.random()*3+6);
		System.out.println("选中消息tab");
		JaveShellUtil.ExecCommand("adb shell input tap 28 643");
		Thread.sleep(random * 1000);
		
		System.out.println("选中qq输入框");
		JaveShellUtil.ExecCommand("adb shell input tap 185 79");
		Thread.sleep(random * 1000);
		
		//输入qq号码
		System.out.println("输入qq号码");
		JaveShellUtil.ExecCommand("adb shell input text " + qq);
		Thread.sleep(random * 1000);
		
		//搜索
		System.out.println("搜索");
		JaveShellUtil.ExecCommand("adb shell input tap 128 179");
		Thread.sleep(random * 1000);
		
		//加好友
		System.out.println("加好友");
		JaveShellUtil.ExecCommand("adb shell input tap 552 1738");
		Thread.sleep(random * 1000);
		
		//下一步
		System.out.println("下一步");
		JaveShellUtil.ExecCommand("adb shell input tap 1058 62");
		Thread.sleep(random * 1000);
		
		//发送
		System.out.println("发送");
		JaveShellUtil.ExecCommand("adb shell input tap 1058 62");
		Thread.sleep(random * 1000);
		
		//返回
		System.out.println("返回");
		JaveShellUtil.ExecCommand("adb shell input tap 57 59");
		Thread.sleep(random * 1000);
	
		//返回
		System.out.println("补偿返回");
		JaveShellUtil.ExecCommand("adb shell input tap 57 59");
		Thread.sleep(random * 1000);
	}
	
	public static void addFriendByGroup(String qq) throws InterruptedException {
		int random=(int)(Math.random()*3+3);
		
		System.out.println("选中qq输入框");
		JaveShellUtil.ExecCommand("adb shell input tap 500 200");
		Thread.sleep(random * 1000);
		
		//输入qq号码
		System.out.println("输入qq号码");
		JaveShellUtil.ExecCommand("adb shell input text " + qq);
		Thread.sleep(random * 1000);
		
		//加好友
		System.out.println("加好友");
		JaveShellUtil.ExecCommand("adb shell input tap 1100 230");
		Thread.sleep(random * 1000);
		
		//发送
		System.out.println("发送");
		JaveShellUtil.ExecCommand("adb shell input tap 1130 100");
		Thread.sleep(random * 1000);
		
		System.out.println("发送失败时取消");
		JaveShellUtil.ExecCommand("adb shell input tap 90 100");
		Thread.sleep(random * 1000);
		
		//返回
		System.out.println("取消");
		JaveShellUtil.ExecCommand("adb shell input tap 1100 119");
		Thread.sleep(random * 1000);
	}

	public static void reloadResource(String groupId) throws InterruptedException {
		int random=(int)(Math.random()*3+3);
		
		System.out.println("Home按键");
		JaveShellUtil.ExecCommand("adb shell input keyevent 3");
		Thread.sleep(random * 1000);
		
		System.out.println("任务按键");
		JaveShellUtil.ExecCommand("adb shell input keyevent 82");
		Thread.sleep(random * 1000);
		
		System.out.println("清理任务");
		JaveShellUtil.ExecCommand("adb shell input tap 600 1700");
		Thread.sleep(random * 1000);
		
		System.out.println("打开飞行设置页面");
		JaveShellUtil.ExecCommand("adb shell am start -a android.settings.AIRPLANE_MODE_SETTINGS");
		Thread.sleep(random * 1000);
		
		System.out.println("打开飞行模式");
		JaveShellUtil.ExecCommand("adb shell input tap 1088 220");
		Thread.sleep(random * 1000);
		
		System.out.println("关闭飞行模式");
		JaveShellUtil.ExecCommand("adb shell input tap 1088 220");
		Thread.sleep(random * 1000);
		
		System.out.println("Home按键");
		JaveShellUtil.ExecCommand("adb shell input keyevent 3");
		Thread.sleep(random * 1000);
		
		System.out.println("任务按键");
		JaveShellUtil.ExecCommand("adb shell input keyevent 82");
		Thread.sleep(random * 1000);
		
		System.out.println("清理任务");
		JaveShellUtil.ExecCommand("adb shell input tap 600 1700");
		Thread.sleep(random * 1000);
		
		System.out.println("打开QQ应用");
		JaveShellUtil.ExecCommand("adb shell am start -n com.tencent.mobileqq/.activity.SplashActivity");
		Thread.sleep(random * 1000);
		
		System.out.println("选中搜索框");
		JaveShellUtil.ExecCommand("adb shell input tap 500 210");
		Thread.sleep(random * 1000);
		
		System.out.println("输入群号" + groupId);
		JaveShellUtil.ExecCommand("adb shell input text " + groupId);
		Thread.sleep(random * 1000);
		
		System.out.println("选中群");
		JaveShellUtil.ExecCommand("adb shell input tap 800 300");
		Thread.sleep(random * 1000);
		
		System.out.println("群菜单按键");
		JaveShellUtil.ExecCommand("adb shell input tap 1150 100");
		Thread.sleep(random * 1000);
		
		System.out.println("群成员列表");
		JaveShellUtil.ExecCommand("adb shell input tap 1100 355");
		Thread.sleep((random + 10)  * 1000);
	}
	
}
