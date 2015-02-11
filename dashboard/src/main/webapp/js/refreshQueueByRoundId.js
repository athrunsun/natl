$(document).ready(function(){
    ates.queueTableRowTplFn = doT.template($('#queue_table_row_tpl').text(), undefined, undefined);

    ates.refreshQueueByRoundId($("#current_round_id").val());
    setInterval("ates.refreshQueueByRoundId($(\"#current_round_id\").val())", 5000);
});