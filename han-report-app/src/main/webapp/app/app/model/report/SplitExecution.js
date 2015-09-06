/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.model.report.SplitExecution', {
    extend: 'Report.model.report.Base',

    fields: [
        'splitQuantity',
        'currentPosition',
        {name: 'dateFilled', type: 'date', dateFormat: 'time'},
        'executionId',
        {name: 'tradeId', type: 'string', reference: {type: 'Trade', inverse: 'splitExecutions'}}
    ]
});