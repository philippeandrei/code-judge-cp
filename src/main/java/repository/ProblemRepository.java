package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

            List<Problem> problemList = mapper.readValue(file, new TypeReference<List<Problem>>() {});

            for (Problem problem : problemList) {
              //  System.out.println("ID: " + problem.getId() + " | Title: " + problem.getTitle());
                problems.put(problem.getId(), problem);
            }
        } catch (IOException e) {
            System.out.println("Error loading from " + FILE_PATH);
        }
    }
    public void saveToFile() throws IOException {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), problems.values());
        } catch (IOException e) {
            System.out.println("Error saving to " + FILE_PATH);
        }
    }



    @Override
    public Problem save(Problem problem) throws IOException {
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
    public boolean deleteById(UUID id) throws IOException {
        Problem problem = problems.remove(id);
        if(problem != null){
            saveToFile();
            return true;
        }
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
