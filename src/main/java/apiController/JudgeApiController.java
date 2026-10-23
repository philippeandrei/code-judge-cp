package apiController;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import service.JudgeService;

@Singleton
public class JudgeApiController {
  public JudgeService judgeService;
  @Inject
  public JudgeApiController(JudgeService judgeService) {
    this.judgeService = judgeService;
  }

  public void submit(Context ctx) {}
}
