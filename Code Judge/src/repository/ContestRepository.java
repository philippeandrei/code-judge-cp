package repository;

import model.Contest;

import java.util.*;

public class ContestRepository implements CrudRepository<Contest, UUID>{
    HashMap<UUID, Contest> contests;
    @Override
    public Contest save(Contest contest){
        contests.put(contest.getId(), contest);
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
    public boolean deleteById(UUID id){
        return (contests.remove(id) != null);
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
