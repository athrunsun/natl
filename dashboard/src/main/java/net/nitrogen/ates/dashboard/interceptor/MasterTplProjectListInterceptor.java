package net.nitrogen.ates.dashboard.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import net.nitrogen.ates.core.model.ProjectModel;

public class MasterTplProjectListInterceptor implements Interceptor {
    @Override
    public void intercept(ActionInvocation ai) {
        ai.getController().setAttr("projectList", ProjectModel.me.findAllProjects());
        ai.invoke();
    }
}
