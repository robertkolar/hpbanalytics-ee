/**
 * Created by robertk on 4/11/15.
 */
Ext.define('HanGui.model.iblogger.IbAccount', {
    extend: 'HanGui.model.iblogger.Base',
    idProperty: 'accountId',

    fields: [
        'accountId',
        'host',
        'port',
        'listen',
        'allowUpd',
        'ibtoc2',
        'analytics',
        'stk',
        'fut',
        'opt',
        'fx',
        'permittedClients',
        'permittedAccounts',
        {name: 'ibConnectionAccounts', mapping: 'ibConnection.accounts', persist: false},
        {name: 'ibConnectionIsConnected', mapping: 'ibConnection.isConnected', persist: false}
    ]
});