/**
 * Created by robertk on 4/11/15.
 */
Ext.define('HanGui.model.c2.PublishEvent', {
    extend: 'HanGui.model.c2.Base',

    fields: [
        {name: 'id', type: 'string'},
        {name: 'eventDate', type: 'date', dateFormat: 'time'},
        'status',
        'c2Request',
        'c2Response',
        {name: 'c2SignalDbId', type: 'string', reference: {type: 'C2Signal', inverse: 'publishEvents'}}
    ]
});