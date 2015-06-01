/**
 * Created by robertk on 4/17/15.
 */
Ext.define('IbLogger.view.iblogger.IbLoggerModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'IbLogger.model.IbOrder',
        'IbLogger.model.IbAccount'
    ],

    alias: 'viewmodel.iblogger',

    stores: {
        ibOrders: {
            model: 'IbLogger.model.IbOrder',
            autoload: true,
            pageSize: 10
        },
        ibAccounts: {
            model: 'IbLogger.model.IbAccount',
            autoload: true,
            pageSize: 10
        }
    }
});