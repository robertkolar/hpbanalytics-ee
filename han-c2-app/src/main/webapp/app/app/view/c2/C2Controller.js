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
            c2Signals = me.getStore('c2Signals'),
            inputRequests = me.getStore('inputRequests'),
            systemsGrid = me.lookupReference('systemsGrid');

        if (c2Systems) {
            c2Systems.getProxy().setUrl(C2.common.Definitions.urlPrefix + '/c2systems');
            c2Systems.load(function (records, operation, success) {
                if (success) {
                    systemsGrid.setSelection(c2Systems.first());
                }
            });
        }
        if (inputRequests) {
            inputRequests.getProxy().setUrl(C2.common.Definitions.urlPrefix + '/inputrequests');
            inputRequests.load(function(records, operation, success) {
                if (success) {
                    console.log('reloaded inputRequests');
                }
            });
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

    onSystemSelect: function(grid, record, index, eOpts) {
        var me = this,
            c2Signals = me.getStore('c2Signals'),
            signalsPaging = me.lookupReference('signalsPaging');

        me.c2SystemId = record.data.systemId;
        c2Signals.getProxy().setUrl(C2.common.Definitions.urlPrefix + '/c2systems/' + me.c2SystemId  + '/c2signals');

        if (signalsPaging.getStore().isLoaded()) {
            signalsPaging.moveFirst();
        } else {
            c2Signals.load(function(records, operation, success) {
                if (success) {
                    console.log('reloaded c2Signals for c2SystemId=' + me.c2SystemId)
                }
            });
        }
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