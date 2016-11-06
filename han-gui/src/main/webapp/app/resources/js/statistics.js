/**
 * Created by robertk on 21.9.2015.
 */
var HpbChart = (function() {
    var my = {};
    my.createC1 = function(d1, d2) {
        return 'Number Opened/Closed';
    };
    my.createC2 = function(d1, d2) {
        return 'Number Winners/Losers';
    };
    my.createC3 = function(d1, d2) {
        return 'Max Winner/Loser';
    };
    my.createC4 = function(d1, d2) {
        return 'Winners Profit/Losers Loss';
    };
    my.createC5 = function(d1) {
        return 'Profit Loss';
    };
    my.createC6 = function(d1) {
        return 'Cumulative PL';
    };
    return my;
}());