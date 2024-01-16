package com.jpcc.CFBProject.controller;

import com.jpcc.CFBProject.repository.GameRepository;
import com.jpcc.CFBProject.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private TeamService teamService;
    private GameService gameService;
    private TeamRecordService teamRecordService;
    private PlayService playService;
    private PlayerService playerService;
    private SeasonCalendarService seasonCalendarService;
    private GameRepository gameRepository;


    @Autowired
    public AdminController(TeamService teamService, GameService gameService, TeamRecordService teamRecordService, PlayService playService, PlayerService playerService, SeasonCalendarService seasonCalendarService, GameRepository gameRepository) {
        this.teamService = teamService;
        this.gameService = gameService;
        this.teamRecordService = teamRecordService;
        this.playService = playService;
        this.playerService = playerService;
        this.seasonCalendarService = seasonCalendarService;
        this.gameRepository = gameRepository;
    }

    @GetMapping
    public String adminHome(Model model) {
        // Add any necessary data to the model
        return "admin/home"; // HTML page in 'src/main/resources/templates/admin/home.html'
    }
    @PostMapping("/fetchSeasonCalendar")
    public String fetchAndSaveSeasonCalendar(RedirectAttributes redirectAttributes,
                                             @RequestParam(required = true) Integer season) throws Exception {
        seasonCalendarService.fetchAndSaveWeeks(season);
        redirectAttributes.addFlashAttribute("message",
                "Season" + season + "fetched and saved successfully.");
        return "redirect:/admin";
    }

    @PostMapping("/fetchTeams")
    public String fetchAndSaveTeams(RedirectAttributes redirectAttributes) throws Exception {
        teamService.fetchAndSaveTeams();
        redirectAttributes.addFlashAttribute("message", "Teams fetched and saved successfully.");
        return "redirect:/admin";
    }

    @PostMapping("/fetchGames")
    public String fetchAndSaveGames(RedirectAttributes redirectAttributes,
                                    @RequestParam(required = true) Integer season) throws Exception {
        gameService.fetchAndSaveGames(season, "postseason");
        gameService.fetchAndSaveGames(season, "regular");
        redirectAttributes.addFlashAttribute("message", "Games fetched and saved successfully.");
        return "redirect:/admin";
    }

    @PostMapping("/fetchTeamRecords")
    public String fetchAndSaveTeamRecords(RedirectAttributes redirectAttributes,
                                          @RequestParam(required = true) Integer year) throws Exception {
        teamRecordService.fetchAndSaveTeamRecords(year);
        redirectAttributes.addFlashAttribute("message", "Team records for year " + year + " fetched and saved successfully.");
        return "redirect:/admin";
    }

    @PostMapping("/fetchPlays")
    public String fetchAndSavePlays(RedirectAttributes redirectAttributes,
                                    @RequestParam(required = true) Integer year,
                                    @RequestParam(required = true) Integer week) throws Exception {
        playService.fetchAndSavePlaysBySeason(year, week, "regular");
        redirectAttributes.addFlashAttribute("message",
                "Plays for " + year + "and week" + week + " fetched and saved successfully.");
        List<Long> gameIdsList = gameRepository.findAllGameIds();
        for (Long gameId : gameIdsList){
            gameService.calculateQuarterScores(gameId);
        }
        return "redirect:/admin";
    }
    @PostMapping("/fetchPlayers") //Goes to Roster endpoint in CFB API
    public String fetchAndSavePlayers(RedirectAttributes redirectAttributes,
                                    @RequestParam(required = true) Integer year,
                                    @RequestParam(required = false) String team) throws Exception {
        playerService.fetchAndSavePlayers(team, year);
        redirectAttributes.addFlashAttribute("message",
                "Players for " + team + " " + year + " fetched and saved successfully.");
        return "redirect:/admin";
    }
}
