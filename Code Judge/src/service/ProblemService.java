package service;

import exception.ProblemNotFoundException;
import model.Problem;
import model.TestCase;
import repository.ProblemRepository;

import java.util.UUID;

public class ProblemService {
    public ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    //String title, String statement, long timeLimitMs, long memoryLimitKb) {
    public Problem createProblem(String title, String statement, long timeLimitMs, long memoryLimitKb) {
        Problem problem = new Problem(title, statement, timeLimitMs, memoryLimitKb);
        return problem;
    }

    public Problem getProblemById(UUID id) throws ProblemNotFoundException {
        return problemRepository.findById(id).orElseThrow(() -> new ProblemNotFoundException("Can not find any problem with the id: " + id));
    }

    public Problem getProblemByTitle(String title) throws ProblemNotFoundException {
        return problemRepository.getProblemByTitle(title).orElseThrow(()-> new ProblemNotFoundException("Can not find any problem with the title: " + title));
    }

    public void deleteProblemById(UUID id) throws ProblemNotFoundException {
        if(!problemRepository.deleteById(id))
           throw new ProblemNotFoundException("Can not find aby problem with id: " + id);
    }

    public void addTestCaseToProblem(UUID problemId, String input, String expectedOutput) throws ProblemNotFoundException{
        Problem problem = getProblemById(problemId);
        TestCase testCase = new TestCase(problemId, input, expectedOutput);
        //nu creez test case separat in Main, creez direct aici in ProblemService
        //
        problem.addTestCase(testCase);

        problemRepository.save(problem);
    }

}
