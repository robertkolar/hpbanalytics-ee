/**
 * Created by robertk on 4/17/15.
 */
Ext.define('C2.view.c2.C2Controller', {
    extend: 'Ext.app.ViewController',
    requires: [
        'C2.common.Definitions',
        'C2.store.PollEventStore',
        'C2.view.c2.PollEventsGrid',
        'C2.view.c2.PublishEventsGrid',
        'Ext.String',
        'Ext.toolbar.Paging'
    ],

    alias: 'controller.c2',

    init: function() {
        var me = this,
            c2Systems = me.getStore('c2Systems'),
            inputRequests = me.getStore('inputRequests'),
            c2Signals = me.getStore('c2Signals');

        if (c2Systems) {
            c2Systems.reload();
        }
        if (inputRequests) {
            inputRequests.reload();
        }
        if (c2Signals) {
            c2Signals.reload();
        }

        var ws = new WebSocket(C2.common.Definitions.wsUrl);
        ws.onopen = function(evt) {
            console.log('WS opened');
        };
        ws.onclose = function(evt) {
            console.log('WS closed');
        };
        ws.onmessage = function(evt) {
            console.log('WS message, reloading stores...');
            inputRequests.reload();
            c2Signals.reload();
        };
        ws.onerror = function(evt) {
            console.log('WS error');
        };
    },

    showEvents: function (view, cell, cellIndex, record, row, rowIndex, e) {
        if (cellIndex == 2) {
            this.showPublishEvents(record);
        } else if (cellIndex == 18) {
            this.showPollEvents(record);
        }
    },

    showPublishEvents: function (record) {
        var me = this;
        if (!me.publishEventsGrid) {
            me.publishEventsGrid =  Ext.create('C2.view.c2.PublishEventsGrid');
            me.publishEventsWindow = Ext.create('widget.events-window');
            me.publishEventsWindow.add(me.publishEventsGrid);
        }
        var dbId = record.get(record.getFields()[0].getName());
        me.publishEventsGrid.setStore(record.publishEvents());
        me.publishEventsWindow.setTitle("Publish Events, dbId=" + dbId);
        me.publishEventsWindow.show();
    },

    showPollEvents: function (record) {
        var me = this;
        var dbId = record.get(record.getFields()[0].getName());
        if (!me.pollEventsGrid) {
            me.pollEventsGrid =  Ext.create('C2.view.c2.PollEventsGrid', {
                store: Ext.create('C2.store.PollEventStore')
            });
            var paging = Ext.create('Ext.toolbar.Paging', {
                displayInfo: true,
                dock: 'bottom',
                store: me.pollEventsGrid.getStore()
            });
            me.pollEventsGrid.addDocked(paging, 'bottom');
            me.pollEventsWindow = Ext.create('widget.events-window');
            me.pollEventsWindow.add(me.pollEventsGrid);
        }
        var proxy = me.pollEventsGrid.getStore().getProxy();
        proxy.setUrl(proxy.getUrlTemplate().replace('{dbId}', dbId));
        me.pollEventsGrid.getStore().removeAll();
        me.pollEventsGrid.getStore().reload();
        me.pollEventsWindow.setTitle("Poll Events, dbId=" + dbId);
        me.pollEventsWindow.show();
    },

    requestStatusRenderer: function(val, metadata, record) {
        metadata.style = 'background-color: ' + C2.common.Definitions.getRequestStatusColor(val) + '; color: white;';
        return val.toLowerCase();
    },

    publishStatusRenderer: function(val, metadata, record) {
        metadata.style = 'cursor: pointer; background-color: ' + C2.common.Definitions.getPublishStatusColor(val) + '; color: white;';
        return val.toLowerCase();
    },

    pollStatusRenderer: function(val, metadata, record) {
        metadata.style = 'cursor: pointer; background-color: ' + C2.common.Definitions.getPollStatusColor(val) + '; color: white;';
        return val.toLowerCase();
    }
});