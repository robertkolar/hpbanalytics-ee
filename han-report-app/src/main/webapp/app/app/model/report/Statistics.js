/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.model.report.Statistics', {
    extend: 'Report.model.report.Base',

    fields: [
        {name: 'periodDate', type: 'date', dateFormat: 'time'},
        'numOpened',
        'numClosed',
        'numWinners',
        'numLosers',
        'maxWinner',
        'maxLoser',
        'winnersProfit',
        'losersLoss',
        'profitLoss',
        'cumulProfitLoss'
    ]
});