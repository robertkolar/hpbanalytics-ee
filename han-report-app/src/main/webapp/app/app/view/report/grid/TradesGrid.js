/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.view.report.grid.TradesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'trades-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'Report.view.report.ReportController'
    ],
    bind: '{trades}',
    title: 'Trades',
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
            text: 'Type',
            width: 80,
            dataIndex: 'type',
            renderer: function(val, metadata, record) {
                metadata.style = (val == 'LONG' ? 'color: blue;' : 'color: brown;');
                return val;
            }
        }, {
            text: 'Qnt',
            width: 80,
            dataIndex: 'cumulativeQuantity',
            align: 'right'
        }, {
            text: 'Pos',
            width: 80,
            dataIndex: 'openPosition',
            align: 'right'
        }, {
            text: 'Sec',
            width: 80,
            dataIndex: 'secType'
        }, {
            text: 'Cur',
            width: 80,
            dataIndex: 'currency'
        }, {
            text: 'Symbol',
            width: 180,
            dataIndex: 'symbol'
        }, {
            type: 'numbercolumn',
            format: '0.00',
            text: 'Open',
            width: 80,
            dataIndex: 'avgOpenPrice',
            align: 'right'
        }, {
            text: 'Open Date',
            width: 180,
            dataIndex: 'dateOpened',
            xtype: 'datecolumn',
            format: 'm/d/Y H:i:s.u'
        }, {
            xtype: 'numbercolumn',
            format: '0.00',
            text: 'Close',
            width: 80,
            dataIndex: 'avgClosePrice',
            align: 'right'
        }, {
            text: 'Close Date',
            width: 180,
            dataIndex: 'dateClosed',
            xtype: 'datecolumn',
            format: 'm/d/Y H:i:s.u'
        }, {
            text: 'Duration',
            width: 140,
            dataIndex: 'duration'
        }, {
            text: 'P/L',
            width: 100,
            dataIndex: 'profitLoss',
            align: 'right',
            renderer: function(val, metadata, record) {
                metadata.style = val < 0 ? 'color: red;' : 'color: green;';
                return Ext.util.Format.number(val, '0.00');
            }
        }, {
            text: 'Status',
            width: 60,
            dataIndex: 'status',
            renderer: function(val, metadata, record) {
                metadata.style = 'color: white; ' + (val == 'OPEN' ? 'background-color: green;' : 'background-color: brown;');
                return val.toLowerCase();
            }
        }, {
            flex: 1
        }]
    },
    dockedItems: [{
        xtype: 'pagingtoolbar',
        bind: '{trades}',
        dock: 'bottom',
        displayInfo: true
    }]
});