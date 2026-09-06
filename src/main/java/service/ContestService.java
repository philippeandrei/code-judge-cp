package service;

import exception.ContestNotFoundException;
import exception.ProblemNotFoundException;
import model.Contest;
import model.Problem;
import repository.ContestRepository;
import repository.ProblemRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class ContestService {
    public ContestRepository contestRepository;
    public ProblemRepository problemRepository;

    public ContestService(ContestRepository contestRepository, ProblemRepository problemRepository){
        this.contestRepository = contestRepository;
        this.problemRepository = problemRepository;
    }
    public Contest createContest(String name, LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        Contest contest = new Contest(name, startTime, endTime);
        contestRepository.save(contest);
        return contest;
    }
    public Contest getContestById(UUID id) throws ContestNotFoundException {
        return contestRepository.findById(id).orElseThrow(() -> new ContestNotFoundException("Can not find contest with id: " + id));
    }

    public Contest getContestByName(String name) throws ContestNotFoundException{
        return contestRepository.getContestByName(name).orElseThrow(() -> new ContestNotFoundException("Can not find contest with name: " + name));
    }
    public void deleteProblemById(UUID id) throws ContestNotFoundException, IOException {
        if(!contestRepository.deleteById(id))
            throw new ContestNotFoundException("Can not found contest with id: " + id);
    }

    public void addProblemToContest(UUID problemId, UUID contestID) throws ContestNotFoundException, ProblemNotFoundException, IOException {
        Problem problem = problemRepository.findById(problemId).orElseThrow(() -> new ProblemNotFoundException("Can not find problem: " + problemId));

        Contest contest = contestRepository.findById(contestID).orElseThrow(() -> new ContestNotFoundException("can not find contest: " + contestID));

        contest.addProblem(problem);
        contestRepository.save(contest);
    }
}
