package com.mucholabs;

import java.util.List;
import java.util.Objects;

public class Player {

    private String gamerTag;
    private int ELO;
    private long gamerID;
    private int wins;
    private int losses;

    public Player(String tag, int ELOScore, long ID){
        this.gamerTag = tag;
        this.gamerID = ID;
        this.ELO = ELOScore;
    }

    public Player(String tag, long ID){
        this.gamerTag = tag;
        this.gamerID = ID;
        this.ELO = 1500;
    }

    public Player(String tag){
        this.gamerTag = tag;
        this.gamerID = -1;
        this.ELO = 1500;
    }

    public void addWin(){
        this.wins++;
    }

    public void addLoss(){
        this.losses++;
    }

    public int getGamesPlayed(){
        return this.wins + this.losses;
    }
    public void setGamerID(long newID){
        this.gamerID = newID;
    }

    public long getGamerID(){
        return this.gamerID;
    }

    public void setELO(int newScore){
        this.ELO = newScore;
    }

    public int getELO(){
        return this.ELO;
    }

    public String getGamerTag(){
        return this.gamerTag;
    }

    public double getWinLossRatio(){
        double WinLossRatio;
        if (wins > 0) {
            WinLossRatio = (double) this.wins / getGamesPlayed();
        } else WinLossRatio = 0;
        return WinLossRatio;
    }

    public boolean checkIfPlayerIsInRoster(List<Player> listOfPlayers){
        if (listOfPlayers.contains(this)){
            return true;
        } else return false;
    }

    @Override
    public boolean equals(Object o) {
        StringDistanceFinder finder = new StringDistanceFinder();
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        if (finder.similarity(gamerTag.toLowerCase(), player.gamerTag.toLowerCase()) >= 0.66){
            return true;
        } else return false;
//        return gamerTag.equals(player.gamerTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gamerTag);
    }

    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("Tag: " + this.gamerTag + " ELO: " + this.ELO + " W/L%: " + getWinLossRatio());
        return str.toString();
    }
}
