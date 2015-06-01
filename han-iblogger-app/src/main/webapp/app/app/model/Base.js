/**
 * Created by robertk on 4/17/15.
 */
Ext.define('IbLogger.model.Base', {
    extend: 'Ext.data.Model',

    requires: [
        'Ext.data.proxy.Ajax',
        'Ext.data.reader.Json',
        'Ext.data.writer.Json'
    ],

    schema: {
        id: 'iblog',
        namespace: 'HpbSignals.model.iblog',
        urlPrefix: HpbSignals.common.Definitions.urlPrefix + 'iblog',
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
                    HpbSignals.common.Util.showErrorMsg(response.responseText);
                }
            }
        }
    }
});