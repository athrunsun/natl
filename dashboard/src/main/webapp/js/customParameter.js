(function (ates, $, undefined) {
    ates.bindRemoveParaOnClickEventHandler = function() {
        $("#custom_params .remPara").off('click');

        $("#custom_params .remPara").on('click', function (event) {
            if(confirm('Are you sure?')) {
                $(this).parent().parent().remove();
            }

            event.preventDefault();
        });
    }
})(window.ates = window.ates || {}, jQuery)