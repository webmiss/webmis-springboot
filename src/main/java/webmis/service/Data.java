package webmis.service;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;

import webmis.config.Env;
import webmis.library.Redis;
import webmis.util.Util;

import webmis.model.Model;

/* 数据类 */
public class Data extends Base {

  // 机器标识
  final static long machineId = Env.machine_id;
  final static long max8bit = 8;    //随机数位数
  final static long max10bit = 10;  //机器位数
  final static long max12bit = 12;  //序列数位数

  // 分区时间
  static LinkedHashMap<String, Integer> partition;

  /* 构造函数 */
  public Data(){
    partition = new LinkedHashMap<String, Integer>();
    partition.put("p2208", 1661961600);
    partition.put("p2209", 1664553600);
    partition.put("plast", 1664553600);
  }

  /* 薄雾算法 */
  public static long Mist(String redisName) {
    // 获取
    Redis redis = new Redis("");
    Long autoId = redis.Incr(redisName);
    redis.Close();
    // 随机数
    Random random = new Random();
    long randA = random.nextInt(255);
    long randB = random.nextInt(255);
    // 位运算
    long mist = (autoId << (Data.max8bit + Data.max8bit) | (randA << Data.max8bit) | randB);
    return mist;
  }

  /* 雪花算法 */
  public static long Snowflake() {
    // 时间戳
    long t = new Date().getTime();
    // 随机数
    Random random = new Random();
    long rand = random.nextInt(4095);
    // 位运算
    long mist = (t << (Data.max10bit + Data.max12bit) | (Data.machineId << Data.max12bit) | rand);
    return mist;
  }
  
  /* 图片地址 */
  public static String Img(Object img) {
    String str = String.valueOf(img);
    return !str.equals("")?Env.base_url+str:"";
  }

  /*
  * 分区-获取ID
  * p2209 = Data.PartitionID("2022-10-01 00:00:00", "logs")
  */
  public static HashMap<String, Object> PartitionID(String date, String table) {
    return PartitionID(date, table, "ctime");
  }
  public static HashMap<String, Object> PartitionID(String date, String table, String column) {
    long t = Util.Time();
    Model m = new Model();
    m.Table(table);
    m.Columns("id", column);
    m.Where(column+" < ?", t);
    m.Order(column+" DESC, id DESC");
    HashMap<String, Object> one = m.FindFirst();
    one.put("date", date);
    one.put("time", t);
    return one;
  }

  /*
  * 分区-获取名称
  * (new Data()).PartitionName(1661961600, 1664553600)
  */
  public String PartitionName(int stime, int etime){
    String p1 = __getPartitionTime(stime);
    String p2 = __getPartitionTime(etime);
    JSONArray arr = new JSONArray();
    boolean start = false;
    for(Entry<String, Integer> entry : partition.entrySet()){
      if(entry.getKey()==p1) start=true;
      if(start) arr.add(entry.getKey());
      if(entry.getKey()==p2) break;
    }
    return Util.Implode(",", arr);
  }
  private String __getPartitionTime(int time){
    String name = "";
    for(Entry<String, Integer> entry : partition.entrySet()){
      if(time<entry.getValue()) return entry.getKey();
      name = entry.getKey();
    }
    return name;
  }

}
