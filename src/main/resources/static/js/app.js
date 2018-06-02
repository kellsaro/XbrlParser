var app = angular.module("app", ['ngFileUpload']);



app.controller('xbrlController', ['$scope', 'Upload', '$timeout', function ($scope, Upload, $timeout) {

    $scope.uploadFiles = function(file) {
        
    	$scope.f = file;
    	if (file != null){
    		
    		$scope.name = file.name;
    		   	
	        if (file.name.includes(".xml") || file.name.includes(".xbrl") ) {
	        	if (file.size <= 15000000) {
	        		
	 	            file.upload = Upload.upload({
		                url: 'https://xbrlframework.herokuapp.com/upload',
	 	            	//url: 'http://localhost:8080/upload',
		                data: {apifile: file}
		            });
	 	            
	 	            if (file.size > 10000000){
	 	            	$scope.f = "Parsing...\n This file has a very big size ("+(file.size)+" bytes), its processing will take several seconds...";
	 	            }else if (file.size >  5000000){
	        			$scope.f = "Parsing...\n This size file ("+(file.size)+" bytes) will take few seconds...";
	        		}else{
	        			$scope.f = "Parsing...";
	        		}
	        		
	        		
		
		            file.upload.then(
		            	function success (response) {
		            		$scope.f = JSON.stringify(response.data, undefined, 4);
		            		$timeout(function () {
		            			console.log(response.data);
		            			console.log(response.status);
		            		});
		            	}, 
		            	function unsuccess (response) {
		            		console.log("response is a error: "+response.data);
		            		console.log("response.status: "+response.status);
		            		if (response.status == 500)
		            			$scope.f = "Sorry for this error! Possible reasons:  \n " +
		            					"[1] This api-rest version just parses xBRL-XML INSTANCE file from SEC standard; \n " +
		            					"[2] There is some invalid json character into XBRL-XML document (please! send us this file, to find it out); \n " +
		            					"[3] Json-based response string was malformed by tool (please! inform us); \n" +
		            					"[4] This XBRL-XML file is malformed. Pay attention: \n"+
		            					"[4.1] The XML prolog is mandatory in file (e.g. <?xml version=\"1.0\" encoding=\"UTF-8\"?>) \n"+
		            					"[4.2] The entity \"nbsp\" can have been referenced into XBRL-XML file, but not declared. if so, you must declare it into file (e.g. <!DOCTYPE inline_dtd[<!ENTITY nbsp \"&#160;\">]>)" ;
		            		},
		            	function (evt) {
		            		file.progress = Math.min(100, parseInt(100.0 * 
		                                         evt.loaded / evt.total));
		            });
	        	}else{
	        		console.log("For while, max size per file is 15mb.");
	    			$scope.f = "Sorry! For while, max size per file is 15mb.";        	}
	        }else{ // if is xml or xbrl file
	        	console.log("file must be in XML or XBRL format");
	        	$scope.f = "Sorry! This file must be in XML or XBRL format.";
	        }
    	} // if is not null
    }

}]);
