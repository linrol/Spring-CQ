package xin.lz1998.cq.robot.qqadb;

import org.apache.commons.lang3.StringUtils;

import xin.lz1998.cq.util.JaveShellUtil;
import xin.lz1998.cq.util.XmlUtil;

public class HM2ShellAdb {
	
	static String tmpDir = "/home/linrol/work/sourcecode/Spring-CQ/src/main/resources/dumpxml/";
	
	public static void reloadResource(String groupId) throws InterruptedException {
		int random=(int)(Math.random()*3+2);
		
		System.out.println("Home按键");
		JaveShellUtil.ExecCommand("adb shell input keyevent 3");
		Thread.sleep(random * 1000);
		
		System.out.println("打开飞行模式");
		JaveShellUtil.ExecCommand("adb shell settings put global airplane_mode_on 1");
		Thread.sleep(random * 1000);
		
		System.out.println("广播飞行模式");
		JaveShellUtil.ExecCommand("adb shell am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true");
		Thread.sleep(random * 1000);
		
		System.out.println("关闭飞行模式");
		JaveShellUtil.ExecCommand("adb shell settings put global airplane_mode_on 0");
		Thread.sleep(random * 1000);
		
		System.out.println("广播飞行模式");
		JaveShellUtil.ExecCommand("adb shell am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false");
		Thread.sleep(random * 1000);
		
		System.out.println("Home按键");
		JaveShellUtil.ExecCommand("adb shell input keyevent 3");
		Thread.sleep(random * 1000);
		
		System.out.println("任务按键");
		JaveShellUtil.ExecCommand("adb shell input keyevent 187");
		Thread.sleep(random * 1000);
		
		System.out.println("清理任务");
		JaveShellUtil.ExecCommand("adb shell input tap 680 90");
		Thread.sleep(random * 1000);
		
		System.out.println("打开QQ应用");
		JaveShellUtil.ExecCommand("adb shell am start -n com.tencent.mobileqq/.activity.SplashActivity");
		Thread.sleep((random + 3) * 1000);
		
		System.out.println("选中联系人tab");
		JaveShellUtil.ExecCommand("adb shell input tap 260 1200");
		Thread.sleep((random) * 1000);
		
		System.out.println("点击添加按钮");
		JaveShellUtil.ExecCommand("adb shell input tap 660 100");
		Thread.sleep((random) * 1000);
	}

	public static void addNormalFriend(String qq) throws InterruptedException {
		int random=(int)(Math.random()*3+2);
		System.out.println("点击添加好友");
		JaveShellUtil.ExecCommand("adb shell input tap 330 330");
		Thread.sleep(random * 1000);
		
		System.out.println("输入qq号码");
		JaveShellUtil.ExecCommand("adb shell input text " + qq);
		Thread.sleep(random * 1000);
		
		System.out.println("点击查找按钮");
		JaveShellUtil.ExecCommand("adb shell input tap 650 300");
		Thread.sleep((random + 2) * 1000);
		
		System.out.println("点击加好友");
		String[] addLocation = XmlUtil.getAttributeValue(tmpDir + "uidump.xml","加为好友");
		JaveShellUtil.ExecCommand("adb shell input tap " + addLocation[0] + " " + addLocation[1]);
		Thread.sleep(random * 1000);
		
		System.out.println("下一步");
		JaveShellUtil.ExecCommand("adb shell input tap 650 100");
		Thread.sleep(random * 1000);
		
		System.out.println("发送");
		JaveShellUtil.ExecCommand("adb shell input tap 650 100");
		Thread.sleep(random * 1000);
		
		if(StringUtils.isNotBlank(JaveShellUtil.ExecCommandResult("adb shell dumpsys activity activities | grep 'AddFriendVerifyActivity'")) || XmlUtil.getAttributeValue(tmpDir + "uidump.xml","发送") != null) {
			System.out.println("添加失败返回");
			JaveShellUtil.ExecCommand("adb shell input tap 100 100");
			Thread.sleep(random * 1000);
			
			System.out.println("添加失败返回");
			JaveShellUtil.ExecCommand("adb shell input tap 100 100");
			Thread.sleep(random * 1000);
			
			System.out.println("添加失败返回");
			JaveShellUtil.ExecCommand("adb shell input tap 100 100");
			Thread.sleep(random * 1000);
		}
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

	
	
}
