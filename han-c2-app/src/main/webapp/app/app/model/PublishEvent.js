/**
 * Created by robertk on 4/11/15.
 */
Ext.define('C2.model.PublishEvent', {
    extend: 'C2.model.Base',

    fields: [
        {name: 'id', type: 'string'},
        {name: 'eventDate', type: 'date', dateFormat: 'time'},
        'status',
        'c2Request',
        'c2Response',
        {name: 'c2SignalDbId', type: 'string', reference: {type: 'C2Signal', inverse: 'publishEvents'}}
    ]
});