define([ 'angular', './sample-module' ], function(angular, controllers) {
	'use strict';

	// Controller definition
	controllers.controller('airqmainCtrl', [ '$scope', '$http', '$state', '$log', 'PredixAssetService', 'PredixViewService', 'CalculationOneService', 'CalculationService', '$interval',
			function($scope, $http, $state, $log, PredixAssetService, PredixViewService, CalculationOneService, CalculationService, $interval) {
				CalculationService.getNukes().then(function(data) {
					// console.log(JSON.stringify(data));
				});

				var charts = [];
				var promise = null;

				$scope.sub_graph = function() {
					$state.go('detilpara');
				};

				$scope.sub_para = function() {
					$state.go('detail_parameter');
				};

				var getTocke = function() {
					$http({
						method : 'GET',
						url : 'https://3ac499f6-d00a-4f3f-a15f-eec715933a48.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token?grant_type=client_credentials',
						headers : {
							'Authorization' : 'Basic ZWhzLWNsaWVudC1pZDpjbGllbnQ='
						}
					}).success(function(data) {
						$scope.acc_token = data.access_token;
						// console.log($scope.acc_token);
						// startRequesting($scope.acc_token);

					}).error(function(data) {
						// console.log(data);
					});
				};

				var getValues = function(acc_token) {
					$http({
						// headers : {
						// "Authorization" : 'bearer ' + acc_token
						// },
						method : 'GET',
						// http://localhost:9092/services/geography/generated
						// url :
						// 'https://ehs-rmd-datasource.run.aws-usw02-pr.ice.predix.io/services/geography/last20Sec?asset_name=Soltech-Machine',
						url : 'https://ehs-rmd-datasource.run.aws-usw02-pr.ice.predix.io/services/geography/generatedArea24Hrs?asset_name=Soltech-Machine',
					}).success(function(data) {
						$scope.contlist = data;
						// console.log(data);
						calculateAQI(data.body);
						startRequesting();
					});
				}

				var getValues20Sec = function(acc_token) {
					$http({
						// headers : {
						// "Authorization" : 'bearer ' + acc_token
						// },
						method : 'GET',
						// http://localhost:9092/services/geography/generated
						// url :
						// 'https://ehs-rmd-datasource.run.aws-usw02-pr.ice.predix.io/services/geography/last20Sec?asset_name=Soltech-Machine',
						url : 'https://ehs-rmd-datasource.run.aws-usw02-pr.ice.predix.io/services/geography/generatedArea20Sec?asset_name=Soltech-Machine',
					}).success(function(data) {
						$scope.contlist = data;
						// console.log(data);
						calculateAQI(data.body);

					});
				}

				var startRequesting = function(token) {
					promise = $interval(function() {
						getValues20Sec(token);
					}, 10000);
				};
				var createChart = function(getMaxAqiValuesArray, dates) {

					$('.CampaignPercent').each(function() {
						var chart = new Highcharts.Chart({
							type : 'spline',
							animation : Highcharts.svg, // don't animate in old
														// IE
							marginRight : 10,
							chart : {
								renderTo : this,
								spacingTop : 10,
								width : 350
							},
							title : {
								text : ''
							},
							tooltip : {
								formatter : function() {
									return '<b>' + this.series.name + '</b><br/>' + Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' + Highcharts.numberFormat(this.y, 2);
								}
							},
							exporting : {
								enabled : false
							},

							plotOptions : {
								series : {
									color : '#8BBE3D',
									lineWidth : 1,
									marker : {
										enabled : false,
									},
								}
							},
							credits : {
								enabled : false
							},

							xAxis : {
								title : {
									text : 'Time'
								},
								categories : dates
							},

							yAxis : {
								title : {
									text : 'Air value'
								},
							},

							series : [ {
								name : 'AQI',
								data : getMaxAqiValuesArray
							} ]
						});
						charts.push(chart);
					});
				};

				function prettyDate(date) {
					var months = [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ];
					return months[date.getUTCMonth()] + ' ' + date.getUTCDate();
				}
				var applayValue = function(getMaxAqiValuesArray, dataPoints) {

					var dates = [];
					for (var i = 0; i < dataPoints.length; i++) {
						var date = new Date(dataPoints[i]);
						// console.log(date);
						var dateString = prettyDate(date);
						dateString = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
						dates.push(dateString);
					}
					// console.log(chart);
					// createChart(getMaxAqiValuesArray, dates);

					if (!charts.length) {
						createChart(getMaxAqiValuesArray, dates);
					} else {
						for (var i = 0; i < charts.length; i++) {
							var chart = charts[i];
							for (var j = 0; j < getMaxAqiValuesArray.length - 5; j++) {
								var x = dates[j];
								var y = getMaxAqiValuesArray[j];

								chart.series[0].addPoint([ x, y ], true, true);
							}
							// chart.series[0].update({
							// data : getMaxAqiValuesArray
							// });
							// chart.xAxis[0].setCategories(dates)
						}

					}

				};

				var calculateAQI = function(data) {
					// console.log(JSON.stringify(data));
					var NO2 = data.tags[0].results[0].attributes.NO2;
					var SO2 = data.tags[0].results[0].attributes.SO2;
					var PM2_5 = data.tags[0].results[0].attributes.PM2_5;

					var PB = data.tags[0].results[0].attributes.PB;
					var O3 = data.tags[0].results[0].attributes.O3;
					var CO2 = data.tags[0].results[0].attributes.CO2;
					var NH3 = data.tags[0].results[0].attributes.NH3;
					var PM10 = data.tags[0].results[0].attributes.PM10;

					var values = data.tags[0].results[0].values;
					var timeStapms = [];

					for (var i = 0; i < values.length; i++) {
						timeStapms.push(values[i][0]);
					}

					var aquiValuesNO2 = getAquiValue(NO2, 'NO2');
					var aquiValuesSO2 = getAquiValue(SO2, 'SO2');
					var aquiValuesPM2_5 = getAquiValue(PM2_5, 'PM2_5');
					var aquiValuesPB = getAquiValue(PB, 'PB');
					var aquiValuesO3 = getAquiValue(O3, 'O3');
					var aquiValuesCO2 = getAquiValue(CO2, 'CO2');
					var aquiValuesNH3 = getAquiValue(NH3, 'NH3');
					var aquiValuesPM10 = getAquiValue(PM10, 'PM10');

					var aquiValues = [];
					aquiValues.push(aquiValuesNO2);
					aquiValues.push(aquiValuesSO2);
					aquiValues.push(aquiValuesPM2_5);
					aquiValues.push(aquiValuesPB);
					aquiValues.push(aquiValuesO3);
					aquiValues.push(aquiValuesCO2);
					aquiValues.push(aquiValuesNH3);
					aquiValues.push(aquiValuesPM10);
					// console.log(aquiValues);

					var maxAqiValuesArray = getMaxAqiValues(aquiValues);
					var pickVal = 6;
					var length = maxAqiValuesArray.length;
					var interval = parseFloat(length) / parseFloat(pickVal);

					var filterdAqiValues = [];
					var filterdTimeStapms = [];
					for (var i = 0; i < length; i += interval) {
						filterdAqiValues.push(maxAqiValuesArray[Math.floor(i)]);
						filterdTimeStapms.push(timeStapms[Math.floor(i)]);
					}
					// filterdAqiValues = filterdAqiValues.splice(0, 6);
					// filterdTimeStapms = filterdTimeStapms.splice(0, 6);

					// console.log(maxAqiValuesArray);
					// console.log(filterdAqiValues);
					// console.log(filterdTimeStapms);
					applayValue(filterdAqiValues, filterdTimeStapms);
					// console.log("\n");
				};

				var getMaxAqiValues = function(aquiValues) {
					var getMaxAqiValuesArray = [];
					var limit = aquiValues.length;
					// console.log(aquiValues);

					var aquiValuesTansose = [ [] ];

					for (var i = 0; i < limit; i++) {
						for (var j = 0; j < aquiValues[i].length; j++) {
							if (!aquiValuesTansose[j]) {
								aquiValuesTansose[j] = [];
							}
							aquiValuesTansose[j][i] = aquiValues[i][j];
						}
					}

					// console.log(aquiValuesTansose);
					for (var i = 0; i < aquiValuesTansose.length; i++) {
						getMaxAqiValuesArray.push(Math.max.apply(null, aquiValuesTansose[i]));
					}

					return getMaxAqiValuesArray;
				};

				var getAquiValue = function(paramValues, param) {
					var aqiValues = [];
					for (var i = 0; i < paramValues.length; i++) {
						var aqiParametersNO2 = AQI.getAqiParameters(paramValues[i], param);
						var auiNO2 = AQI.getAqui(aqiParametersNO2);
						aqiValues.push(auiNO2);
					}
					return aqiValues;
				};
				var AQI = {
					getAqiParameters : function(val, parameter) {
						return CalculationOneService.getAqiValue(val, parameter, "Soltech-Machine");
					},
					getAqui : function(p) {
						// console.log(JSON.stringify(p));

						var IHI = parseFloat(p.i_hi);
						var ILO = parseFloat(p.i_lo);
						var BHI = parseFloat(p.b_hi);
						var BLO = parseFloat(p.b_lo);
						var Cp = parseFloat(p.avg);

						// Ip= [{(IHI - ILO)/ (BHI -BLO)} * (Cp-BLO)] + ILO
						var Lp = (((IHI - ILO) / (BHI - BLO)) * (Cp - BLO)) + ILO;
						return Math.round(Lp * 100) / 100;
					}
				};

				// startRequesting($scope.acc_token);

				getValues();
				$scope.stop = function() {
					$interval.cancel(promise);

				};
				$scope.$on('$destroy', function() {
					$scope.stop();
				});

			} ]);

});