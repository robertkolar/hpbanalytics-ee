/**
 * Created by robertk on 4/17/15.
 */
Ext.define('IbLogger.view.iblogger.IbLogger', {
    extend: 'Ext.panel.Panel',
    xtype: 'han-iblogger',
    reference: 'iblogger',
    header: false,
    border: false,
    requires: [
        'Ext.layout.container.VBox',
        'IbLogger.view.iblogger.grid.AccountsGrid',
        'IbLogger.view.iblogger.IbLoggerController',
        'IbLogger.view.iblogger.IbLoggerModel',
        'IbLogger.view.iblogger.grid.OrdersGrid'
    ],
    controller: 'han-iblogger',
    viewModel: {
        type: 'han-iblogger'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [{
        xtype: 'han-iblogger-accounts-grid',
        reference: 'accountsGrid'
    }, {
        xtype: 'han-iblogger-orders-grid',
        reference: 'ordersGrid'
    }]
});