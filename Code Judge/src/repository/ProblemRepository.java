package repository;

import model.Problem;
import model.TestCase;
import model.User;

import java.util.*;

public class ProblemRepository implements CrudRepository<Problem, UUID>{
    HashMap<UUID, Problem> problems;
    @Override
    public Problem save(Problem problem){
        problems.put(problem.getId(), problem);
        return problem;
    }

    @Override
    public Optional<Problem> findById(UUID id){
        Problem problem = problems.get(id);
        return Optional.ofNullable(problem);
    }

    @Override
    public List<Problem> findAll(){
        return new ArrayList<>(problems.values());
    }

    @Override
    public boolean deleteById(UUID id){
        return (problems.remove(id) != null);
    }


    public Optional<Problem> getProblemByTitle(String title){
        for(Problem problem: problems.values()){
            if(Objects.equals(problem.getTitle(), title)){
                return Optional.of(problem);
            }
        }
        return Optional.empty();
    }

}
