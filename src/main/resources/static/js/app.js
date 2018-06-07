var app = angular.module("app", ['ngFileUpload']);

app.controller('xbrlController', ['$http','$scope', 'Upload', '$timeout', function ($http, $scope, Upload, $timeout) {
	
	$scope.init = function(){
		$scope.host = 'https://xbrlframework.herokuapp.com';
		//$scope.host = 'http://localhost:8080';
		$scope.user_url = '';
		$scope.loadStatus = '	';		
		$scope.f = '{\n "report" : {\n   "load a XBRL instance file" \n } \n}';
	}
	
	$scope.init();	
	
	$scope.buildindString = function(){
		
		$scope.docType = '"documentType" : "http://www.xbrl.org/CR/2017-05-02/xbrl-json",\n';
		$scope.prefixes = '"loading..."\n';
		$scope.dts = '"loading..."\n';
		$scope.facts = '"loading..."\n';
		
		$scope.f = '{\n' 
			+'  "report" : {\n'
			+'    '+$scope.docType
			+'    "prefix" : {\n'
			+'      '+$scope.prefixes
			+'    }\n'
			+'    "dts": {\n'
			+'      '+$scope.dts
			+'    }\n'
			+'    "fact" : [\n'
			+'      '+$scope.facts
			+'    ]\n'
			+'  }\n'
		+'}';
	}

	$scope.uploadFiles = function(file) {
    	
    	if (file != null){
    		
    		$scope.buildingString();
    		
	        if (file.name.includes(".xml") || file.name.includes(".xbrl") ) {
	        	if (file.size <= 15000000) {
	        		        		
	 	            file.upload = Upload.upload({
		                url: $scope.host+'/upload',
		                data: {apifile: file}
		            });      
	 	            
	 	            file.upload.then(
		            	function success (response) {
		            		console.log(response.data);
		            		$scope.f = JSON.stringify(response.data, undefined, 4);
		            		$timeout(function () {console.log(response.status);});
		            		$scope.loadStatus = '';
		            	}, 
		            	function unsuccess (response) {
		            		console.log("response is a error: "+response.data);
		            		console.log("response.status: "+response.status);
		            		$scope.loadStatus = '';
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
    			
    			$scope.buildindString();
    			
    			var temp = $scope.user_url.split("/");
    			$scope.name = temp[temp.length-1];
	    		
	    		$http({
					method:'POST', 
					url: $scope.host+'/upload-uri',
					data: $scope.user_url
				})
				.then(function success(response){
					$scope.clientes = response.data;
					$scope.f = JSON.stringify(response.data, undefined, 4);
					$scope.loadStatus = '';
				},function unsuccess(response){
					console.log(response);
					$scope.f = '{\n "msg": "sorry! there was some error in server" \n}';
					$scope.loadStatus = '';
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
