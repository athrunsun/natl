package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.QueueEntryWithResult;

public class QueueController extends Controller {
    public void index() {
        render("index.html");
    }

    public void fetchAllQueueEntriesWithResultAsJson(){
        renderJson(QueueEntryWithResult.createMapListForAllQueueEntries());
    }

    public void fetchQueueEntriesWithResultByExecutionIdAsJson() {
        renderJson(QueueEntryWithResult.createMapList(getParaToLong("executionId")));
    }
}
