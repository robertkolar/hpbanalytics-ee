/**
 * Created by robertk on 4/18/15.
 */
Ext.define('IbLogger.view.iblogger.AccountsGrid', {
    extend: 'Ext.grid.Panel',
    requires: [
        'Ext.form.field.Checkbox',
        'Ext.form.field.Number',
        'Ext.form.field.Text',
        'Ext.grid.column.Action',
        'Ext.grid.column.Check',
        'Ext.grid.plugin.RowEditing',
        'IbLogger.view.iblogger.IbLoggerController'
    ],
    xtype: 'han-iblogger-accounts-grid',
    listeners: {
        select: 'onAccountSelect'
    },
    bind: '{ibAccounts}',
    title: 'IB Accounts',
    viewConfig: {
        stripeRows: true
    },
    columns: [{
        text: 'Account ID',
        width: 120,
        dataIndex: 'accountId'
    }, {
        text: 'Connect',
        xtype: 'actioncolumn',
        width: 140,
        align: 'center',
        items: [{
            icon: 'resources/images/play-circle.png',
            tooltip: 'Connect',
            handler: 'connectIb'
        }, {
            icon: 'resources/images/pause.png',
            tooltip: 'Disconnect',
            handler: 'disconnectIb'
        }]
    }, {
        text: 'Status',
        width: 80,
        align: 'center',
        dataIndex: 'ibConnectionIsConnected',
        renderer: 'connectStatusRenderer'
    }, {
        text: 'Accounts',
        width: 200,
        dataIndex: 'ibConnectionAccounts'
    }, {
        text: 'Host',
        width: 150,
        dataIndex: 'host',
        editor: {
            xtype: 'textfield',
            allowBlank: false
        }
    }, {
        text: 'Port',
        width: 80,
        dataIndex: 'port',
        align: 'right',
        editor: {
            xtype: 'numberfield',
            minValue: 1,
            maxValue: 65535,
            allowDecimals: false
        }
    }, {
        text: 'Lst',
        width: 60,
        dataIndex: 'listen',
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkboxfield'
        }
    }, {
        text: 'Upd',
        width: 60,
        dataIndex: 'allowUpd',
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkboxfield'
        }
    }, {
        text: 'Ibtoc2',
        width: 60,
        dataIndex: 'ibtoc2',
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkboxfield'
        }
    }, {
        text: 'Analy',
        width: 60,
        dataIndex: 'analytics',
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkboxfield'
        }
    }, {
        text: 'Stk',
        width: 60,
        dataIndex: 'stk',
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkboxfield'
        }
    }, {
        text: 'Opt',
        width: 60,
        dataIndex: 'opt',
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkboxfield'
        }
    }, {
        text: 'Fut',
        width: 60,
        dataIndex: 'fut',
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkboxfield'
        }
    }, {
        text: 'Fx',
        width: 60,
        dataIndex: 'fx',
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkboxfield'
        }
    }, {
        text: 'Permit Clients',
        width: 140,
        dataIndex: 'permittedClients',
        editor: {
            xtype: 'textfield',
            allowBlank: true
        }
    }, {
        text: 'Permit Accounts',
        flex: 1,
        dataIndex: 'permittedAccounts',
        editor: {
            xtype: 'textfield',
            allowBlank: true
        }
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        bind: '{ibAccounts}',
        dock: 'bottom',
        displayInfo: true
    }],
    plugins: {
        ptype: 'rowediting',
        clicksToEdit: 2,
        listeners: {
            edit: function (editor, ctx, eOpts) {ctx.grid.getStore().sync()}
        }
    }
});