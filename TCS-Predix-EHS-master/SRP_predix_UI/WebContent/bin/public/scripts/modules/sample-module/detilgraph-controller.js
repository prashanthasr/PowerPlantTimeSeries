define([ 'angular', './sample-module' ], function(angular, controllers) {
	'use strict';

	// Controller definition
	controllers.controller('detilgraphCtrl', [
			'$scope',
			'$http',
			'$log',
			'PredixAssetService',
			'PredixViewService',
			'CalculationOneService',
			'CalculationService',
			'$interval',
			function($scope, $http, $log, PredixAssetService, PredixViewService, CalculationOneService, CalculationService, $interval) {

				var pickVal = 20;
				var addCount = 1;
				var promise;
				var intervalMs = 4000;
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
					}, intervalMs);
				};
				var charts = [];
				var chartsTrends = [];
				var createChart = function(getMaxAqiValuesArray, dates) {
					charts = [];
					Highcharts.setOptions({
						global : {
							useUTC : false
						}
					});
					$('.CampaignPercent').each(function() {

						var chartPrediction = new Highcharts.Chart({
							type : 'spline',
							animation : Highcharts.svg,
							marginRight : 10,
							chart : {
								renderTo : this,
								spacingTop : 10
							},
							title : {
								text : ''
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

						charts.push(chartPrediction)
					});
				};

				var createChartTrends = function(array, dates) {
					chartsTrends = [];
					Highcharts.setOptions({
						global : {
							useUTC : false
						}
					});
					$('.CampaignPercent1').each(function() {
						var chart = new Highcharts.Chart({
							type : 'spline',
							chart : {
								renderTo : this,
								spacingTop : 10
							},
							title : {
								text : ''
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
							credits : {},

							xAxis : {
								title : {
									text : 'Time'
								},
								categories : dates
							},

							yAxis : {
								title : {
									text : 'Air value'
								}
							},

							series : [ {
								name : 'NO2',
								color : '#8BBE3D',
								data : array[0]
							}, {
								name : 'SO2',
								color : '#8BB23D',
								data : array[1]
							}, {
								name : 'PM2_5',
								color : '#80BE3D',
								data : array[2]
							}, {
								name : 'PB',
								color : '#8BBEED',
								data : array[3]
							}, {
								name : 'O3',
								color : '#FFBE3D',
								data : array[4]
							}, {
								name : 'CO2',
								color : '#8BBFFD',
								data : array[5]
							}, {
								name : 'NH3',
								color : '#8BFF3D',
								data : array[6]
							}, {
								name : 'PM10',
								color : '#8BBE3D',
								data : array[7]
							} ]
						});
						chartsTrends.push(chart);
					});
				};

				function prettyDate(date) {
					var months = [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ];
					return months[date.getUTCMonth()] + ' ' + date.getUTCDate();
				}
				var applayValue = function(array, dataPoints) {

					var dates = [];
					for (var i = 0; i < dataPoints.length; i++) {
						var date = new Date(dataPoints[i]);
						var dateString = prettyDate(date);
						dateString = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
						dates.push(dateString);
					}

					if (charts.length == 0) {
						createChart(array, dates);
					} else {
						for (var i = 0; i < charts.length; i++) {
							var chart = charts[i];
							// console.log(dates.length);
							for (var j = 0; j < addCount; j++) {
								var x = dates[j];
								var y = array[j];

								chart.series[0].addPoint([ x, y ], true, true);
							}

						}

					}

				};

				var applayValueTrends = function(dataPoints, aquiValuesNO2, aquiValuesSO2, aquiValuesPM2_5, aquiValuesPB, aquiValuesO3, aquiValuesCO2, aquiValuesNH3, aquiValuesPM10) {

					var dates = [];
					var array = [];
					for (var i = 0; i < dataPoints.length; i++) {
						var date = new Date(dataPoints[i]);
						// var dateString = prettyDate(date);
						var hours = date.getHours();
						var minutes = date.getMinutes();
						var seconds = date.getSeconds();

						if (hours == 0) {
							hours = '00';
						}
						if (minutes == 0) {
							minutes = '00';
						}
						if (seconds == 0) {
							seconds = '00';
						}

						var dateString = hours + ":" + minutes + ":" + seconds;
						dates.push(dateString);
					}
					// console.log(dates);
					array.push(aquiValuesNO2);
					array.push(aquiValuesSO2);
					array.push(aquiValuesPM2_5);
					array.push(aquiValuesPB);
					array.push(aquiValuesO3);
					array.push(aquiValuesCO2);
					array.push(aquiValuesNH3);
					array.push(aquiValuesPM10);

					// console.log(array);
					if (chartsTrends.length == 0) {
						createChartTrends(array, dates);
					} else {
						for (var i = 0; i < chartsTrends.length; i++) {
							var chart = chartsTrends[i];
							updateValues(chart.series[0], aquiValuesNO2, dates);
							updateValues(chart.series[1], aquiValuesSO2, dates);
							updateValues(chart.series[2], aquiValuesPM2_5, dates);
							updateValues(chart.series[3], aquiValuesPB, dates);
							updateValues(chart.series[4], aquiValuesO3, dates);
							updateValues(chart.series[5], aquiValuesCO2, dates);
							updateValues(chart.series[6], aquiValuesNH3, dates);
							updateValuesAnimate(chart.series[7], aquiValuesPM10, dates);
						}

					}

				};
				var updateValues = function(series, array, dates) {
					for (var j = 0; j < addCount; j++) {
						var x = dates[j];
						var y = array[j];
						series.addPoint([ x, y ], false, true);
					}

				};
				var updateValuesAnimate = function(series, array, dates) {
					for (var j = 0; j < addCount; j++) {
						var x = dates[j];
						var y = array[j];
						series.addPoint([ x, y ], true, true);
					}

				};

				var calculateAQI = function(data) {
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

					var maxAqiValuesArray = getMaxAqiValues(aquiValues);

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
					applayValueTrends(filterdTimeStapms, aquiValuesNO2.splice(0, filterdTimeStapms.length), aquiValuesSO2.splice(0, filterdTimeStapms.length), aquiValuesPM2_5.splice(0,
							filterdTimeStapms.length), aquiValuesPB.splice(0, filterdTimeStapms.length), aquiValuesO3.splice(0, filterdTimeStapms.length), aquiValuesCO2.splice(0,
							filterdTimeStapms.length), aquiValuesNH3.splice(0, filterdTimeStapms.length), aquiValuesPM10.splice(0, filterdTimeStapms.length));
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

				// console.log("i am in detilgraphCtrlss control");
				var redrawChart = function() {
					$('.CampaignPercent1').hide();
					$('.CampaignPercent').hide();
					charts = [];
					chartsTrends = [];
					$scope.stop();
					getValues();
					$('.CampaignPercent1').fadeIn();
					$('.CampaignPercent').fadeIn();
				};
				$scope.graphFunction = function(value) {
					if (value === "x1") {
						redrawChart();
					}
					if (value === "x2") {
						redrawChart();
					}
					if (value === "x3") {
						redrawChart();
					}
				};

			} ]);
});