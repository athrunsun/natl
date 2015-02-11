package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Model;
import net.nitrogen.ates.core.entity.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectModel extends Model<ProjectModel> {
    public static final String TABLE = "project";

    public class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String JAR_NAME = "jar_name";
        public static final String JAR_WITH_DEPENDENCY_NAME = "jar_with_dependency_name";
        public static final String GIT_URL = "git_url";
    }

    public static final ProjectModel me = new ProjectModel();

    public Project findProject(long projectId){
        return Project.create(me.findById(projectId));
    }

    public String findJarName(long projectId) {
        return this.findById(projectId).getStr(Fields.JAR_NAME);
    }

    public String findJarWithDependencyName(long projectId) {
        return this.findById(projectId).getStr(Fields.JAR_WITH_DEPENDENCY_NAME);
    }

    public List<Project> findAllProjects(){
        List<ProjectModel> projectModelList = this.find(String.format(
                "SELECT `%s`,`%s`,`%s`,`%s`,`%s` FROM `%s`",
                Fields.ID,
                Fields.NAME,
                Fields.JAR_NAME,
                Fields.JAR_WITH_DEPENDENCY_NAME,
                Fields.GIT_URL,
                TABLE));

        List<Project> projects = new ArrayList<Project>();

        for(ProjectModel m : projectModelList){
            projects.add(Project.create(m));
        }

        return projects;
    }
}
