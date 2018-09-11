var INST_LIST = [
	"bilby",
	"dingo",
	"echidna",
	"emu",
//	"joey",
//	"koala",
	"kookaburra",
	"kowari",
	"pelican",
	"platypus",
	"quokka",
//	"sika",
	"spatz",
	"taipan",
	"wombat"
]

$(document).ready(function() {
	$('#id_a_signout').click(function() {
		signout("sicsConfigMenu.html");
	});
	
	var mdiv = $('#id_div_main_area');
	$.each(INST_LIST, function(idx, val) {
		mdiv.append('<div class="class_div_instrument_option"><a id="id_button_instrument" ' + 
				'class="btn btn-outline-primary btn-block class_button_instrument" ' + 
				'href="sicsConfig.html?inst=' + val + '">' + val.toUpperCase() + ' motor configuration</a></div>');
	});
	
});