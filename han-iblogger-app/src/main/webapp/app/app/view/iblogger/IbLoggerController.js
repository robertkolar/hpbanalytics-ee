/**
 * Created by robertk on 4/17/15.
 */
Ext.define('IbLogger.view.iblogger.IbLoggerController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.iblogger',

    requires: [
        'Ext.Ajax',
        'IbLogger.common.Definitions',
        'IbLogger.view.iblogger.EventsGrid'
    ],

    init: function() {
        var me = this,
            ibAccounts = me.getStore('ibAccounts'),
            ibOrders = me.getStore('ibOrders'),
            accountsGrid = me.lookupReference('accountsGrid');

        if (ibAccounts) {
            ibAccounts.getProxy().setUrl(IbLogger.common.Definitions.urlPrefix + '/ibaccounts');
            ibAccounts.load(function (records, operation, success) {
                if (success) {
                    accountsGrid.setSelection(ibAccounts.first());
                }
            });
        }

        var ws = new WebSocket(IbLogger.common.Definitions.wsUrl);
        ws.onopen = function(evt) {
            console.log('WS opened');
        };
        ws.onclose = function(evt) {
            console.log('WS closed');
        };
        ws.onmessage = function(evt) {
            console.log('WS message, reloading store...');
            ibOrders.reload();
        };
        ws.onerror = function(evt) {
            console.log('WS error');
        };
    },

    onAccountSelect: function(grid, record, index, eOpts) {
        var me = this,
            ibOrders = me.getStore('ibOrders'),
            ordersPaging = me.lookupReference('ordersPaging');

        me.ibAccountId = record.data.accountId;
        ibOrders.getProxy().setUrl(IbLogger.common.Definitions.urlPrefix + '/ibaccounts/' + me.ibAccountId  + '/iborders');

        if (ordersPaging.getStore().isLoaded()) {
            ordersPaging.moveFirst();
        } else {
            ibOrders.load(function(records, operation, success) {
                if (success) {
                    console.log('reloaded ibOrders for ibAccountId=' + me.ibAccountId)
                }
            });
        }
    },

    showEvents: function (view, cell, cellIndex, record, row, rowIndex, e) {
        if (cellIndex != 2) {
            return;
        }
        var me = this;

        if (!me.eventsGrid) {
            me.eventsGrid =  Ext.create('IbLogger.view.iblogger.EventsGrid');
            me.eventsWindow = Ext.create('widget.events-window');
            me.eventsWindow.add(me.eventsGrid);
        }
        var permId = record.get(record.getFields()[1].getName());
        me.eventsGrid.setStore(record.ibOrderEvents());
        me.eventsWindow.setTitle("IB Order Events, permId=" + permId);
        me.eventsWindow.show();
    },

    statusRenderer: function(val, metadata, record) {
        metadata.style = 'cursor: pointer; background-color: ' + IbLogger.common.Definitions.getIbOrderStatusColor(val) + '; color: white;';
        return val.toLowerCase();
    },

    connectStatusRenderer: function(val, metadata, record) {
        if (metadata) {
            metadata.style = 'background-color: ' + (val ? 'green' : 'red') + '; color: white;';
        }
        return (val ? 'conn' : 'disconn');
    },

    connectIb: function(grid, rowIndex, colIndex) {
        this.connect(grid, rowIndex, colIndex, true);
    },

    disconnectIb: function(grid, rowIndex, colIndex) {
        this.connect(grid, rowIndex, colIndex, false);
    },

    connect: function(grid, rowIndex, colIndex, con) {
        var me = this,
            ibAccounts = me.getStore('ibAccounts'),
            accountId = grid.getStore().getAt(rowIndex).get('accountId'),
            box = Ext.MessageBox.wait(((con ? 'Connecting' : 'Disconnecting') + ' IB account ' + accountId), 'Action in progress');

        Ext.Ajax.request({
            method: 'PUT',
            url: IbLogger.common.Definitions.urlPrefix + '/ibaccounts/' + accountId + '/connect/' + (con ? 'true' : 'false'),
            success: function(response) {
                box.hide();
                grid.getStore().reload();
            },
            failure: function() {
                box.hide();
            }
        });
    }
});