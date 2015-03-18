package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.QueueEntryModel;
import net.nitrogen.ates.core.model.QueueEntryWithResult;

public class QueueController extends Controller {
    public void index() {
        render("index.html");
    }

    public void fetchAllQueueEntriesWithResultAsJson(){
        renderJson(QueueEntryWithResult.createMapList(QueueEntryModel.me.findAllEntries()));
    }

    public void fetchQueueEntriesWithResultByExecutionIdAsJson() {
        renderJson(QueueEntryWithResult.createMapList(QueueEntryModel.me.findEntries(getParaToLong("executionId"))));
    }
}
