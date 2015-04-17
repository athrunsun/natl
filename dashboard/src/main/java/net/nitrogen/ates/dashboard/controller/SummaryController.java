package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.execution.ExecutionModel;

public class SummaryController extends Controller {
    public void index() {
        render("index.html");
    }

    public void fetchPassrateOfRecentExecutionsAsJson() {
        renderJson(ExecutionModel.me.passrateOfRecentExecutions(getParaToLong("projectId"), 6));
    }
}
