package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.DataAccessException;
import model.Contest;
import model.Problem;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ContestRepository implements CrudRepository<Contest, UUID>{
    HashMap<UUID, Contest> contests = new HashMap<>();

    private final String FILE_PATH =  "json_folder/contests.json";
    ObjectMapper mapper = new ObjectMapper();
    public ContestRepository(){
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        loadFromFile();
    }

    public void loadFromFile(){
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return; //am creat fisierul ca nu exista
            }
            List<Contest> contestList = mapper.readValue(file, new TypeReference<List<Contest>>() {});

            for (Contest contest : contestList) {
                //  System.out.println("ID: " + problem.getId() + " | Title: " + problem.getTitle());
                contests.put(contest.getId(), contest);
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to load contests from " + FILE_PATH, e);
        }
    }
    public void saveToFile()  {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), contests.values());
        } catch (IOException e) {
            throw new DataAccessException("Failed to save data to " + FILE_PATH, e);
        }
    }
    @Override
    public Contest save(Contest contest)  {
        contests.put(contest.getId(), contest);
        saveToFile();
        return contest;
    }
    @Override
    public Optional<Contest> findById(UUID id){
        Contest contest = contests.get(id);
        return Optional.ofNullable(contest);
    }
    @Override
    public List<Contest> findAll(){
        return new ArrayList<>(contests.values());
    }
    @Override
    public boolean deleteById(UUID id)  {
        Contest contest = contests.remove(id);
        if(contest != null){
            saveToFile();
            return true;
        }
        return  false;
    }

    public Optional<Contest> getContestByName(String name){
        for(Contest contest : contests.values()){
            if(Objects.equals(contest.getName(), name)){
                return Optional.of(contest);
            }
        }
        return Optional.empty();
    }



}
