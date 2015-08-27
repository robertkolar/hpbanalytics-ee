/**
 * Created by robertk on 4/11/15.
 */
Ext.define('C2.view.c2.RequestsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'requests-grid',

    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],

    reference: 'requestsGrid',
    title: 'Input Requests',
    bind: '{inputRequests}',
    disableSelection: true,
    columns: {
        defaults: {
            style: 'background-color: #157fcc; color: black;'
        },
        items: [{
            text: 'ID',
            width: 60,
            dataIndex: 'id',
            align: 'right'
        }, {
            text: 'Received Date',
            width: 180,
            dataIndex: 'receivedDate',
            xtype: 'datecolumn',
            format: 'm/d/Y H:i:s.u'
        }, {
            text: 'Status',
            width: 80,
            dataIndex: 'status',
            renderer: 'requestStatusRenderer'
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
            text: 'ReqType',
            width: 80,
            dataIndex: 'requestType'
        }, {
            text: 'Action',
            width: 80,
            dataIndex: 'action'
        }, {
            text: 'Qnt',
            width: 80,
            dataIndex: 'quantity',
            align: 'right'
        }, {
            text: 'Symbol',
            width: 200,
            dataIndex: 'symbol'
        }, {
            text: 'Sec',
            width: 80,
            dataIndex: 'secType'
        }, {
            text: 'Ord',
            width: 80,
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
            text: 'Ign Reason',
            flex: 1,
            dataIndex: 'ignoreReason'
        }]
    },
    dockedItems: [{
        xtype: 'pagingtoolbar',
        bind: '{inputRequests}',
        dock: 'bottom',
        displayInfo: true
    }]
});