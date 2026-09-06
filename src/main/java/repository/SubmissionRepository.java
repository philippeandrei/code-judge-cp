package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.DataAccessException;
import model.Submission;
import model.User;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SubmissionRepository implements CrudRepository <Submission, UUID>{

    HashMap<UUID, Submission> submissions = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();
    private final String FILE_PATH =  "json_folder/submissions.json";
    public SubmissionRepository(){
        loadFromFile();
    }
    public void loadFromFile(){
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return; //am creat fisierul ca nu exista
            }
            List<Submission> submissionsList = mapper.readValue(file, new TypeReference<List<Submission>>() {});

            for (Submission submission : submissionsList) {
//                System.out.println("ID: " + user.getId() + " | Username: " + user.getUsername());
                submissions.put(submission.getId(), submission);
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to load submission from " + FILE_PATH, e);
        }
    }

    public void saveToFile()  {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), submissions.values());
        } catch (IOException e) {
            throw new DataAccessException("Failed to save data to " + FILE_PATH, e);
        }
    }
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
            if(submission.getUserId().equals(userID)){
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
