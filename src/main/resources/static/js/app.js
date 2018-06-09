var app = angular.module("app", ['ngFileUpload']);

app.controller('xbrlController', ['$http','$scope', 'Upload', '$timeout', function ($http, $scope, Upload, $timeout) {
	
	$scope.init = function(){
		$scope.host = 'http://localhost:8080' || 'https://xbrlframework.herokuapp.com';
		$scope.user_url = '';
		$scope.loadStatus = '	';		
		$scope.f = '{\n "report" : {\n   "load a XBRL instance file" \n } \n}';
		
		$scope.docType = '"documentType" : "http://www.xbrl.org/CR/2017-05-02/xbrl-json",\n';
		$scope.prefixes = '"loading..."';
		$scope.dts = '"loading..."';
		$scope.facts = '"loading..."';
	}
	
	$scope.init();	
	
	$scope.initString = function(docType,prefixes,dts,facts){
		
		$scope.f = '{\n' 
			+'  "report" : {\n'
			+'    '+docType
			+'    "prefix" : '+prefixes
			+',\n    "dts": '+dts
			+',\n    "fact" : '+facts
			+'\n  }\n'
		+'}';
	}

	$scope.uploadFiles = function(file) {
    	
    	if (file != null){
    		    		
    		$scope.initString($scope.docType,$scope.prefixes,$scope.dts,$scope.facts);
    		    		   		
	        if (file.name.includes(".xml") || file.name.includes(".xbrl") ) {
	        	if (file.size <= 15000000) {
	        		        		
	        		var prefixe = file;
	        		var dts = file;
	        		var facts = file;
	        		        		
	        		//prefixes
	        		prefixe.upload = Upload.upload({
		                url: $scope.host+'/prefixes-file',
		                data: {apifile: prefixe}
		            });      
	 	            
	        		prefixe.upload.then(
		            	function success (response) {
		            		console.log(response.data);
		            		$scope.prefixes = JSON.stringify(response.data, undefined, 4);
		            		$timeout(function () {console.log(response.status);});
		            		$scope.initString($scope.docType,$scope.prefixes,$scope.dts,$scope.facts);
		            	}, 
		            	function unsuccess (response) {
		            		console.log("response is a error: "+response.data);
		            		console.log("response.status: "+response.status);
	            			$scope.prefixes = '{ "there were problems in loading prefixes" }' ;
	            			$scope.initString($scope.docType,$scope.prefixes,$scope.dts,$scope.facts);
	            		});
	 	           
	 	           //dts
	        		dts.upload = Upload.upload({
		                url: $scope.host+'/dts-file',
		                data: {apifile: dts}
		            });      
	 	            
	        		dts.upload.then(
		            	function success (response) {
		            		console.log(response.data);
		            		$scope.dts = JSON.stringify(response.data, undefined, 4);
		            		$timeout(function () {console.log(response.status);});
		            		$scope.initString($scope.docType,$scope.prefixes,$scope.dts,$scope.facts);
		            	}, 
		            	function unsuccess (response) {
		            		console.log("response is a error: "+response.data);
		            		console.log("response.status: "+response.status);
		            		$scope.loadStatus = '';
	            			$scope.dts = '{ "there were problems in loading dts list" }' ;
	            			$scope.initString($scope.docType,$scope.prefixes,$scope.dts,$scope.facts);
		            	});
	 	           
	 	           
	 	           //facts
	        		facts.upload = Upload.upload({
		                url: $scope.host+'/facts-file',
		                data: {apifile: facts}
		            });      
	 	            
	        		facts.upload.then(
		            	function success (response) {
		            		console.log(response.data);
		            		$scope.facts = JSON.stringify(response.data, undefined, 4);
		            		$timeout(function () {console.log(response.status);});
		            		$scope.initString($scope.docType,$scope.prefixes,$scope.dts,$scope.facts);
		            	}, 
		            	function unsuccess (response) {
		            		console.log("response is a error: "+response);
		            		console.log("response.status: "+response.status);
		            		$scope.facts = '[{ "there were problems in loading the facts" }]' ;
	            			$scope.initString($scope.docType,$scope.prefixes,$scope.dts,$scope.facts);
		            			           
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
    			
    			$scope.initString();
    			
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
