package controller;

import exception.ProblemNotFoundException;
import model.Problem;
import service.ProblemService;

import java.util.UUID;

public class ProblemController {
    public ProblemService problemService;

    public ProblemController(ProblemService problemService){
        this.problemService = problemService;
    }
    //String title, String statement, long timeLimitMs, long memoryLimitKb) {

    //create-problem <title> <statement> <timeLimitMs> <memoryLimitKb>
    public void handleCreateProblem(String title, String statement, long timeLimitMs, long memoryLimitKb){
        if(title == null || statement == null || timeLimitMs == 0 || memoryLimitKb == 0){
            System.out.println("[Syntax error] Use valid title, statement or Limits above 0");
            return;
        }

        problemService.createProblem(title, statement, timeLimitMs, memoryLimitKb);
        System.out.println("Problem saved.");

    }
    //add-test-case <problemId> <input> <expectedOutput>
    public void handleAddTestCaseToProblem(UUID problemId, String input, String expectedOutput){
        if(problemId == null || input == null || expectedOutput == null){
            System.out.println("[Syntax error] Please use valid problem ID, input, and expected output.");
            return;
        }
        try {
            problemService.addTestCaseToProblem(problemId, input, expectedOutput);
            System.out.println("Test case added succesfully");
        } catch (ProblemNotFoundException e){
            System.out.println("[Error] " + e.getMessage());
        } catch (Exception e) {
            System.out.print("[Unknown error] " + e.getMessage());
        }
    }
    //search-problem-id <id>
    public void handleGetProblemById(UUID problemId){
        if(problemId == null){
            System.out.println("[Syntax error] Use valid problem ID");
            return;
        }

        try {
            Problem problem = problemService.getProblemById(problemId);
            System.out.println("Found problem: " + problem.getTitle());
        } catch (ProblemNotFoundException e) {
            System.out.println("[Error] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Unknown error] " + e.getMessage());
        }
    }

    //search-problem-title <title>
    public void handleGetProblemByTitle(String title){
        if(title == null){
            System.out.println("[Syntax error]: Use valid title");
            return;
        }

        try {
            Problem problem = problemService.getProblemByTitle(title);
            System.out.println("Found problem: " + problem.getId());
        } catch (ProblemNotFoundException e) {
            System.out.println("[Error] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Unknown error] " + e.getMessage());
        }

    }
    //delete-problem <id>
    public void handleDeleteProblemById(UUID problemId){
        if(problemId == null){
            System.out.println("Use valid problem id");
            return;
        }
        try {
            problemService.deleteProblemById(problemId);
            System.out.println("Deleted problem succesfully");
        } catch (ProblemNotFoundException e) {
            System.out.println("[Error] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Unkown error] " + e.getMessage());
        }
    }

}
