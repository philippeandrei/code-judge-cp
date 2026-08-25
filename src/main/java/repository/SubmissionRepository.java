package repository;

import model.Submission;

import java.util.*;

public class SubmissionRepository implements CrudRepository <Submission, UUID>{

    HashMap<UUID, Submission> submissions = new HashMap<>();

    @Override
    public Submission save(Submission submission) {
        submissions.put(submission.getId(), submission);
        return submission;
    }


    @Override
    public Optional<Submission> findById(UUID id) {
        Submission submission = submissions.get(id);
        return Optional.ofNullable(submission);
    }

    @Override
    public List<Submission> findAll() {
        return new ArrayList<>(submissions.values());
    }

    @Override
    public boolean deleteById(UUID id) {
        return (submissions.remove(id) != null);
    }

    public List<Submission> getSubmissionsOfProblem(UUID problemId){
        List<Submission> submissionsOfProblem = new ArrayList<>();
        for(Submission submission : submissions.values()){
            if(submission.getProblemId().equals(problemId)){
                submissionsOfProblem.add(submission);
            }
        }
        return submissionsOfProblem;
    }

    public List<Submission> getSubmissionsOfUser(UUID userID){
        List<Submission> submissionsOfUser = new ArrayList<>();
        for(Submission submission : submissions.values()){
            if(submission.getProblemId().equals(userID)){
                submissionsOfUser.add(submission);
            }
        }
        return submissionsOfUser;
    }

    public List<Submission> getSubmissionsOfUserOfProblem(UUID userID, UUID problemID){
        List<Submission> submissionsQuery = new ArrayList<>();
        for(Submission submission : submissions.values()){
            if(submission.getProblemId().equals(problemID) && submission.getUserId().equals(userID)){
                submissionsQuery.add(submission);
            }
        }
        return submissionsQuery;
    }




}
