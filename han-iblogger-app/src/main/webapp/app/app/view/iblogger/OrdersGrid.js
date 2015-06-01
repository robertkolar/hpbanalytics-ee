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
    controller: 'iblogger',
    listeners: {
        'cellclick': 'showEvents'
    },
    disableSelection: true,
    columns: [{
        text: 'ID',
        width: 60,
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
        width: 105,
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
        width: 80,
        dataIndex: 'action'
    }, {
        text: 'Quant',
        width: 60,
        dataIndex: 'quantity',
        align: 'right'
    }, {
        text: 'Symbol',
        width: 200,
        dataIndex: 'symbol'
    }, {
        text: 'SecType',
        width: 80,
        dataIndex: 'secType'
    }, {
        text: 'OrdType',
        width: 80,
        dataIndex: 'orderType'
    }, {
        text: 'OrdPrice',
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
        text: 'Fill Price',
        width: 80,
        dataIndex: 'fillPrice',
        align: 'right'
    }, {
        text: 'OrderId',
        width: 80,
        dataIndex: 'orderId',
        align: 'right'
    }, {
        text: 'Parent',
        width: 60,
        dataIndex: 'parentId',
        align: 'right'
    }, {
        text: 'Cl',
        width: 40,
        dataIndex: 'clientId',
        align: 'right'
    }, {
        text: 'HB',
        width: 40,
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