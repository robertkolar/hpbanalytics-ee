/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.model.report.Trade', {
    extend: 'Report.model.report.Base',

    fields: [
        'type',
        'symbol',
        'underlying',
        'currency',
        'secType',
        'cumulativeQuantity',
        'status',
        'openPosition',
        'avgOpenPrice',
        {name: 'dateOpened', type: 'date', dateFormat: 'time'},
        'avgClosePrice',
        {name: 'dateClosed', type: 'date', dateFormat: 'time'},
        'profitLoss',
        'reportId'
    ]
});