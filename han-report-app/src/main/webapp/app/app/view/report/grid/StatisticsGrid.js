/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.view.report.grid.StatisticsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'statistics-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'Report.view.report.ReportController'
    ],
    bind: '{statistics}',
    title: 'Statistics',
    viewConfig: {
        stripeRows: true
    },
    columns: {
        defaults: {
            style: 'background-color: #157fcc; color: black;'
        },
        items: [{
            text: '#',
            width: 60,
            dataIndex: 'id',
            align: 'right'
        }, {
            text: 'Period',
            width: 100,
            dataIndex: 'periodDate',
            xtype: 'datecolumn',
            format: 'm/d/Y'
        }, {
            text: '#Opn',
            width: 80,
            dataIndex: 'numOpened',
            align: 'right'
        }, {
            text: '#Cls',
            width: 80,
            dataIndex: 'numClosed',
            align: 'right'
        }, {
            text: '#Win',
            width: 80,
            dataIndex: 'numWinners',
            align: 'right'
        }, {
            text: '#Los',
            width: 80,
            dataIndex: 'numLosers',
            align: 'right'
        }, {
            xtype: 'numbercolumn',
            format: '0.00',
            text: 'Max W',
            width: 100,
            dataIndex: 'maxWinner',
            align: 'right'
        }, {
            xtype: 'numbercolumn',
            format: '0.00',
            text: 'Max L',
            width: 100,
            dataIndex: 'maxLoser',
            align: 'right'
        }, {
            xtype: 'numbercolumn',
            format: '0.00',
            text: 'W Profit',
            width: 100,
            dataIndex: 'winnersProfit',
            align: 'right'
        }, {
            xtype: 'numbercolumn',
            format: '0.00',
            text: 'L Loss',
            width: 100,
            dataIndex: 'losersLoss',
            align: 'right'
        }, {
            text: 'PL Period',
            width: 100,
            dataIndex: 'profitLoss',
            align: 'right',
            renderer: function(val, metadata, record) {
                metadata.style = val < 0 ? 'color: red;' : 'color: green;';
                return Ext.util.Format.number(val, '0.00');
            }
        }, {
            text: 'Cumul PL',
            width: 100,
            dataIndex: 'cumulProfitLoss',
            align: 'right',
            renderer: function(val, metadata, record) {
                metadata.style = val < 0 ? 'color: red;' : 'color: green;';
                return Ext.util.Format.number(val, '0.00');
            }
        }, {
            flex: 1
        }]
    },
    dockedItems: [{
        xtype: 'pagingtoolbar',
        bind: '{statistics}',
        dock: 'bottom',
        displayInfo: true
    }]
});