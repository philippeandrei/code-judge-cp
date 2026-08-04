package model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Contest {
    private UUID id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Problem> problems;

    public Contest(String name, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void addProblem(Problem problem){
        this.problems.add(problem);
    }



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStateTime() {
        return startTime;
    }

    public void setStateTime(LocalDateTime stateTime) {
        this.startTime = stateTime;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Contest contest = (Contest) o;
        return Objects.equals(id, contest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
