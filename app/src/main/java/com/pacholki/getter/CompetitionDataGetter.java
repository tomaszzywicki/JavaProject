package com.pacholki.getter;

import java.io.IOException;

import com.pacholki.entity.Competition;
import com.pacholki.entity.League;
import com.pacholki.entity.Season;

public class CompetitionDataGetter extends DataGetter {
    
    League league;
    Season season;

    public CompetitionDataGetter(Competition competition) {
        this.league = competition.getLeague();
        this.season = competition.getSeason();
    }

    @Override
    public void run() {
        downloadsStarted += 1;
        int id = downloadsStarted;

        System.out.println(id + ". -----\nTrying to download data for:\n" + league.getName() + "\t" + season.getLabel() + "\n-----");

        int exitCode = getTeams();
        downloadsFinished += 1;

        if (exitCode == 0) {
            System.out.println(id + ". -----\nDownload successful: \n" + league.getName() + "\t" + season.getLabel() + "\nActive downloads: " + (downloadsStarted - downloadsFinished) + "\n-----");
        } else {
            System.out.println(id + ". -----\nFailed to download: \n" + league.getName() + "\t" + season.getLabel() + "\nActive downloads: " + (downloadsStarted - downloadsFinished) + "\n-----");
        }
    }

    public int getTeams() {

        String scriptName = "get_league_teams.py";
        String scriptPath = SCRIPT_DIR + scriptName;

        ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath, league.getFBrefID(), season.getFBrefID());
        processBuilder.redirectErrorStream(false);

        int exitCode = 2;
        try {
            Process process = processBuilder.start();
            exitCode = process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return exitCode;
    }
}
