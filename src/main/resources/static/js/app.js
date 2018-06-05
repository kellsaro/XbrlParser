var app = angular.module("app", ['ngFileUpload']);

app.controller('xbrlController', ['$http','$scope', 'Upload', '$timeout', function ($http, $scope, Upload, $timeout) {

	$scope.init = function(){
		$scope.f = '{\n  "xBRL-JSON file version will be printed here" \n}';
		$scope.host = 'here you put your url';
		$scope.user_url = '';
	}
	
	$scope.init();	
	
    $scope.uploadFiles = function(file) {
    	
    	if (file != null){
    		
    		$scope.name = file.name;
    		
	        if (file.name.includes(".xml") || file.name.includes(".xbrl") ) {
	        	if (file.size <= 15000000) {
	        		
	 	            file.upload = Upload.upload({
		                url: $scope.host+'/upload',
		                data: {apifile: file}
		            });
	 	            
	 	            if (file.size > 10000000){
	 	            	$scope.f = '{\n  "msg": "Parse has already been started...", '+
	 	            			    '\n  "performance warning": "This file has a huge size ('+(file.size)+' bytes), its processing can take several (120 or +) seconds...", '+
	 	            			    '\n  "so": "We are working on performance issues"\n}';
	 	            }else if (file.size >  5000000){
	        			$scope.f = '{\n  "msg": "Parse has already been started...", '+
         			    			'\n  "performance warning": "This file has a big size ('+(file.size)+' bytes), its processing can take several (120 or +) seconds...", '+
         			    			'\n  "so": "We are working on performance issues"\n}';
	        		}else{
	        			$scope.f = '{\n  "msg": "Parse has already been started..." '+
			    					'\n}';
	        		}

	 	            file.upload.then(
		            	function success (response) {
		            		console.log(response.data);
		            		$scope.f = JSON.stringify(response.data, undefined, 4);
		            		$timeout(function () {console.log(response.status);});
		            	}, 
		            	function unsuccess (response) {
		            		console.log("response is a error: "+response.data);
		            		console.log("response.status: "+response.status);
		            		if (response.status == 500)
		            			$scope.f = '{\n "msg": "Sorry for this error! Possible reasons"' +
		            					'\n "[1]": "This api-rest version just parses xBRL-XML INSTANCE file from SEC standard",' +
		            					'\n "[2]": "There is some invalid json character into XBRL-XML document (please! send us this file, to find it out)", '+
		            					'\n "[3]":  "Json-based response string was malformed by tool (please! inform us)", '+
		            					'\n "[4]":  "This XBRL-XML file is malformed:", '+
		            					'\n "[4.1]": "The XML prolog is mandatory in file (e.g. <?xml version="1.0" encoding="UTF-8"?>)", '+
		            					'\n "[4.2]": "The entity \"nbsp\" can have been referenced into XBRL-XML file, but not declared. if so, you must declare it into file (e.g. <!DOCTYPE inline_dtd[<!ENTITY nbsp "&#160;">]>)" \n}' ;
		            		},
		            	function (evt) {
		            		file.progress = Math.min(100, parseInt(100.0 * 
		                                         evt.loaded / evt.total));
		            });
	        	}else{
	        		console.log("For while, max size per file is 15mb.");
	    			$scope.f = '{\n  "msg": "Sorry! For while, max size per file is 15mb" \n}';
	        	}
	        }else{ // if is not xml or xbrl file
	        	console.log("file must be in XML or XBRL format");
	        	$scope.f = '{\n "msg": "Sorry! This file must be in XML or XBRL format" \n}';
	        }
	       } // if not null
    	}

    
    $scope.sendUrl = function() {
    	    	
    	if ($scope.user_url.includes("http://") || $scope.user_url.includes("https://")){
	    
    		if ($scope.user_url.includes(".xml") || $scope.user_url.includes(".xbrl")){
    			
    			var temp = $scope.user_url.split("/");
    			$scope.name = temp[temp.length-1];
	    		
    			$scope.f = '{\n  "msg": "Parse has already been started...", '+
	    					'\n  "performance warning": "XBRL file with 3+ MB usually takes some seconds to be processed", '+
	    					'\n  "so": "We are working on performance issues"\n}';
		    
	    		$http({
					method:'POST', 
					url: $scope.host+'/upload-uri',
					data: $scope.user_url
				})
				.then(function success(response){
					$scope.clientes = response.data;
					$scope.f = JSON.stringify(response.data, undefined, 4);
				},function unsuccess(response){
					console.log(response);
					$scope.f = '{\n "msg": "sorry! there was some error in server" \n}';
				});
	    	
    		}else{
	    		$scope.f = '{\n "msg": "This URL must contain a XML or XBRL file", '+
	    				'\n  "example": "https://www.sec.gov/Archives/edgar/data/1169567/000116956714000019/oxfo-20140930.xml" \n}';
	    	}
    		
    	}else{
    		$scope.f = '{\n  "msg": "Your URL is not valid/wellformed", '+
    					'\n  "[1]": "There is no "http://" or "https://", ' +
    					'\n  "example": "https://www.sec.gov/Archives/edgar/data/1169567/000116956714000019/oxfo-20140930.xml" \n}';
    	}
    }
    

}]);
