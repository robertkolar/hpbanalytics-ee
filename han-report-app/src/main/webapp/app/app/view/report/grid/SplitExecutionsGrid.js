/**
 * Created by robertk on 19.10.2015.
 */
Ext.define('Report.view.report.grid.SplitExecutionsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'han-report-splitexecutions-grid',
    requires: [
        'Ext.grid.column.Date'
    ],
    viewConfig: {
        stripeRows: true
    },
    columns: {
        defaults: {
            style: 'background-color: #157fcc; color: black;'
        },
        items: [{
            xtype: 'templatecolumn',
            text: 'ID',
            width: 80,
            tpl: '{tradeId}/{id}',
            align: 'right'
        }, {
            text: 'Split Q',
            width: 80,
            dataIndex: 'splitQuantity',
            align: 'right'
        }, {
            text: 'Cur Pos',
            width: 80,
            dataIndex: 'currentPosition',
            align: 'right'
        }, {
            text: 'Fill Date',
            width: 180,
            dataIndex: 'fillDate',
            xtype: 'datecolumn',
            format: 'm/d/Y H:i:s.u'
        }, {
            text: 'Execution',
            flex: 1,
            dataIndex: 'executionDisplay'
        }]
    }
});