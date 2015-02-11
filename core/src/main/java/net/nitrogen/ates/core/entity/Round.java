package net.nitrogen.ates.core.entity;

import net.nitrogen.ates.core.model.RoundModel;

public class Round {
    private long id;
    private String name;
    private long projectId;

    public static Round create(RoundModel m) {
        Round round = new Round();
        round.setId(m.getLong(RoundModel.Fields.ID));
        round.setName(m.getStr(RoundModel.Fields.NAME));
        round.setProjectId(m.getLong(RoundModel.Fields.PROJECT_ID));
        return round;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }
}
