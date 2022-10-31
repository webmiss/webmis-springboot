package webmis.task;

import java.util.List;
import com.alibaba.fastjson.JSONObject;
import webmis.util.Util;

/* 日志 */
public class Logs extends Thread {

  @Override
  public void run() {
    Log();
  }

  /* 消费者 */
  public void Log() {
    // 是否记录
    while(true){
      webmis.library.Redis redis = new webmis.library.Redis("");
      List<String> data = redis.BLPop("logs", 10);
      redis.Close();
      if(data==null) continue;
      // 保存
      String msg = data.get(1);
      Boolean res = _logsWrite(msg);
      if(!res){
        webmis.service.Logs.File("upload/erp/Logs.json", Util.JsonEncode(msg));
      }
    }
  }

  /* 写入 */
  private static boolean _logsWrite(String msg) {
    // 数据
    JSONObject data = Util.JsonDecode(msg);
    System.out.println(data);
    return true;
  }
  
}
