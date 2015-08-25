/**
 * Created by robertk on 4/11/15.
 */
Ext.define('IbLogger.view.iblogger.OrdersGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'orders-grid',
    reference: 'ordersGrid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'IbLogger.view.iblogger.IbLoggerController'
    ],
    bind: '{ibOrders}',
    title: 'IB Orders',
    listeners: {
        'cellclick': 'showEvents'
    },
    columns: [{
        text: 'ID',
        width: 80,
        dataIndex: 'id',
        align: 'right'
    }, {
        text: 'Submit Date',
        width: 180,
        dataIndex: 'submitDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'Status',
        width: 80,
        dataIndex: 'status',
        renderer: 'statusRenderer'
    }, {
        text: 'IB Account',
        width: 100,
        dataIndex: 'ibAccountId'
    }, {
        text: 'PermId',
        width: 100,
        dataIndex: 'permId',
        align: 'right'
    }, {
        text: 'Undl',
        width: 80,
        dataIndex: 'underlying'
    }, {
        text: 'Cur',
        width: 80,
        dataIndex: 'currency'
    }, {
        text: 'Action',
        width: 60,
        dataIndex: 'action'
    }, {
        text: 'Qnt',
        width: 60,
        dataIndex: 'quantity',
        align: 'right'
    }, {
        text: 'Symbol',
        width: 180,
        dataIndex: 'symbol'
    }, {
        text: 'Sec',
        width: 60,
        dataIndex: 'secType'
    }, {
        text: 'Ord',
        width: 60,
        dataIndex: 'orderType'
    }, {
        text: 'Price',
        width: 80,
        dataIndex: 'orderPrice',
        align: 'right'
    }, {
        text: 'TIF',
        width: 60,
        dataIndex: 'tif'
    }, {
        text: 'Status Date',
        width: 180,
        dataIndex: 'statusDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'Fill',
        width: 80,
        dataIndex: 'fillPrice',
        align: 'right'
    }, {
        text: 'Ord',
        width: 60,
        dataIndex: 'orderId',
        align: 'right'
    }, {
        text: 'Prnt',
        width: 60,
        dataIndex: 'parentId',
        align: 'right'
    }, {
        text: 'Cli',
        width: 60,
        dataIndex: 'clientId',
        align: 'right'
    }, {
        text: 'HB',
        width: 60,
        dataIndex: 'heartbeatCount',
        align: 'right'
    }, {
        text: 'OCA',
        flex: 1,
        dataIndex: 'ocaGroup'
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        bind: '{ibOrders}',
        dock: 'bottom',
        displayInfo: true
    }]
});