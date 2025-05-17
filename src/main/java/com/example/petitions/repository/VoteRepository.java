package com.example.petitions.repository;

import com.example.petitions.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Vote findByVoterEmailAndPetitionId(String voterEmail, Long petitionId);
    Vote findByVoterEmailAndPetition_VoteToken(String voterEmail, String voteToken);
}
