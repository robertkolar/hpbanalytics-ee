/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.view.report.grid.ExecutionsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'executions-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'Report.view.report.ReportController'
    ],
    bind: '{executions}',
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
            tpl: '{reportId}/{id}',
            align: 'right'
        }, {
            text: 'Fill Date',
            width: 180,
            dataIndex: 'fillDate',
            xtype: 'datecolumn',
            format: 'm/d/Y H:i:s.u'
        }, {
            text: 'Origin',
            width: 100,
            dataIndex: 'origin'
        }, {
            text: 'RefID',
            width: 100,
            dataIndex: 'referenceId',
            align: 'right'
        }, {
            text: 'Action',
            width: 60,
            dataIndex: 'action',
            renderer: function(val, metadata, record) {
                metadata.style = (val == 'BUY' ? 'color: blue;' : 'color: brown;');
                return val;
            }
        }, {
            text: 'Qnt',
            width: 80,
            dataIndex: 'quantity',
            align: 'right'
        }, {
            text: 'Sec',
            width: 60,
            dataIndex: 'secType'
        }, {
            text: 'Undl',
            width: 80,
            dataIndex: 'underlying'
        }, {
            text: 'Cur',
            width: 60,
            dataIndex: 'currency'
        }, {
            text: 'Symbol',
            width: 180,
            dataIndex: 'symbol'
        }, {
            xtype: 'numbercolumn',
            format: '0.00',
            text: 'Fill',
            width: 80,
            dataIndex: 'fillPrice',
            align: 'right'
        }, {
            text: 'Received Date',
            width: 180,
            dataIndex: 'receivedDate',
            xtype: 'datecolumn',
            format: 'm/d/Y H:i:s.u'
        }, {
            text: 'Comment',
            flex: 1,
            dataIndex: 'comment'
        }]
    },
    dockedItems: [{
        xtype: 'pagingtoolbar',
        bind: '{executions}',
        dock: 'bottom',
        displayInfo: true
    }]
});