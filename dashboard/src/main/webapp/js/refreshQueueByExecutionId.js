$(document).ready(function(){
    ates.queueEntryTableLocator = "#queue_table";
    ates.queueEntryTableRefreshExecutionId = $("#current_execution_id").val();
    ates.reloadQueuePaginationThenEntries("first", null, ates.queueEntryTableRefreshExecutionId);
});