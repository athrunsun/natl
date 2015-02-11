package net.nitrogen.ates.core.entity;

import net.nitrogen.ates.core.model.CustomEnvModel;

public class CustomEnv {
    private long id;
    private String name;
    private long projectId;

    public static CustomEnv create(CustomEnvModel m) {
        CustomEnv customEnv = new CustomEnv();
        customEnv.setId(m.getLong(CustomEnvModel.Fields.ID));
        customEnv.setName(m.getStr(CustomEnvModel.Fields.NAME));
        customEnv.setProjectId(m.getLong(CustomEnvModel.Fields.PROJECT_ID));
        return customEnv;
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
