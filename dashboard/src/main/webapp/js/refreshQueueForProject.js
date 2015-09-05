$(document).ready(function(){
    ates.refreshQueueLocation = ates.refreshQueueLocationEnum["PROJECT"];
    ates.queueEntryTableLocator = "#queue_table";
    ates.reloadQueuePaginationThenEntries("first", null);
});