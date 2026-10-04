package apiController;

import io.javalin.http.Context;
import service.JudgeService;

public class JudgeApiController {
    public JudgeService judgeService ;
    public JudgeApiController(JudgeService judgeService){
        this.judgeService = judgeService;
    }

    public void submit(Context ctx){

    }
}
