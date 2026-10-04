package dto;

import model.Contest;
import model.Problem;

import java.time.LocalDateTime;
import java.util.List;

public class ContestResponseDto {
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Problem> problems;
    public ContestResponseDto() {}
    public ContestResponseDto(Contest contest) {
        this.name = contest.getName();
        this.startTime = contest.getStartTime();
        this.endTime = contest.getEndTime();
        this.problems = contest.getProblems();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public void setProblems(List<Problem> problems) {
        this.problems = problems;
    }
}
