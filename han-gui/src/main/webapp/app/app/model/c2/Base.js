/**
 * Created by robertk on 4/17/15.
 */
Ext.define('HanGui.model.c2.Base', {
    extend: 'Ext.data.Model',

    requires: [
        'HanGui.common.Definitions',
        'HanGui.common.Util',
        'Ext.data.proxy.Ajax',
        'Ext.data.reader.Json',
        'Ext.data.writer.Json'
    ],

    schema: {
        namespace: 'HanGui.model.c2',
        proxy: {
            type: 'ajax',
            actionMethods: {
                read: 'GET',
                update: 'PUT'
            },
            reader: {
                type: 'json',
                rootProperty: 'items',
                totalProperty: 'total'
            },
            writer: {
                type: 'json',
                writeAllFields: true,
                writeRecordId: true
            },
            listeners: {
                exception: function(proxy, response, operation) {
                    //C2.common.Util.showErrorMsg(response.responseText);
                }
            }
        }
    }
});