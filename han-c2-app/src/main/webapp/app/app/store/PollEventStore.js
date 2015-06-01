/**
 * Created by robertk on 4/18/15.
 */
Ext.define('C2.store.PollEventStore', {
    extend: 'Ext.data.Store',

    requires: [
        'C2.common.Definitions',
        'C2.common.Util',
        'C2.model.PollEvent'
    ],

    model: 'C2.model.PollEvent',
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
                C2.common.Util.showErrorMsg(response.responseText);
            }
        },
        getUrlTemplate: function() {
            return C2.common.Definitions.urlPrefix + 'c2pub/c2signals/{dbId}/pollevents'
        }
    }
});