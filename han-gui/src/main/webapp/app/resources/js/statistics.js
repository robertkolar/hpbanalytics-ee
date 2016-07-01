/**
 * Created by robertk on 21.9.2015.
 */
var HpbChart = (function() {
    var my = {};
    my.createC1 = function(d1, d2) {
        return new Highcharts.StockChart({
            chart: {
                type: 'column',
                renderTo: 'hpb_c1'
            },
            title: {
                text: 'Number Opened/Closed'
            },
            series: [{
                name: 'Opened',
                data: d1
            }, {
                name: 'Closed',
                data: d2
            }]
        });
    };
    my.createC2 = function(d1, d2) {
        return new Highcharts.StockChart({
            chart: {
                type: 'column',
                renderTo: 'hpb_c2'
            },
            title: {
                text: 'Number Winners/Losers'
            },
            series: [{
                name: 'Winners',
                color: 'green',
                data: d1
            }, {
                name: 'Losers',
                color: 'red',
                data: d2
            }]
        });
    };
    my.createC3 = function(d1, d2) {
        return new Highcharts.StockChart({
            chart: {
                type: 'column',
                renderTo: 'hpb_c3'
            },
            title: {
                text: 'Max Winner/Loser'
            },
            series: [{
                name: 'MaxWinner',
                color: 'green',
                data: d1,
                tooltip: {
                    valueDecimals: 2
                }
            }, {
                name: 'MaxLoser',
                color: 'red',
                data: d2,
                tooltip: {
                    valueDecimals: 2
                }
            }]
        });
    };
    my.createC4 = function(d1, d2) {
        return new Highcharts.StockChart({
            chart: {
                type: 'column',
                renderTo: 'hpb_c4'
            },
            title: {
                text: 'Winners Profit/Losers Loss'
            },
            series: [{
                name: 'Profit',
                color: 'green',
                data: d1,
                tooltip: {
                    valueDecimals: 2
                }
            }, {
                name: 'Loss',
                color: 'red',
                data: d2,
                tooltip: {
                    valueDecimals: 2
                }
            }]
        });
    };
    my.createC5 = function(d1) {
        return new Highcharts.StockChart({
            chart: {
                type: 'column',
                renderTo: 'hpb_c5'
            },
            title: {
                text: 'Profit Loss'
            },
            series: [{
                name: 'PL',
                data: d1,
                tooltip: {
                    valueDecimals: 2
                }
            }]
        });
    };
    my.createC6 = function(d1) {
        return new Highcharts.StockChart({
            chart: {
                renderTo: 'hpb_c6'
            },
            title: {
                text: 'Cumulative PL'
            },
            series: [{
                name: 'CumulPL',
                data: d1,
                tooltip: {
                    valueDecimals: 2
                }
            }]
        });
    };
    return my;
}());