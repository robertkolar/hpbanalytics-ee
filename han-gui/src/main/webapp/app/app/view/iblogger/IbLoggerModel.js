/**
 * Created by robertk on 4/17/15.
 */
Ext.define('IbLogger.view.iblogger.IbLoggerModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'IbLogger.model.iblogger.IbOrder',
        'IbLogger.model.iblogger.IbAccount'
    ],

    alias: 'viewmodel.han-iblogger',

    stores: {
        ibOrders: {
            model: 'IbLogger.model.iblogger.IbOrder',
            autoload: true,
            pageSize: 25,
            remoteFilter: true,
            remoteSort: false
        },
        ibAccounts: {
            model: 'IbLogger.model.iblogger.IbAccount',
            autoload: true,
            pageSize: 10
        }
    }
});