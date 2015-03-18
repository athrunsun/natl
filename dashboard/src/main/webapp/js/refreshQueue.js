$(document).ready(function(){
    ates.queueTableRowTplFn = doT.template($('#queue_table_row_tpl').text(), undefined, undefined);

    ates.refreshQueue($("#queue_table"));
    setInterval("ates.refreshQueue($(\"#queue_table\"))", 5000);
});