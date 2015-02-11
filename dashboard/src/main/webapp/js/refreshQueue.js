$(document).ready(function(){
    ates.queueTableRowTplFn = doT.template($('#queue_table_row_tpl').text(), undefined, undefined);

    ates.refreshQueue();
    setInterval("ates.refreshQueue()", 5000);
});