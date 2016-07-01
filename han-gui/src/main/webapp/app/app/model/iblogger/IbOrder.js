/**
 * Created by robertk on 4/11/15.
 */
Ext.define('IbLogger.model.iblogger.IbOrder', {
    extend: 'IbLogger.model.iblogger.Base',

    fields: [
        {name: 'id', type: 'string'},
        'permId',
        'orderId',
        'clientId',
        'action',
        'quantity',
        'underlying',
        'currency',
        'symbol',
        'secType',
        'orderType',
        {name: 'submitDate', type: 'date', dateFormat: 'time'},
        'orderPrice',
        'tif',
        'parentId',
        'ocaGroup',
        {name: 'statusDate', type: 'date', dateFormat: 'time'},
        'fillPrice',
        'status',
        'heartbeatCount',
        'ibAccountId'
    ]
});