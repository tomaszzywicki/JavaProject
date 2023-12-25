package com.pacholki.pane;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pacholki.pane.league.League;
import com.pacholki.pane.league.Season;

public class MainPane {

    private final String DATAPATH = "src/main/resources/com/pacholki/data/";
    private final String LEAGUENAMESFILEPATH = DATAPATH + "leagues.json";
    private final String SEASONNAMESFILEPATH = DATAPATH + "seasons.json";

    private List<League> leagues;
    private List<Season> seasons;
    
    private League currentLeague;
    private Season currentSeason;

    public MainPane() {
    }

    public MainPane(League currentLeagueName, Season currentSeason) {
        this.currentLeague = currentLeagueName;
        this.currentSeason = currentSeason;
    }

    public void readSeasons() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Season> seasons = mapper.readValue(new File(SEASONNAMESFILEPATH), new TypeReference<List<Season>>() {});
            for (Season season : seasons) {
                season.generateLabel();
            }
            this.seasons = seasons;
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void readLeagues() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<League> leagues = mapper.readValue(new File(LEAGUENAMESFILEPATH), new TypeReference<List<League>>() {});
            this.leagues = leagues;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<League> getLeagues() {
        return leagues;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public League getCurrentLeague() {
        return currentLeague;
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentLeague(League league) {
        this.currentLeague = league;
    }

    public void setCurrentSeason(Season season) {
        this.currentSeason = season;
    }
}