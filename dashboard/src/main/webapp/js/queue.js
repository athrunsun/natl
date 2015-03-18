(function(ates, $, undefined){
    ates.queueTableRowTplFn = undefined;

    ates.refreshQueueSuccessHandler = function($entryTable, result) {
        $entryTable.find("> tbody > tr").remove();
        var tbody = "";

        $.each(result, function (index, item) {
            var trCssClass = ates.composeQueueTableRowCssClass(item.status);
            var nameTDContent = ates.composeQueueTableRowNameTDContent(item.name);
            var statusTDContent = ates.composeQueueTableRowStatusTDContent(item.status);
            var startTimeTDContent = ates.composeQueueTableRowDateTimeTDContent(item.start_time);
            var endTimeTDContent = ates.composeQueueTableRowDateTimeTDContent(item.end_time);
            var execResultTDContent = ates.composeQueueTableRowExecResultTDContent(item.exec_result);

            tbody += ates.queueTableRowTplFn({
                rowCssClass: trCssClass,
                id: item.id,
                status: statusTDContent,
                name: nameTDContent,
                slave_name: item.slave_name,
                index: item.index,
                //start_time:startTimeTDContent,
                //end_time:endTimeTDContent,
                start_time: item.start_time,
                end_time: item.end_time,
                execution_id: item.execution_id,
                jvm_options: item.jvm_options,
                params: item.params,
                exec_result: execResultTDContent
            });
        });

        $entryTable.find("> tbody").append(tbody);
    }

    ates.refreshQueue = function($entryTable){
        $.ajax({
            type: "POST",
            dataType: "json",
            //contentType: "application/json; charset=utf-8",
            url: ates.contextPath + "/queue/fetchAllQueueEntriesWithResultAsJson",
            //data: "{}",
            success: function(result) {
                ates.refreshQueueSuccessHandler($entryTable, result);
            }
        });
    }

    ates.refreshQueueByExecutionId = function($entryTable, executionId){
        $.ajax({
            type: "POST",
            dataType: "json",
            //contentType: "application/json; charset=utf-8",
            url: ates.contextPath + "/queue/fetchQueueEntriesWithResultByExecutionIdAsJson",
            data: "executionId=" + executionId,
            success: function(result) {
                ates.refreshQueueSuccessHandler($entryTable, result);
            }
        });
    }

    ates.composeQueueTableRowCssClass = function(statusId){
        var cssClass = "callout-default";

        switch (statusId) {
            case ates.queueEntryStatusEnum["WAITING"]:
                cssClass = "callout-default";
                break;
            case ates.queueEntryStatusEnum["RUNNING"]:
                cssClass = "callout-info";
                break;
            case ates.queueEntryStatusEnum["FINISHED"]:
                cssClass = "callout-success";
                break;
            case ates.queueEntryStatusEnum["STOPPED"]:
                cssClass = "callout-danger";
                break;
            default:
                break;
        }

        return cssClass;
    }

    ates.composeQueueTableRowNameTDContent = function(name){
        var entryName = name;

        if (entryName.length > ates.maxTestNameLength) {
            entryName = entryName.substr(0, ates.maxTestNameLength - 3) + "...";
        }

        return "<span title=\"" + name + "\">" + entryName + "</span>";
    }

    ates.composeQueueTableRowStatusTDContent = function(statusId){
        var cssClass = "label";
        var statusLabel = "";

        switch (statusId) {
            case ates.queueEntryStatusEnum["WAITING"]:
                statusLabel = "WAITING";
                break;
            case ates.queueEntryStatusEnum["RUNNING"]:
                cssClass += " info";
                statusLabel = "RUNNING";
                break;
            case ates.queueEntryStatusEnum["FINISHED"]:
                cssClass += " success";
                statusLabel = "FINISHED";
                break;
            case ates.queueEntryStatusEnum["STOPPED"]:
                cssClass += " error";
                statusLabel = "STOPPED";
                break;
            default:
                statusLabel = "UNKNOWN";
                break;
        }

        return "<span class=\"" + cssClass + "\">" + statusLabel + "</span>";
    }

    ates.composeQueueTableRowDateTimeTDContent = function(jsonDateTime){
        return (!jsonDateTime ? "N/A" : ates.convertJSONDateToString(jsonDateTime));
    }

    ates.composeQueueTableRowExecResultTDContent = function(execResultId) {
        var cssClass = "label";
        var execResultLabel = "";

        switch (execResultId) {
            case ates.execResultEnum["UNKNOWN"]:
                execResultLabel = "UNKNOWN";
                break;
            case ates.execResultEnum["SKIPPED"]:
                cssClass += " bg-yellow";
                execResultLabel = "SKIPPED";
                break;
            case ates.execResultEnum["PASSED"]:
                cssClass += " success";
                execResultLabel = "PASSED";
                break;
            case ates.execResultEnum["FAILED"]:
                cssClass += " error";
                execResultLabel = "FAILED";
                break;
            default:
                execResultLabel = "UNKNOWN";
                break;
        }

        return "<span class=\"" + cssClass + "\">" + execResultLabel + "</span>";
    }
})(window.ates = window.ates || {}, jQuery)