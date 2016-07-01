/**
 * Created by robertk on 4/18/15.
 */
Ext.define('HanGui.store.c2.PollEventStore', {
    extend: 'Ext.data.Store',

    requires: [
        'HanGui.common.Definitions',
        'HanGui.common.Util',
        'HanGui.model.c2.PollEvent'
    ],

    model: 'HanGui.model.c2.PollEvent',
    autoload: true,
    pageSize: 5,

    proxy: {
        type: 'ajax',
        reader: {
            type: 'json',
            rootProperty: 'items',
            totalProperty: 'total'
        },
        listeners: {
            exception: function(proxy, response, operation) {
                HanGui.common.Util.showErrorMsg(response.responseText);
            }
        },
        getUrlTemplate: function() {
            return HanGui.Definitions.common.Definitions.urlPrefixC2 + '/c2signals/{dbId}/pollevents'
        }
    }
});