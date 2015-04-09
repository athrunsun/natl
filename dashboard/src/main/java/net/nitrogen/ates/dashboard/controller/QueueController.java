package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.QueueEntryListFactory;
import net.nitrogen.ates.core.model.QueueEntryModel;

public class QueueController extends Controller {
    public void index() {
        render("index.html");
    }

    public void fetchAllQueueEntriesWithResultAsJson(){
        renderJson(QueueEntryListFactory.createMapListForAllQueueEntriesWithPaging(getParaToInt("pageNumber")));
    }

    public void fetchQueueEntriesWithResultByExecutionIdAsJson() {
        renderJson(QueueEntryListFactory.createMapListWithPaging(getParaToLong("executionId"), getParaToInt("pageNumber")));
    }

    public void fetchAllQueueEntriesTotalPageCountAJAX() {
        renderText(String.format("%d", QueueEntryModel.me.allEntriesPageCount()));
    }

    public void fetchQueueEntriesTotalPageCountByExecutionIdAJAX() {
        renderText(String.format("%d", QueueEntryModel.me.entriesPageCount(getParaToLong("executionId"))));
    }
}
