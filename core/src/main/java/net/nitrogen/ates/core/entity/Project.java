package net.nitrogen.ates.core.entity;

import net.nitrogen.ates.core.model.ProjectModel;

public class Project {
    private long id;
    private String name;
    private String jarName;
    private String jarWithDependencyName;
    private String gitUrl;

    public static Project create(ProjectModel m) {
        Project project = new Project();
        project.setId(m.getLong(ProjectModel.Fields.ID));
        project.setName(m.getStr(ProjectModel.Fields.NAME));
        project.setJarName(m.getStr(ProjectModel.Fields.JAR_NAME));
        project.setJarWithDependencyName(m.getStr(ProjectModel.Fields.JAR_WITH_DEPENDENCY_NAME));
        project.setGitUrl(m.getStr(ProjectModel.Fields.GIT_URL));
        return project;
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

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getJarWithDependencyName() {
        return jarWithDependencyName;
    }

    public void setJarWithDependencyName(String jarWithDependencyName) {
        this.jarWithDependencyName = jarWithDependencyName;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }
}
