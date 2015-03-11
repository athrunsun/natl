package net.nitrogen.ates.core.model;

import com.jfinal.plugin.activerecord.Model;

import java.util.List;

public class CustomEnvModel extends Model<CustomEnvModel> {
    public static final String TABLE = "custom_env";

    public class Fields {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PROJECT_ID = "project_id";
    }

    public static final CustomEnvModel me = new CustomEnvModel();

    public long getId() {
        return this.getLong(Fields.ID);
    }

    public void setId(long id) {
        this.set(Fields.ID, id);
    }

    public String getName() {
        return this.getStr(Fields.NAME);
    }

    public void setName(String name) {
        this.set(Fields.NAME, name);
    }

    public long getProjectId() {
        return this.getLong(Fields.PROJECT_ID);
    }

    public void setProjectId(long projectId) {
        this.set(Fields.PROJECT_ID, projectId);
    }

//    public List<CustomEnv> findEnvs(long projectId) {
//        String sql = String.format(
//                "SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?",
//                Fields.ID,
//                Fields.NAME,
//                Fields.PROJECT_ID,
//                TABLE,
//                Fields.PROJECT_ID);
//
//        List<CustomEnv> envs = new ArrayList<>();
//
//        for(CustomEnvModel m : find(sql, projectId)) {
//            envs.add(CustomEnv.create(m));
//        }
//
//        return envs;
//    }

    public List<CustomEnvModel> findEnvsAsModelList(long projectId) {
        String sql = String.format(
                "SELECT `%s`,`%s`,`%s` FROM `%s` WHERE `%s`=?",
                Fields.ID,
                Fields.NAME,
                Fields.PROJECT_ID,
                TABLE,
                Fields.PROJECT_ID);

        return find(sql, projectId);
    }
}
