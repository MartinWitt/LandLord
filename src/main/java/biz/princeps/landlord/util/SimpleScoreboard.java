package biz.princeps.landlord.util;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SimpleScoreboard {

    private Scoreboard scoreboard;

    private String title;
    private List<String> scores;
    private Player player;

    public SimpleScoreboard(String title, Player p) {
        Objects.requireNonNull(p);
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = title;
        this.scores = new ArrayList<>();
        this.player = p;
    }

    public void add(String text) {
        add(text, scores.size());
    }

    public void add(String text, Integer score) {
        Preconditions.checkArgument(text.length() < 48, "text cannot be over 48 characters in length");
        //text = fixDuplicates(text);
        scores.add(score, text);
    }

    private String fixDuplicates(String text) {
        while (scores.contains(text))
            text += "§r";
        if (text.length() > 48)
            text = text.substring(0, 47);
        return text;
    }

    private void build() {
        String s = (title.length() > 16 ? title.substring(0, 15) : title);
        Objective obj = scoreboard.getObjective(s);
        if (obj == null) {
            obj = scoreboard.registerNewObjective(s, "dummy");
        }
        obj.setDisplayName(title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (int i = 0; i < scores.size(); i++) {
            obj.getScore(scores.get(i)).setScore(i);
        }
    }

    public void reset() {
        scores.clear();
        deactivate();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void send() {
        build();
        player.setScoreboard(scoreboard);
    }

    public void deactivate() {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}