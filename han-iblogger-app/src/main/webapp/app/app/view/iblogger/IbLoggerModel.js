/**
 * Created by robertk on 4/17/15.
 */
Ext.define('IbLogger.view.iblogger.IbLoggerModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'IbLogger.model.iblogger.IbOrder',
        'IbLogger.model.iblogger.IbAccount'
    ],

    alias: 'viewmodel.iblogger',

    stores: {
        ibOrders: {
            model: 'IbLogger.model.iblogger.IbOrder',
            autoload: true,
            pageSize: 20
        },
        ibAccounts: {
            model: 'IbLogger.model.iblogger.IbAccount',
            autoload: true,
            pageSize: 10
        }
    }
});