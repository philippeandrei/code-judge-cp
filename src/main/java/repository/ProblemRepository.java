package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.DataAccessException;
import model.Problem;
import model.User;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ProblemRepository implements CrudRepository<Problem, UUID>{
    HashMap<UUID, Problem> problems = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();
    private final String FILE_PATH =  "json_folder/problems.json";
    public ProblemRepository() {
        loadFromFile();
    }
    public void loadFromFile(){
        try {
            File file = new File(FILE_PATH);
            if (!file.exists() || file.length() == 0) {
                return; //am creat fisierul ca nu exista
            }
            List<Problem> problemList = mapper.readValue(file, new TypeReference<List<Problem>>() {});

            for (Problem problem : problemList) {
              //  System.out.println("ID: " + problem.getId() + " | Title: " + problem.getTitle());
                problems.put(problem.getId(), problem);
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to load problems from " + FILE_PATH, e);
        }
    }
    public void saveToFile()  {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), problems.values());
        } catch (IOException e) {
            throw new DataAccessException("Failed to save data to " + FILE_PATH, e);
        }
    }



    @Override
    public Problem save(Problem problem)  {
        problems.put(problem.getId(), problem);
        saveToFile();
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
    public boolean deleteById(UUID id)  {
        Problem problem = problems.remove(id);
        if(problem != null){
            saveToFile();
            return true;
        }
        return false;
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
