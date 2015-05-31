$(document).ready(function() {
    urlPrefix = "http://" + getUrlBase() + "/rest";
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });
    createStatisticsCharts();
});

function getUrlBase() {
    return window.location.host + window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
}

function createStatisticsCharts() {
    $.getJSON(urlPrefix + '/report/statistics', function(statData) {
        if (statData == null) {
            return;
        }
        var numberOpened = [];
        var numberClosed = [];
        
        var numberWinners = [];
        var numberLosers = [];
        
        var maxWinner = [];
        var maxLoser = [];
        
        var winnersProfit = [];
        var losersLoss = [];
        
        var profitLoss = [];
        var cumulativePl = [];

        for (i = 0; i < statData.length; i++) {
            numberOpened.push([
                statData[i].timeInMillis,
                statData[i].numOpened
            ]);
            numberClosed.push([
                statData[i].timeInMillis,
                statData[i].numClosed
            ]);
            
            numberWinners.push([
                statData[i].timeInMillis,
                statData[i].numWinners
            ]);
            numberLosers.push([
                statData[i].timeInMillis,
                statData[i].numLosers
            ]);
            
            maxWinner.push([
                statData[i].timeInMillis,
                statData[i].maxWinner
            ]);
            maxLoser.push([
                statData[i].timeInMillis,
                statData[i].maxLoser
            ]);
            
            winnersProfit.push([
                statData[i].timeInMillis,
                statData[i].winnersProfit
            ]);
            losersLoss.push([
                statData[i].timeInMillis,
                statData[i].losersLoss
            ]);
            
            profitLoss.push([
                statData[i].timeInMillis,
                statData[i].profitLoss
            ]);
            cumulativePl.push([
                statData[i].timeInMillis,
                statData[i].cumulProfitLoss
            ]);
        }
        
        $('#c1').highcharts('StockChart', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'Number Opened/Closed'
            },
            series: [{
                name: 'Opened',
                data: numberOpened
            },
            {
                name: 'Closed',
                data: numberClosed
            }]
        });
        $('#c2').highcharts('StockChart', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'Number Winners/Losers'
            },
            series: [{
                name: 'Winners',
                color: 'green',
                data: numberWinners
            },
            {
                name: 'Losers',
                color: 'red',
                data: numberLosers
            }]
        });
        $('#c3').highcharts('StockChart', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'Max Winner/Loser'
            },
            series: [{
                name: 'MaxWinner',
                color: 'green',
                data: maxWinner,
                tooltip: {
                    valueDecimals: 2
                }
            },
            {
                name: 'MaxLoser',
                color: 'red',
                data: maxLoser,
                tooltip: {
                    valueDecimals: 2
                }
            }]
        });
        $('#c4').highcharts('StockChart', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'Winners Profit/Losers Loss'
            },
            series: [{
                name: 'Profit',
                color: 'green',
                data: winnersProfit,
                tooltip: {
                    valueDecimals: 2
                }
            },
            {
                name: 'Loss',
                color: 'red',
                data: losersLoss,
                tooltip: {
                    valueDecimals: 2
                }
            }]
        });
        $('#c5').highcharts('StockChart', {
            chart: {
                type: 'column'
            },
            title: {
                text: 'Profit Loss'
            },
            series: [{
                name: 'PL',
                data: profitLoss,
                tooltip: {
                    valueDecimals: 2
                }
            }]
        });
        $('#c6').highcharts('StockChart', {
            title: {
                text: 'Cumulative PL'
            },
            series: [{
                name: 'CumulPL',
                data: cumulativePl,
                tooltip: {
                    valueDecimals: 2
                }
            }]
        });
    });
}