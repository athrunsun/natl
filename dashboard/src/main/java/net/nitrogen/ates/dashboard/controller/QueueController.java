package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.QueueEntryModel;

public class QueueController extends Controller {
    public void index() {
        render("index.html");
    }

    public void fetchAllQueueEntriesAsJson(){
        renderJson(QueueEntryModel.me.findAllEntriesAsModelList());
    }

    public void fetchQueueEntriesByRoundIdAsJson() {
        renderJson(QueueEntryModel.me.findEntriesAsModelList(getParaToLong("roundId")));
    }
}
