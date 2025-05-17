package com.example.petitions.controller;

import com.example.petitions.model.Petition;
import com.example.petitions.model.Vote;
import com.example.petitions.repository.PetitionRepository;
import com.example.petitions.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequestMapping("/vote")
public class VoteController {

    @Autowired
    private PetitionRepository petitionRepository;

    @Autowired
    private VoteRepository voteRepository;

    @PostMapping("/{petitionId}")
    public String voteForPetition(@PathVariable Long petitionId, Principal principal) {
        Petition petition = petitionRepository.findById(petitionId).orElseThrow();
        String voterEmail = principal.getName();

        if (petition.getVotes().size() >= petition.getMaxVotes()
                || LocalDate.now().isAfter(petition.getDeadline())) {
            return "redirect:/petitions/" + petitionId + "?error=closed";
        }

        if (voteRepository.findByVoterEmailAndPetitionId(voterEmail, petitionId) != null) {
            return "redirect:/petitions/" + petitionId + "?error=alreadyVoted";
        }

        Vote vote = new Vote();
        vote.setVoterEmail(voterEmail);
        vote.setPetition(petition);
        voteRepository.save(vote);

        return "redirect:/petitions/" + petitionId + "?success=voted";
    }

    @GetMapping("/token/{token}")
    public String voteViaToken(@PathVariable String token, Principal principal) {
        Petition petition = petitionRepository.findAll().stream()
                .filter(p -> p.getVoteToken().equals(token))
                .findFirst()
                .orElse(null);

        if (petition == null) {
            return "redirect:/petitions?error=invalidToken";
        }

        return "redirect:/petitions/" + petition.getId();
    }
}
