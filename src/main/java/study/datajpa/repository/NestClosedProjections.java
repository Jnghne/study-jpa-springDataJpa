package study.datajpa.repository;

public interface NestClosedProjections {
    String getUsername();

    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }
}
