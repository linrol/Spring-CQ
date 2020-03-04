package xin.lz1998.cq.robot.qqadb;

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
		JaveShellUtil.ExecCommand("adb shell input tap 1050 100");
		Thread.sleep((random + 3) * 1000);
		
		//输入qq号码
		System.out.println("输入qq号码" + qq);
		JaveShellUtil.ExecCommand("adb shell input text " + qq);
		Thread.sleep(random * 1000);
		
		System.out.println("进入好友资料页");
		JaveShellUtil.ExecCommand("adb shell input tap 1100 230");
		Thread.sleep(random * 1000);
		
		System.out.println("点击加好友");
		JaveShellUtil.ExecCommand("adb shell input tap 320 1750");
		Thread.sleep(random * 1000);
		
		System.out.println("点击下一步");
		JaveShellUtil.ExecCommand("adb shell input tap 1100 100");
		Thread.sleep(random * 1000);
		
		System.out.println("点击发送");
		JaveShellUtil.ExecCommand("adb shell input tap 1100 100");
		Thread.sleep(random * 1000);
		
		System.out.println("补偿失败");
		JaveShellUtil.ExecCommand("adb shell input tap 600 590");
		Thread.sleep(2 * 1000);
		
		System.out.println("补偿失败");
		JaveShellUtil.ExecCommand("adb shell input tap 35 100");
		Thread.sleep(2 * 1000);
		
		//返回
		System.out.println("返回");
		JaveShellUtil.ExecCommand("adb shell input keyevent 4");
		Thread.sleep(random * 1000);
	}
	
	public static void addFriendByIQQ(String qq) throws InterruptedException {
		int random=(int)(Math.random()*3+3);
		
		System.out.println("选中qq输入框");
		JaveShellUtil.ExecCommand("adb shell input tap 300 300");
		Thread.sleep(random * 1000);
		
		//输入qq号码
		System.out.println("输入qq号码");
		JaveShellUtil.ExecCommand("adb shell input text " + qq);
		Thread.sleep(random * 1000);
		
		System.out.println("点击查找按钮");
		JaveShellUtil.ExecCommand("adb shell input tap 1120 300");
		Thread.sleep(random * 1000);
		
		System.out.println("发送");
		JaveShellUtil.ExecCommand("adb shell input tap 1130 100");
		Thread.sleep(random * 1000);
		
		System.out.println("点击加好友");
		JaveShellUtil.ExecCommand("adb shell input tap 600 1800");
		Thread.sleep(random * 1000);
		
		System.out.println("点击下一步");
		JaveShellUtil.ExecCommand("adb shell input tap 1150 100");
		Thread.sleep(random * 1000);
		
		System.out.println("点击发送");
		JaveShellUtil.ExecCommand("adb shell input tap 1150 100");
		Thread.sleep(random * 1000);
		
		System.out.println("清空输入框");
		JaveShellUtil.ExecCommand("adb shell input tap 1030 320");
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
		JaveShellUtil.ExecCommand("adb shell am start -n com.tencent.qqlite/com.tencent.mobileqq.activity.SplashActivity");
		Thread.sleep(random * 1000);
		
		System.out.println("选中搜索栏");
		JaveShellUtil.ExecCommand("adb shell input tap 1050 100");
		Thread.sleep((random + 3) * 1000);
		
		System.out.println("输入群号码" + groupId);
		JaveShellUtil.ExecCommand("adb shell input text " + groupId);
		Thread.sleep(random * 1000);
		
		System.out.println("进入群界面");
		JaveShellUtil.ExecCommand("adb shell input tap 800 230");
		Thread.sleep(random * 1000);
		
		System.out.println("点击群菜单");
		JaveShellUtil.ExecCommand("adb shell input tap 1140 110");
		Thread.sleep(random * 1000);
		
		System.out.println("进入群成员列表");
		JaveShellUtil.ExecCommand("adb shell input tap 1070 250");
		Thread.sleep((random + 10) * 1000);
		
	}
	
}
