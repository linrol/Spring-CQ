package xin.lz1998.cq;

import xin.lz1998.cq.robot.CoolQ;
import xin.lz1998.cq.robot.qlight.Qlight;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Global {
    public static Map<Long, CoolQ> robots = new ConcurrentHashMap<>();
    
    public static Map<Long, Qlight> qlightRobots = new ConcurrentHashMap<>();

}
