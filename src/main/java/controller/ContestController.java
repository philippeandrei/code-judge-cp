package controller;

import exception.*;
import model.Contest;
import service.ContestService;
import service.ProblemService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class ContestController {
    public ContestService contestService;
    public ProblemService problemService;

    public ContestController(ContestService contestService, ProblemService problemService) {
        this.contestService = contestService;
        this.problemService = problemService;
    }

    //create-contest <name> <startTime> <endTime>

    public void handleCreateContest(String name, LocalDateTime startTime, LocalDateTime endTime) {
        if (name == null ) {
            System.out.println("[Syntax error] Use valid name, startTime end endTime");
            return;
        }
        try {
            contestService.createContest(name, startTime, endTime);
            System.out.println("Contest created");
        } catch (ValidationException e) {
            System.out.println("[Validation error] " + e.getMessage());
        } catch (CodeJudgeException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    //get-contest-id <id>
    public void handleGetContestById(UUID id) {
        if (id == null) {
            System.out.println("[Syntax error] Use valid problem ID");
            return;
        }
        try{
            Contest contest = contestService.getContestById(id);
            System.out.println("Found contest: " + contest.getName() + " - " + " Start Time: "  + contest.getStartTime() + " and End Time: " + contest.getEndTime());
        } catch (ContestNotFoundException e) {
            System.out.println("[Contest not found exception] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Unknown exception] " + e.getMessage());
        }
    }
    //get-contest-name <name>
    public void handleGetContestByName(String name){
        if(name == null){
            System.out.println("[Syntax error] User valid Contest Name");
            return;
        }
        try {
            Contest contest = contestService.getContestByName(name);
            System.out.println("Found contest: " + contest.getName() + " id: " + contest.getId() + " Start Time: "  + contest.getStartTime() + " and End Time: " + contest.getEndTime());
        }catch (ContestNotFoundException e){
            System.out.println("[Contest not found exception] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Unknown exception] " + e.getMessage());
        }
    }

    //delete-contest-id <id>
    public void handleDeleteContestId(UUID id){
        if(id == null){
            System.out.println("[Syntax error] User valid Contest ID");
            return;
        }
        try {
          contestService.deleteContestById(id);
          System.out.println("Deleted contest succesfully");
        } catch (ContestNotFoundException e) {
            System.out.println("[Contest not found exception] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Unknown exception] " + e.getMessage());
        }
    }

    //contest-add <contestID> <problemID>

    public void handleAddProblemToContest(UUID contestID, UUID problemID){
        if(problemID == null || contestID == null){
            System.out.println("[Syntax error] User valid Contest ID or Problem ID");
            return;
        }
        try{

            contestService.addProblemToContest(problemID, contestID);
            System.out.println("Added problem to contest successfully");


        }  catch (EntityNotFoundException e) {
            System.out.println("[Not Found Error] " + e.getMessage());
        } catch (ValidationException e) {
            System.out.println("[Validation Error] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Unexpected Error] " + e.getMessage());
        }
    }


}
