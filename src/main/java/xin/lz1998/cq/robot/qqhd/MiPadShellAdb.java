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
}
