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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {

    public static void main(String[] args) throws DataAccessException, IOException {
        Credentials myCredentials = new Credentials("mmrp", "i3MUQqT3QzpiX5MdZhmIVK4AFx5aGuAcfVYUCr8N");
        Serializer mySerializer = new GsonSerializer();
        RestClient myClient = new RetrofitRestClient();
        List<Player> myList = new ArrayList<Player>();
        PowerRank myRank = new PowerRank(myList, myCredentials, mySerializer, myClient);

        Tournament testTourney = myRank.challongeHelper.getTournament("saturday_night_smash_9singles");

        List<Tournament> tourniesUnderConsideration = myRank.gatherTourniesInLastXMonths(testTourney.getCreatedAt());
        for (Tournament tourney : tourniesUnderConsideration) {
             myRank.processTournamentResults(tourney);
        }

        myRank.getPRPlayers().sort(Comparator.comparing(Player::getELO));
        for (Player player : myRank.getPRPlayers()){
            System.out.println(player.toString());
        }


    }


}
