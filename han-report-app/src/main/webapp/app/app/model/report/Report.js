/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.model.report.Report', {
    extend: 'Report.model.report.Base',

    fields: [
        'origin',
        'name',
        'numExecutions',
        'numTrades',
        'numOpenTrades',
        'numUnderlyings',
        'numOpenUnderlyings',
        {name: 'firstExecutionDate', type: 'date', dateFormat: 'time'},
        {name: 'lastExecutionDate', type: 'date', dateFormat: 'time'}
    ]
});