$(document).ready(function(){
    ates.queueTableRowTplFn = doT.template($('#queue_table_row_tpl').text(), undefined, undefined);

    ates.refreshQueueByExecutionId($("#current_execution_id").val());
    setInterval("ates.refreshQueueByExecutionId($(\"#current_execution_id\").val())", 5000);
});