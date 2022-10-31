package webmis.service;

import webmis.config.Env;
import webmis.library.FileEo;
import webmis.library.Redis;
import webmis.util.Util;

/* 日志 */
public class Logs extends Base {

  /* 文件 */
  public static void File(String file, String content) {
    FileEo.Root = Env.root_dir;
    FileEo.WriterEnd(file, content+"\n");
  }

  /* 生产者 */
  public static void Log(Object data) {
    Redis redis = new Redis("");
    redis.RPush("logs", Util.JsonEncode(data));
    redis.Close();
  }
  
}
