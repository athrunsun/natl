$(document).ready(function(){
    ates.queueTableRowTplFn = doT.template($('#queue_table_row_tpl').text(), undefined, undefined);

    ates.refreshQueueByExecutionId($("#queue_table"), $("#current_execution_id").val());
    setInterval("ates.refreshQueueByExecutionId($(\"#queue_table\"), $(\"#current_execution_id\").val())", 5000);
});