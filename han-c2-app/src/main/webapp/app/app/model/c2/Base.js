/**
 * Created by robertk on 4/17/15.
 */
Ext.define('C2.model.c2.Base', {
    extend: 'Ext.data.Model',

    requires: [
        'C2.common.Definitions',
        'C2.common.Util',
        'Ext.data.proxy.Ajax',
        'Ext.data.reader.Json',
        'Ext.data.writer.Json'
    ],

    schema: {
        namespace: 'C2.model.c2',
        urlPrefix: C2.common.Definitions.urlPrefix + 'c2',
        proxy: {
            type: 'ajax',
            actionMethods: {
                read: 'GET',
                update: 'PUT'
            },
            api: {
                read: '{prefix}/{entityName:lowercase}s',
                update: '{prefix}/{entityName:lowercase}s'
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
                    C2.common.Util.showErrorMsg(response.responseText);
                }
            }
        }
    }
});