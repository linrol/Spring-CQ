package xin.lz1998.cq.util;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class JaveShellUtil {
	
	public static void main(String[] args) {
		System.out.println(ExecCommandResult("adb shell dumpsys activity activities | grep 'AddFriendVerifyActivity'"));
	}
	

	public static String ExecCommandResult(String command) {
        try {
            Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command }, null, null);
            process.waitFor();
            return ExecOutput(process);
        } catch (Exception e) {
            return null;
        }
    }
	
	public static void ExecCommandNumber(String command,int num) {
		for(int i=0;i<num;i++) {
			ExecCommand(command);
		}
	}
	
	public static void ExecCommand(String command) {
        try {
        	Thread.sleep((int)(Math.random()*1500+800));
            Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command }, null, null);
            process.waitFor();
            ExecOutput(process);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    public static String ExecOutput(Process process) throws Exception {
        if (process == null) {
            return null;
        }
        InputStreamReader ir = new InputStreamReader(process.getInputStream());
        LineNumberReader input = new LineNumberReader(ir);
        String line;
        String output = "";
        while ((line = input.readLine()) != null) {
            output += line + "\n";
        }
        input.close();
        ir.close();
        if (output.length() > 0) {
        	return output;
        }
        return null;
    }
}
