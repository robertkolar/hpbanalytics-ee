/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.model.report.SplitExecution', {
    extend: 'Report.model.report.Base',

    fields: [
        'splitQuantity',
        'currentPosition',
        {name: 'fillDate', type: 'date', dateFormat: 'time'},
        {name: 'tradeId', type: 'string', reference: {type: 'Trade', inverse: 'splitExecutions'}},
        'executionDisplay'
    ]
});