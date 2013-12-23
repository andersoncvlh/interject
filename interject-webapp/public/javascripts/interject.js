$(function() {
	var goButtons = $(".button-go");
	$.each(goButtons, function(index, value) {
		$(this).click(function() {
			var actionUrl = $('#action-url-' + index + '');
			window.location = actionUrl.html();
		});
	});

	var successFn = function(data) {
		console.debug("Success of Ajax Call");
		console.debug(data);
	};
	
	var errorFn = function(err) {
		console.debug("Error of ajax Call");
		console.debug(err);
	}
	
	var rateUp = $(".rate-up");
	$.each(rateUp, function(index, value) {
		$(this).click(function() {
			var actionName = $('#action-name-' + index + '').html();
//			jsRoutes.controllers.Application.rate(actionName, 'up').ajax({success:successFn, error:errorFn});
			var entry = actionName + ", up";
			$("#last-rated").html("" + entry + "");
			$("#feedback-form").modal('show');
		});
	});

	var rateDown = $(".rate-down");
	$.each(rateDown, function(index, value) {
		$(this).click(function() {
			var actionName = $('#action-name-' + index + '').html();
//			jsRoutes.controllers.Application.rate(actionName, 'down').ajax({success:successFn, error:errorFn});
			var entry = actionName + ", down";
			$("#last-rated").html("" + entry + "");
			$("#feedback-form").modal('show');
		});
	});

	$('#feedback-send').click(function() {
		var message = $('#feedback-message');
		var entry = $("#last-rated").html() + ", " + message.val();
		jsRoutes.controllers.Application.sendFeedback(entry).ajax({success:successFn, error:errorFn});
		$("#feedback-form").modal('hide');
		message.val('');
		// close and reset form
	});
	
	$('#feedback-close').click(function() {
		var entry = $("#last-rated").html();
		jsRoutes.controllers.Application.rate(entry).ajax({success:successFn, error:errorFn});
		$("#feedback-form").modal('hide');
		// close and reset form
	});
});