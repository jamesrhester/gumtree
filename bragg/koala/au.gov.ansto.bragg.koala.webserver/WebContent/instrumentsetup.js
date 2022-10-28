var title = "Koala";
var batchEnabled = false;
var timeEstimationEnabled = true;
var histmemTypes = [];

function adaptBs(val) {
	var pos = Number(val);
	if (val < 66 && val > 62) {
		return "in";
	} else {
		return "out";
	}
}

function adaptTime(val) {
	try {
		var t = parseInt(val);
		return t > 0 ? t + " s" : "--";
	} catch (e) {
		return "";
	}
	
}

var devices = [
			   {"group":"EXPERIMENT", 
			 	   "items":[{"classId":"gumstatus", "deviceId":"/experiment/gumtree_status", "title":"Scan Status", "units":""},
			 		   		{"classId":"currpoint", "deviceId":"/experiment/currpoint", "title":"Frame ID", "units":""},
			 	            {"classId":"imagestate", "deviceId":"/instrument/image/state", "title":"Phase", "units":""},
			 	            {"classId":"exporemain", "deviceId":"/instrument/image/timer_sec", "title":"Exposure time remaining", "units":"", "adapt":adaptTime},
			 	            {"classId":"temperature", "deviceId":"/sample/tc1/sensor", "title":"Temperature", "units":"K"}
			 	            ]
			   },
			   {"group":"NEUTRON SOURCE", 
			 	   "items":[{"classId":"reactorPower", "deviceId":"/instrument/source/power", "title":"Reactor Power", "units":"MW"}
			 	            ]
			   },
               {"group":"SHUTTER STATUS", 
            	   "items":[{"classId":"secondary", "deviceId":"/instrument/sis/status/secondary", "title":"Secondary Shutter", "units":""},
            		   		{"classId":"tertiary", "deviceId":"/instrument/sis/status/tertiary", "title":"Tertiary Shutter", "units":""},
            		   		{"classId":"fast_shutter", "deviceId":"/instrument/sis/status/fast_shutter", "title":"Fast Shutter", "units":""}
            	            ]
               },
               {"group":"SAMPLE", 
            	   "items":[{"classId":"sphi", "deviceId":"sr", "title":"Sample Phi", "units":"\u00B0"},
            		   		{"classId":"schi", "deviceId":"schi", "title":"Sample Chi", "units":"degrees"},
            	            {"classId":"samx", "deviceId":"sx", "title":"Sample X", "units":"mm"},
            	            {"classId":"samy", "deviceId":"sy", "title":"Sample Y", "units":"mm"},
            	            {"classId":"samz", "deviceId":"sz", "title":"Sample Z", "units":"mm"}
            	            ]
               },
               {"group":"INSTRUMENT", 
            	   "items":[{"classId":"dcz", "deviceId":"dcz", "title":"Detector Height", "units":"mm"},
            		   		{"classId":"rdz", "deviceId":"rdz", "title":"Reading Head", "units":"mm"}
            	            ]
               }
               ];

var nsItems = [
    
    ];

//var histmemUrl = "dae/rest/image?type=TOTAL_HISTOGRAM_XY&screen_size_x=800&screen_size_y=600";
//var histmemUrl = "dae/rest/image?type=$HISTMEM_TYPE&screen_size_x=800";
var histmemUrl = null;

var histmemTypes = [
                    {"id" : "TOTAL_HISTOGRAM_XY", "text" : "Total x-y histogram", "isDefault" : true},
                    {"id" : "TOTAL_HISTOGRAM_XT", "text" : "Total x-t histogram"},
                    {"id" : "TOTAL_HISTOGRAM_YT", "text" : "Total y-t histogram"},
                    {"id" : "TOTAL_HISTOGRAM_X", "text" : "Total x histogram"},
                    {"id" : "TOTAL_HISTOGRAM_Y", "text" : "Total y histogram"},
                    {"id" : "TOTAL_HISTOGRAM_T", "text" : "Total t histogram"}
                    ];