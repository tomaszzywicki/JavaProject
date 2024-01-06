package com.pacholki.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pacholki.controller.MainController;
import com.pacholki.changer.CompetitionDataGetter;

public class Competition extends Entity {
    
    private static final String DATA_DIR = "src/main/resources/com/pacholki/data/leagues/";

    private League league;
    private Season season;
    private List<Team> teams;
    private List<Game> schedule;

    private String compDir;
    private String teamsFilePath;
    private String scheduleFilePath;

//    private MainController controller;

//    private CompetitionDataGetter getter;

    public Competition(League league, Season season, MainController controller) {
        this.league = league;
        this.season = season;
        this.controller = controller;
        if (isValid())  {
            prepPaths();
            getData();
        }
    }

    private void prepPaths() {
        compDir = DATA_DIR + league.getName() + "/" + season.getFBrefID() + "/";
        teamsFilePath = compDir + "teams.json";
        scheduleFilePath = compDir + "schedule.json";
    }

    public League getLeague() {
        return league;
    }

    public Season getSeason() {
        return season;
    }

    public String getCompDir() {
        return compDir;
    } 

    public List<Team> getTeams() {
        return teams;
    }

    private void getData() {
        getter = new CompetitionDataGetter(this);
        getter.start();
    }

    @Override
    public void prepareData() {
        readTeams();
        readSchedule();
        prepareSchedule();
        prepareTable();
    }

    private void readTeams() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            teams = mapper.readValue(new File(teamsFilePath), new TypeReference<List<Team>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readSchedule() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            schedule = mapper.readValue(new File(scheduleFilePath), new TypeReference<List<Game>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareSchedule() {
        List <Game> gamesToBeRemoved = new ArrayList<>();
        for (Game game : schedule) {
            if(game.getGameweek() == null) {
                gamesToBeRemoved.add(game);
                continue;
            }
            game.setCompetition(this);
            game.prepareData();
        }

        for (Game game : gamesToBeRemoved) {
            schedule.remove(game);
        }
    }

    public void prepareTable() {
        for (Game game : schedule) {
            Integer gameweek = game.getGameweek();
            game.getHomeTeam().setGamesPlayed(gameweek);
            game.getAwayTeam().setGamesPlayed(gameweek);

            if(game.getHomeScore() > game.getAwayScore()) {
                game.getHomeTeam().setGamesWon(gameweek, 1);
                game.getAwayTeam().setGamesWon(gameweek, 0);
                game.getHomeTeam().setGamesDrawn(gameweek, 0);
                game.getAwayTeam().setGamesDrawn(gameweek, 0);
                game.getHomeTeam().setGamesLost(gameweek, 0);
                game.getAwayTeam().setGamesLost(gameweek, 1);
            } else if (game.getHomeScore() < game.getAwayScore()) {
                game.getHomeTeam().setGamesWon(gameweek, 0);
                game.getAwayTeam().setGamesWon(gameweek, 1);
                game.getHomeTeam().setGamesDrawn(gameweek, 0);
                game.getAwayTeam().setGamesDrawn(gameweek, 0);
                game.getHomeTeam().setGamesLost(gameweek, 1);
                game.getAwayTeam().setGamesLost(gameweek, 0);
            } else {
                game.getHomeTeam().setGamesWon(gameweek, 0);
                game.getAwayTeam().setGamesWon(gameweek, 0);
                game.getHomeTeam().setGamesDrawn(gameweek, 1);
                game.getAwayTeam().setGamesDrawn(gameweek, 1);
                game.getHomeTeam().setGamesLost(gameweek, 0);
                game.getAwayTeam().setGamesLost(gameweek, 0);
            }
            
            game.getHomeTeam().setGoalsFor(gameweek, game.getHomeScore());
            game.getAwayTeam().setGoalsFor(gameweek, game.getAwayScore());

            game.getHomeTeam().setGoalsAgainst(gameweek, game.getHomeScore());
            game.getHomeTeam().setGoalsAgainst(gameweek, game.getAwayScore());
            
        }
    }

    public boolean isValid() {
        return (league != null & season != null);
    }

    public Team getTeamByName(String name) {

        for (Team team : teams) {
            if (team.getName().equals(name))    return team;
        }

        return null;
    }

//    @Override
//    public void setMe() {
//        DataWaiter waiter = new DataWaiter(controller, getter);
//        waiter.start();
//
//    }

    public String toString() {
        if (! isValid())    return "Null values!!!";
        return league.toString() + "\t" + season.toString();
    }
}
