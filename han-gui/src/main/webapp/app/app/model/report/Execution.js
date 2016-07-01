/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.model.report.Execution', {
    extend: 'Report.model.report.Base',

    fields: [
        {name: 'receivedDate', type: 'date', dateFormat: 'time'},
        'comment',
        'origin',
        'referenceId',
        'action',
        'quantity',
        'symbol',
        'underlying',
        'currency',
        'secType',
        {name: 'fillDate', type: 'date', dateFormat: 'time'},
        'fillPrice',
        'reportId'
    ]
});