package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.QueueEntryWithAdditionalInfo;

public class QueueController extends Controller {
    public void index() {
        render("index.html");
    }

    public void fetchAllQueueEntriesWithResultAsJson(){
        renderJson(QueueEntryWithAdditionalInfo.createMapListForAllQueueEntries());
    }

    public void fetchQueueEntriesWithResultByExecutionIdAsJson() {
        renderJson(QueueEntryWithAdditionalInfo.createMapList(getParaToLong("executionId")));
    }
}
