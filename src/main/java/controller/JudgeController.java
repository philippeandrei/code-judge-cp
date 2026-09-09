package controller;

import repository.SubmissionRepository;
import service.JudgeService;
import service.SubmissionService;

import java.io.IOException;
import java.util.UUID;

public class JudgeController {
    public JudgeService judgeService;
    public JudgeController(JudgeService judgeService){
        this.judgeService = judgeService;
    }
    public void handleTestJudge(String path){
        if(path == null || path.trim().isEmpty()){
            System.out.println("[Syntax error] ");
            return;
        }
        try {
            System.out.println("File submitted");
            judgeService.runHardCodedTest(path);
        } catch (Exception e) {
            System.out.println("[Exception]" + e.getMessage());
        }
    }
    public void handleSubmit(UUID userId, UUID problemId, String pathToFile) throws IOException, InterruptedException {
        judgeService.submitProblem(userId, problemId, pathToFile);
    }


}
