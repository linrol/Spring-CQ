package xin.lz1998.cq.util;
import java.io.File;
import java.util.Calendar;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xin.lz1998.cq.plugin.log.LogPlugin;  
  
public class XmlUtil {  
      
	 private static Logger logger = LoggerFactory.getLogger(LogPlugin.class);
	
    public static String[] getAttributeValue(String xmlPath,String attributeValue) {
    	JaveShellUtil.ExecCommand("rm -rf " + xmlPath);
    	long time = Calendar.getInstance().getTimeInMillis();
    	JaveShellUtil.ExecCommand("adb shell uiautomator dump /data/local/tmp/uidump_"+time+".xml");
    	JaveShellUtil.ExecCommand("adb pull /data/local/tmp/uidump"+time +".xml " + xmlPath);
    	File file = new File(xmlPath);
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(file);
            String xpath = "//*[@text='"+attributeValue+"']";
            Element element = (Element) document.selectSingleNode(xpath);
            String boundString = element.attributeValue("bounds");//获取到节点的指定属性
            String[] bounds = boundString.split("]\\[");
            if(bounds.length == 2) {
            	String location = bounds[0].substring(1);
            	logger.info("获取到元素"+attributeValue+"的坐标:" + location);
            	return location.split(",");
            }
        } catch (Exception ex) {  
        	//ex.printStackTrace();
        	logger.error("解释xml文件出现异常:" + ex.getMessage());
        }  
        return null;
    }   
      
      
    public static void main(String[] args) {
    	//System.out.println(XmlUtil.getAttributeValue("/home/linrol/work/sourcecode/Spring-CQ/src/main/resources/dumpxml/uidump.xml","发送") != null);
    }  
}
