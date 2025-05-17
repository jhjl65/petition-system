package com.example.petitions.controller;

import com.example.petitions.model.Petition;
import com.example.petitions.model.User;
import com.example.petitions.repository.PetitionRepository;
import com.example.petitions.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/petitions")
public class PetitionController {

    @Autowired
    private PetitionRepository petitionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getPetitions(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(required = false) String status) {

        List<Petition> petitions;

        if ("active".equalsIgnoreCase(status)) {
            petitions = petitionRepository.findAll().stream()
                    .filter(p -> p.getVotes().size() < p.getMaxVotes()
                            && LocalDate.now().isBefore(p.getDeadline()))
                    .toList();
            model.addAttribute("filter", "active");
        } else {
            petitions = petitionRepository.findAll(PageRequest.of(page, 10)).getContent();
            model.addAttribute("filter", "all");
        }

        model.addAttribute("petitions", petitions);
        return "index";
    }

    @GetMapping("/{id}")
    public String getPetition(@PathVariable Long id,
                              @RequestParam(required = false) String error,
                              @RequestParam(required = false) String success,
                              Model model,
                              Principal principal,
                              HttpServletRequest request) {
        Petition petition = petitionRepository.findById(id).orElseThrow();
        model.addAttribute("petition", petition);
        model.addAttribute("error", error);
        model.addAttribute("success", success);

        boolean isCreator = principal != null && principal.getName().equals(petition.getCreator().getEmail());
        model.addAttribute("isCreator", isCreator);

        // Додаємо baseUrl
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        model.addAttribute("baseUrl", baseUrl);

        return "petition_detail";
    }

    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createPetitionForm(Model model) {
        model.addAttribute("petition", new Petition());
        return "create_petition";
    }

    @PostMapping("/create")
    public String createPetition(@ModelAttribute Petition petition,
                                 @RequestParam("image") MultipartFile image,
                                 Principal principal) throws IOException {
        User user = userRepository.findByEmail(principal.getName());
        petition.setCreator(user);

        if (!image.isEmpty()) {
            petition.setImageData(image.getBytes());
            petition.setImageType(image.getContentType());
        }

        petitionRepository.save(petition);
        return "redirect:/petitions";
    }

    @GetMapping("/edit/{id}")
    public String editPetitionForm(@PathVariable Long id, Model model) {
        model.addAttribute("petition", petitionRepository.findById(id).orElseThrow());
        return "edit_petition";
    }

    @PostMapping("/edit/{id}")
    public String editPetition(@PathVariable Long id,
                               @ModelAttribute Petition petition,
                               @RequestParam("image") MultipartFile image) throws IOException {
        Petition existing = petitionRepository.findById(id).orElseThrow();
        existing.setTitle(petition.getTitle());
        existing.setDescription(petition.getDescription());
        existing.setMaxVotes(petition.getMaxVotes());
        existing.setDeadline(petition.getDeadline());

        if (!image.isEmpty()) {
            existing.setImageData(image.getBytes());
            existing.setImageType(image.getContentType());
        }

        petitionRepository.save(existing);
        return "redirect:/petitions/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deletePetition(@PathVariable Long id) {
        petitionRepository.deleteById(id);
        return "redirect:/petitions";
    }

    @GetMapping("/{id}/image")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Petition petition = petitionRepository.findById(id).orElseThrow();
        if (petition.getImageData() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header("Content-Type", petition.getImageType())
                .body(petition.getImageData());
    }
}
