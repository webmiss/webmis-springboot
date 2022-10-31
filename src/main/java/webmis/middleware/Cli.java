package webmis.middleware;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import webmis.task.Logs;

/* 命令行 */
@Component
public class Cli {

  @Bean
  public void Init() {
    // 监听日志
    Logs logs = new Logs();
    logs.start();
  }
  
}
