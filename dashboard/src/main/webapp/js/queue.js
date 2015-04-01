(function(ates, $, undefined){
    ates.queueEntryTableRefreshInterval = 5000;
    ates.currentQueuePageNumber = 1;
    ates.refreshQueueIntervalId = null;
    ates.refreshQueueByExecutionIdIntervalId = null;
    ates.queuePaginationTplFn = doT.template($('#queue_pagination_tpl').text(), undefined, undefined);
    ates.queueTableRowTplFn = doT.template($('#queue_table_row_tpl').text(), undefined, undefined);
    ates.queueEntryTableLocator = null;
    ates.queueEntryTableRefreshExecutionId = null;

    ates.reloadQueuePaginationThenEntries = function(pageType, pageNumber, executionId) {
        var requestPath = "/queue/fetchQueueEntriesTotalPageCountByExecutionIdAJAX";
        var requestData = "executionId=" + ates.queueEntryTableRefreshExecutionId;

        if(executionId === undefined || executionId === null) {
            requestPath = "/queue/fetchAllQueueEntriesTotalPageCountAJAX";
            requestData = null;
        }

        $.ajax({
            type: "POST",
            dataType: "text",
            //contentType: "application/json; charset=utf-8",
            url: ates.contextPath + requestPath,
            data: requestData,
            success: function(result) {
                var $queueEntryTable = $(ates.queueEntryTableLocator);
                var $paginationContainer = $queueEntryTable.find("> tfoot > tr > td");
                var $pagination = $paginationContainer.find(".pagination");
                var totalPage = parseInt(result, 10);

                if(pageType === "num") {
                    ates.currentQueuePageNumber = pageNumber;
                }else if(pageType === "first" || pageType === "prev" || pageType === "next" || pageType === "last") {
                    if($pagination.find("> ul > li." + pageType).hasClass("disabled") === true) {
                        return;
                    }

                    if(pageType === "first") {
                        ates.currentQueuePageNumber = 1;
                    }else if(pageType === "prev") {
                        ates.currentQueuePageNumber = ates.currentQueuePageNumber - 1;
                    }else if(pageType === "next") {
                        ates.currentQueuePageNumber = ates.currentQueuePageNumber + 1;
                    }else if(pageType === "last") {
                        ates.currentQueuePageNumber = totalPage;
                    }
                }else {
                    return;
                }

                if(ates.currentQueuePageNumber < 1) {
                    ates.currentQueuePageNumber = 1;
                }

                if(ates.currentQueuePageNumber > totalPage) {
                    ates.currentQueuePageNumber = totalPage;
                }

                $pagination.remove();

                $paginationContainer.append(ates.queuePaginationTplFn({
                    totalPageCount:totalPage,
                    currentPageNumber:ates.currentQueuePageNumber}));

                var $pagination = $paginationContainer.find(".pagination");

                $pagination.find("> ul > li.first > a").on("click", function() {
                    ates.reloadQueuePaginationThenEntries("first", null, ates.queueEntryTableRefreshExecutionId);
                });

                $pagination.find("> ul > li.prev > a").on("click", function() {
                    ates.reloadQueuePaginationThenEntries("prev", null, ates.queueEntryTableRefreshExecutionId);
                });

                $pagination.find("> ul > li.number > a").on("click", function() {
                    ates.reloadQueuePaginationThenEntries("num", parseInt($(this).attr("data-id"), 10), ates.queueEntryTableRefreshExecutionId);
                });

                $pagination.find("> ul > li.next > a").on("click", function() {
                    ates.reloadQueuePaginationThenEntries("next", null, ates.queueEntryTableRefreshExecutionId);
                });

                $pagination.find("> ul > li.last > a").on("click", function() {
                    ates.reloadQueuePaginationThenEntries("last", null, ates.queueEntryTableRefreshExecutionId);
                });

                if(ates.refreshQueueIntervalId !== null) {
                    clearInterval(ates.refreshQueueIntervalId);
                }

                if(executionId === undefined || executionId === null) {
                    ates.refreshQueue();
                    ates.refreshQueueIntervalId = setInterval("ates.refreshQueue()", ates.queueEntryTableRefreshInterval);
                }else {
                    ates.refreshQueueByExecutionId();
                    ates.refreshQueueByExecutionIdIntervalId = setInterval("ates.refreshQueueByExecutionId()", ates.queueEntryTableRefreshInterval);
                }
            }
        });
    }

    ates.refreshQueueSuccessHandler = function(result) {
        var $queueEntryTable = $(ates.queueEntryTableLocator);
        $queueEntryTable.find("> tbody > tr").remove();
        var tbody = "";

        $.each(result, function (index, item) {
            var trCssClass = ates.composeQueueTableRowCssClass(item.status);
            var nameTDContent = ates.composeQueueTableRowNameTDContent(item.name);
            var statusTDContent = ates.composeQueueTableRowStatusTDContent(item.status);
            var startTimeTDContent = ates.composeQueueTableRowDateTimeTDContent(item.start_time);
            var endTimeTDContent = ates.composeQueueTableRowDateTimeTDContent(item.end_time);
            var execResultTDContent = ates.composeQueueTableRowExecResultTDContent(item.test_result_id, item.exec_result);

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

        $queueEntryTable.find("> tbody").append(tbody);
    }

    ates.refreshQueue = function(){
        $.ajax({
            type: "POST",
            dataType: "json",
            //contentType: "application/json; charset=utf-8",
            url: ates.contextPath + "/queue/fetchAllQueueEntriesWithResultAsJson",
            data: "pageNumber=" + ates.currentQueuePageNumber,
            success: function(result) {
                ates.refreshQueueSuccessHandler(result);
            }
        });
    }

    ates.refreshQueueByExecutionId = function(){
        $.ajax({
            type: "POST",
            dataType: "json",
            //contentType: "application/json; charset=utf-8",
            url: ates.contextPath + "/queue/fetchQueueEntriesWithResultByExecutionIdAsJson",
            data: "executionId=" + ates.queueEntryTableRefreshExecutionId + "&pageNumber=" + ates.currentQueuePageNumber,
            success: function(result) {
                ates.refreshQueueSuccessHandler(result);
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

    ates.composeQueueTableRowExecResultTDContent = function(testResultId, execResultId) {
        var cssClass = "label";
        var execResultLabel = "";
        var isResultUnknown = false;

        switch (execResultId) {
            case ates.execResultEnum["UNKNOWN"]:
                execResultLabel = "UNKNOWN";
                isResultUnknown = true;
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
                isResultUnknown = true;
                break;
        }

        if(isResultUnknown === false) {
            return "<a target=\"_blank\" href=\"" + ates.contextPath + "/testresult/detail/" + testResultId + "\"><span class=\"" + cssClass + "\">" + execResultLabel + "</span></a>";
        }else {
            return "<span class=\"" + cssClass + "\">" + execResultLabel + "</span>";
        }
    }
})(window.ates = window.ates || {}, jQuery)