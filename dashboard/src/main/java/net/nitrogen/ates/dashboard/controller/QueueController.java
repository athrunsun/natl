package net.nitrogen.ates.dashboard.controller;

import com.jfinal.core.Controller;
import net.nitrogen.ates.core.model.queue_entry.QueueEntryListFactory;
import net.nitrogen.ates.core.model.queue_entry.QueueEntryModel;

public class QueueController extends Controller {
    public void index() {
        render("index.html");
    }

    public void fetchAllQueueEntriesWithResultAJAX(){
        renderJson(QueueEntryListFactory.createMapListForAllQueueEntriesWithPaging(getParaToInt("pageNumber")));
    }

    public void fetchQueueEntriesWithResultForProjectAJAX() {
        renderJson(QueueEntryListFactory.createMapListForProjectWithPaging(ControllerHelper.getProjectPrefFromCookie(this), getParaToInt("pageNumber")));
    }

    public void fetchQueueEntriesWithResultForExecutionAJAX() {
        renderJson(QueueEntryListFactory.createMapListForExecutionWithPaging(getParaToLong("executionId"), getParaToInt("pageNumber")));
    }

    public void fetchAllQueueEntriesTotalPageCountAJAX() {
        renderText(String.format("%d", QueueEntryModel.me.allEntriesPageCount()));
    }

    public void fetchQueueEntriesTotalPageCountForProjectAJAX() {
        renderText(String.format("%d", QueueEntryModel.me.entriesPageCountForProject(ControllerHelper.getProjectPrefFromCookie(this))));
    }

    public void fetchQueueEntriesTotalPageCountForExecutionAJAX() {
        renderText(String.format("%d", QueueEntryModel.me.entriesPageCountForExecution(getParaToLong("executionId"))));
    }
}
