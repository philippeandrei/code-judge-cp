package service;

import exception.ContestNotFoundException;
import exception.ProblemNotFoundException;
import exception.ValidationException;
import model.Contest;
import model.Problem;
import repository.ContestRepository;
import repository.ProblemRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class ContestService {
    public ContestRepository contestRepository;
    public ProblemRepository problemRepository;

    public ContestService(ContestRepository contestRepository, ProblemRepository problemRepository){
        this.contestRepository = contestRepository;
        this.problemRepository = problemRepository;
    }
    public void createContest(String name, LocalDateTime startTime, LocalDateTime endTime)  {
        if(startTime.isBefore(LocalDateTime.now()) || endTime.isBefore(startTime) || endTime.isEqual(startTime))
            throw new ValidationException("Date Time is not valid") ;
        Contest contest = new Contest(name, startTime, endTime);
        contestRepository.save(contest);
    }
    public Contest getContestById(UUID id)  {
        return contestRepository.findById(id).orElseThrow(() -> new ContestNotFoundException("Can not find contest with id: " + id));
    }

    public Contest getContestByName(String name) {
        return contestRepository.getContestByName(name).orElseThrow(() -> new ContestNotFoundException("Can not find contest with name: " + name));
    }
    public void deleteContestById(UUID id)  {
        if(!contestRepository.deleteById(id))
            throw new ContestNotFoundException("Can not found contest with id: " + id);
    }

    public void addProblemToContest(UUID problemId, UUID contestID) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(() -> new ProblemNotFoundException("Can not find problem: " + problemId));

        Contest contest = contestRepository.findById(contestID).orElseThrow(() -> new ContestNotFoundException("can not find contest: " + contestID));

        contest.addProblem(problem);
        contestRepository.save(contest);
    }
}
