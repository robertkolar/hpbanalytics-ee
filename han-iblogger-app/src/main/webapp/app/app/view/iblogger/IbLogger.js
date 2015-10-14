/**
 * Created by robertk on 4/17/15.
 */
Ext.define('IbLogger.view.iblogger.IbLogger', {
    extend: 'Ext.panel.Panel',
    xtype: 'iblogger',
    reference: 'iblogger',
    header: false,
    border: false,
    requires: [
        'Ext.layout.container.VBox',
        'IbLogger.view.iblogger.AccountsGrid',
        'IbLogger.view.iblogger.IbLoggerController',
        'IbLogger.view.iblogger.IbLoggerModel',
        'IbLogger.view.iblogger.OrdersGrid'
    ],
    controller: 'iblogger',
    viewModel: {
        type: 'iblogger'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [{
        xtype: 'accounts-grid',
        reference: 'accountsGrid'
    }, {
        xtype: 'orders-grid',
        reference: 'ordersGrid'
    }]
});