$(document).ready(function(){
    ates.refreshQueueLocation = ates.refreshQueueLocationEnum["EXECUTION"];
    ates.queueEntryTableLocator = "#queue_table";
    ates.queueEntryTableRefreshExecutionId = $("#current_execution_id").val();
    ates.reloadQueuePaginationThenEntries("first", null);
});