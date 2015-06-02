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

    showEvents: function (view, cell, cellIndex, record, row, rowIndex, e) {
        if (cellIndex != 2) {
            return;
        }
        var me = this;

        if (!me.eventsGrid) {
            me.eventsGrid =  Ext.create('IbLogger.view.iblogger.EventsGrid');
            me.eventsWindow = Ext.create('widget.events-window', {
                width: 680
            });
            me.eventsWindow.add(me.eventsGrid);
        }
        var permId = record.get(record.getFields()[1].getName());
        me.eventsGrid.setStore(record.ibOrderEvents());
        me.eventsWindow.setTitle("IB Order Events, permId=" + permId);
        me.eventsWindow.show();
    },

    ibEventIdRenderer: function(val, metadata, record) {
        return record.data['id'] + '/' + record.data['ibOrderDbId'];
    },

    statusRenderer: function(val, metadata, record) {
        metadata.style = 'cursor: pointer; background-color: ' + IbLogger.common.Definitions.getIbOrderStatusColor(val) + '; color: white;';
        return val;
    },

    connectStatusRenderer: function(val, metadata, record) {
        if (metadata) {
            metadata.style = 'background-color: ' + (val ? 'green' : 'red') + '; color: white;';
        }
        return (val ? 'CON' : 'DIS');
    },

    statusRendererEvent: function(val, metadata, record) {
        metadata.style = 'background-color: ' + IbLogger.common.Definitions.getIbOrderStatusColor(val) + '; color: white;';
        return val;
    },

    reloadStores: function() {
        var me = this,
            ibAccounts = me.getStore('ibAccounts'),
            ibOrders = me.getStore('ibOrders');

        ibAccounts.reload();
        ibOrders.reload();
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