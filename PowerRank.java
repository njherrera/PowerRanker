package com.mucholabs;

import at.stefangeyer.challonge.Challonge;
import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Credentials;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.Tournament;
import at.stefangeyer.challonge.rest.RestClient;
import at.stefangeyer.challonge.rest.retrofit.RetrofitRestClient;
import at.stefangeyer.challonge.serializer.Serializer;
import at.stefangeyer.challonge.serializer.gson.GsonSerializer;
import java.time.OffsetDateTime;

import org.apache.commons.lang3.ObjectUtils;
import org.simmetrics.StringDistance;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class PowerRank {

    List<Player> playersInPR;
    Credentials myCredentials;
    Serializer mySerializer;
    RestClient myClient;
    Challonge challongeHelper;


    public PowerRank(List<Player> playerList, Credentials suppliedCredentials, Serializer suppliedSerializer, RestClient suppliedClient){
        this.playersInPR = playerList;
        this.challongeHelper = new Challonge(suppliedCredentials, suppliedSerializer, suppliedClient);
    }

    public List<Player> generatePowerRank(List<Participant> listOfEntrants){
        for (Participant entrant : listOfEntrants){
            Player newPlayer = new Player(entrant.getDisplayName(), entrant.getId());
            if(this.playersInPR.contains(newPlayer) == false){
                this.playersInPR.add(newPlayer);
            } if (this.playersInPR.contains(newPlayer)){
                int indexOfPlayer = this.playersInPR.indexOf(newPlayer);
                this.playersInPR.get(indexOfPlayer).setGamerID(entrant.getId());
            }
        }
        return this.playersInPR;
    }

    public void updateELOScores(Player winner, Player loser){
        Player winningPlayer = getPlayerMatchingID(winner.getGamerID());
        Player losingPlayer = getPlayerMatchingID(loser.getGamerID());
        double expectedScoreOfWinner = 1 / (1 + Math.pow(10, ((loser.getELO()-winner.getELO())/400)));
        double expectedScoreOfLoser = 1 / (1 + Math.pow(10, ((winner.getELO()-loser.getELO())/400)));
        int newWinningPlayerScore = (int) (winner.getELO() + 32 * (1 - expectedScoreOfWinner));
        int newLosingPlayerScore = (int) (loser.getELO() + 32 * (0 - expectedScoreOfLoser));
        winner.setELO(newWinningPlayerScore);
        loser.setELO(newLosingPlayerScore);
    }

    public Player getPlayerMatchingID (Long playerID){
        for(Player player : this.playersInPR){
            Long testID = player.getGamerID();
            if (testID.equals(playerID)){
                return player;
            }
        }
        return null;
//        Player foundPlayer = this.playersInPR.stream()
//                .filter(x -> playerID.equals(x.getGamerID()))
//                .findAny()
//                .orElse(null);
//        return foundPlayer;
    }

    public void processTournamentResults(Tournament tourneyToBeEntered) throws DataAccessException {
        this.playersInPR = generatePowerRank(challongeHelper.getParticipants(tourneyToBeEntered));
        for (Match set : challongeHelper.getMatches(tourneyToBeEntered)) {
            try{
                updateELOScores(getPlayerMatchingID(set.getWinnerId()), getPlayerMatchingID(set.getLoserId()));
                getPlayerMatchingID(set.getWinnerId()).addWin();
                getPlayerMatchingID(set.getLoserId()).addLoss();
            } catch (NullPointerException ex){
                System.out.println("error processing match results, match info: " + set.toString());
            }
        }
    }

    public List<Tournament> generateSNSTournaments(int numberOfTournaments) throws IOException, DataAccessException {
        List<Tournament> tourneyList = new ArrayList<Tournament>();
        for (int i = 1; i <= numberOfTournaments; i++) {
            URL firstCase = new URL("https://challonge.com/saturday_night_smash_" + i + "singles");
            URL secondCase = new URL("https://challonge.com/sns_"+ i + "_singles");
            URL thirdCase = new URL("https://challonge.com/b1n7vvt6");
            try {
                tourneyList.add(challongeHelper.getTournament("saturday_night_smash_" + i + "singles"));
            } catch (DataAccessException ex){
                try {
                    tourneyList.add(challongeHelper.getTournament("sns_"+ i + "_singles"));
                } catch (DataAccessException secondEx){
                    try {
                        tourneyList.add(challongeHelper.getTournament("b1n7vvt6"));
                    } catch (DataAccessException thirdEx){
                        System.out.println("something went terribly horribly wrong");
                    }
                }
            }
        }
        return tourneyList;
    }

    public List<Tournament> gatherTourniesInLastXMonths(OffsetDateTime date) throws IOException, DataAccessException {
        List<Tournament> allTournaments = generateSNSTournaments(18);
        List<Tournament> tourniesInDateRange = new ArrayList<Tournament>();
        for (Tournament entry: allTournaments) {
            try {
                if (entry.getCompletedAt().isAfter(date)) {
                    tourniesInDateRange.add(entry);
                }
            } catch (NullPointerException ex){
                System.out.println(entry.getName());
            }
        }
        return tourniesInDateRange;
    }

    public boolean checkURLValidity(URL link) throws IOException {
        HttpURLConnection huc = (HttpURLConnection) link.openConnection();
        int responseCode = huc.getResponseCode();
        if(responseCode != 404){
            return true;
        } else return false;
    }

    public List<Player> getPRPlayers(){
        return this.playersInPR;
    }
}
