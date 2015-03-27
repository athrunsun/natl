$(document).ready(function(){
	$('img.img-thumb').on('click',function(){
		var src = $(this).attr('src');
		var img = '<img src="' + src + '"/>';
		
		$.Dialog({
	        overlay: true,
	        shadow: true,
	        flat: true,
	        content: '',
	        onShow: function(_dialog){
	            var content = _dialog.children('.content');
	            content.html(img);
	        }
	    });
   	});
})