package com.technews.repository;

import com.technews.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
//    return type is integer; method has two arguments:
//    method-level annotation of @Param("id") to use id as a parameter, and the other is Integer id.
//    @Param is used to specify the name of the parameter for the query
//    Integer id is actual parameter in method
//    @Query takes a single argument which is the query we want to use
//    the query prints count from Vote model/table aliased as "v" where v.postId = the id in the param
    @Query("SELECT count(*) FROM Vote v where v.postId = :id")
    int countVotesByPostId(@Param("id") Integer id);
}
