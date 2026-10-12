package service;

import exception.ContestNotFoundException;
import exception.ProblemNotFoundException;
import exception.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.Contest;
import model.Problem;
import repositoryDataBase.ContestDataBaseRepository;
import repositoryDataBase.ProblemDataBaseRepository;

public class ContestService {
  public ContestDataBaseRepository contestDataBaseRepository;
  public ProblemDataBaseRepository problemDataBaseRepository;

  public ContestService(
      ContestDataBaseRepository contestDataBaseRepository,
      ProblemDataBaseRepository problemDataBaseRepository) {
    this.contestDataBaseRepository = contestDataBaseRepository;
    this.problemDataBaseRepository = problemDataBaseRepository;
  }

  public void createContest(String name, LocalDateTime startTime, LocalDateTime endTime) {
    if (startTime.isBefore(LocalDateTime.now())
        || endTime.isBefore(startTime)
        || endTime.isEqual(startTime)) throw new ValidationException("Date Time is not valid");
    Contest contest = new Contest(name, startTime, endTime);
    contestDataBaseRepository.save(contest);
  }

  public Contest createContest(
      String name, LocalDateTime startTime, LocalDateTime endTime, ArrayList<Problem> problems) {
    if (startTime.isBefore(LocalDateTime.now())
        || endTime.isBefore(startTime)
        || endTime.isEqual(startTime)) throw new ValidationException("Date Time is not valid");
    Contest contest = new Contest(name, startTime, endTime, problems);
    contestDataBaseRepository.save(contest);
    return contest;
  }

  public Contest getContestById(UUID id) {
    return contestDataBaseRepository
        .findById(id)
        .orElseThrow(() -> new ContestNotFoundException("Can not find contest with id: " + id));
  }

  public Contest getContestByName(String name) {
    return contestDataBaseRepository
        .getContestByName(name)
        .orElseThrow(() -> new ContestNotFoundException("Can not find contest with name: " + name));
  }

  public List<Contest> getAll() {
    return contestDataBaseRepository.findAll();
  }

  public void deleteContestById(UUID id) {
    if (!contestDataBaseRepository.deleteById(id))
      throw new ContestNotFoundException("Can not found contest with id: " + id);
  }

  public Contest addProblemToContest(UUID problemId, UUID contestID) {
    Problem problem =
        problemDataBaseRepository
            .findById(problemId)
            .orElseThrow(() -> new ProblemNotFoundException("Can not find problem: " + problemId));

    Contest contest =
        contestDataBaseRepository
            .findById(contestID)
            .orElseThrow(() -> new ContestNotFoundException("can not find contest: " + contestID));

    contest.addProblem(problem);
    contestDataBaseRepository.save(contest);
    return contest;
  }
}
