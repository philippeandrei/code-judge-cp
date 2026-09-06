package service;

import exception.ProblemNotFoundException;
import model.Problem;
import model.TestCase;
import repository.ProblemRepository;

import java.io.IOException;
import java.util.UUID;

public class ProblemService {
    public ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    //String title, String statement, long timeLimitMs, long memoryLimitKb) {
    public Problem createProblem(String title, String statement, long timeLimitMs, long memoryLimitKb)  {
        Problem problem = new Problem(title, statement, timeLimitMs, memoryLimitKb);
        problemRepository.save(problem);
        return problem;
    }

    public Problem getProblemById(UUID id)  {
        return problemRepository.findById(id).orElseThrow(() -> new ProblemNotFoundException("Can not find any problem with the id: " + id));
    }

    public Problem getProblemByTitle(String title)  {
        return problemRepository.getProblemByTitle(title).orElseThrow(()-> new ProblemNotFoundException("Can not find any problem with the title: " + title));
    }

    public void deleteProblemById(UUID id)  {
        if(!problemRepository.deleteById(id))
           throw new ProblemNotFoundException("Can not find any problem with id: " + id);
    }

    public void addTestCaseToProblem(UUID problemId, String input, String expectedOutput)  {
        Problem problem = getProblemById(problemId);
        TestCase testCase = new TestCase(problemId, input, expectedOutput);
        //nu creez test case separat in Main, creez direct aici in ProblemService
        //
        problem.addTestCase(testCase);

        problemRepository.save(problem);
    }

}
