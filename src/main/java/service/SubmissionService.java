package service;

import exception.SubmissionNotFoundException;
import model.Submission;
import repository.SubmissionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SubmissionService {
    public SubmissionRepository submissionRepository;

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    //    public Submission(UUID userId, UUID problemId, String sourceCode, LocalDateTime submittedTime){
    public Submission createSubmission(UUID userId, UUID problemId, String sourceCode, LocalDateTime submittedTime) {
        Submission submission = new Submission(userId, problemId, sourceCode, submittedTime);
        submissionRepository.save(submission);
        return submission;
    }

    public Submission getSubmissionById(UUID id) {
        return submissionRepository.findById(id).orElseThrow(() -> new SubmissionNotFoundException("Couldn't find submission: " + id));
    }

    public List<Submission> getAllSubmissions(){
        return submissionRepository.findAll();
    }

    public List<Submission> getAllSubmissionsOfUser(UUID id){
        return submissionRepository.getSubmissionsOfUser(id);
    }

    public List<Submission> getAllSubmissionsOfProblem(UUID id){
        return submissionRepository.getSubmissionsOfProblem(id);
    }

    public List<Submission> getAllSubmissionOfProblemFromUser(UUID problemId, UUID userId){
        return submissionRepository.getSubmissionsOfUserOfProblem(userId, problemId);
    }
    public boolean deleteSubmission(UUID id){
        return submissionRepository.deleteById(id);
    }


}
