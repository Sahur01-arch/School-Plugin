package com.ryushin.schoolplugin.manager;

import com.ryushin.ryulib.RyuPlugin;
import java.util.*;

public class SchoolClubManager {

    private final RyuPlugin plugin;
    private final Map<String, Club> clubs = new HashMap<>();
    private final List<String> validRoles = Arrays.asList("President", "VicePresident", "Secretary", "Treasurer", "Member");

    public SchoolClubManager(RyuPlugin plugin) {
        this.plugin = plugin;
    }

    public Result createClub(String name, String description) {
        if (name == null || name.isEmpty()) {
            return new Result(false, "Club name cannot be empty.");
        }
        String key = name.toLowerCase();
        if (clubs.containsKey(key)) {
            return new Result(false, "Club '" + name + "' already exists.");
        }

        Club club = new Club(name, description != null ? description : "", System.currentTimeMillis());
        clubs.put(key, club);
        return new Result(true, "Club '" + name + "' successfully created.");
    }

    public Result deleteClub(String name) {
        if (name == null || name.isEmpty()) return new Result(false, "Club name cannot be empty.");
        String key = name.toLowerCase();
        if (!clubs.containsKey(key)) {
            return new Result(false, "Club '" + name + "' not found.");
        }
        clubs.remove(key);
        return new Result(true, "Club '" + name + "' successfully deleted.");
    }

    public Result addMember(String clubName, String playerName, String uuid, String role) {
        if (clubName == null || playerName == null || uuid == null) {
            return new Result(false, "Incomplete data.");
        }
        String finalRole = role != null && validRoles.contains(role) ? role : "Member";
        String key = clubName.toLowerCase();
        Club club = clubs.get(key);
        if (club == null) {
            return new Result(false, "Club '" + clubName + "' not found.");
        }
        if (club.members.containsKey(uuid)) {
            return new Result(false, playerName + " is already a member of " + clubName + ".");
        }

        club.members.put(uuid, new ClubMember(playerName, finalRole, System.currentTimeMillis()));
        return new Result(true, playerName + " added to " + clubName + " as " + finalRole + ".");
    }

    public Result removeMember(String clubName, String playerName, String uuid) {
        if (clubName == null || uuid == null) return new Result(false, "Incomplete data.");
        String key = clubName.toLowerCase();
        Club club = clubs.get(key);
        if (club == null) return new Result(false, "Club '" + clubName + "' not found.");
        if (!club.members.containsKey(uuid)) return new Result(false, (playerName != null ? playerName : "Player") + " is not a member of " + clubName + ".");

        club.members.remove(uuid);
        return new Result(true, (playerName != null ? playerName : "Player") + " removed from " + clubName + ".");
    }

    public Club getClub(String clubName) {
        if (clubName == null) return null;
        return clubs.get(clubName.toLowerCase());
    }

    public Collection<Club> listClubs() {
        return clubs.values();
    }

    public static class Club {
        private final String name;
        private final String description;
        private final long created;
        private final Map<String, ClubMember> members = new HashMap<>();

        public Club(String name, String description, long created) {
            this.name = name;
            this.description = description;
            this.created = created;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public long getCreated() { return created; }
        public Map<String, ClubMember> getMembers() { return members; }
    }

    public static class ClubMember {
        private final String name;
        private final String role;
        private final long joined;

        public ClubMember(String name, String role, long joined) {
            this.name = name;
            this.role = role;
            this.joined = joined;
        }

        public String getName() { return name; }
        public String getRole() { return role; }
        public long getJoined() { return joined; }
    }

    public static class Result {
        private final boolean success;
        private final String message;

        public Result(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
