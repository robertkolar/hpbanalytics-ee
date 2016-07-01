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
        {name: 'openDate', type: 'date', dateFormat: 'time'},
        'avgClosePrice',
        {name: 'closeDate', type: 'date', dateFormat: 'time'},
        'duration',
        'profitLoss',
        'reportId'
    ]
});