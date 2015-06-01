/**
 * Created by robertk on 4/11/15.
 */
Ext.define('IbLogger.model.IbOrderEvent', {
    extend: 'IbLogger.model.Base',

    fields: [
        {name: 'id', type: 'string'},
        {name: 'eventDate', type: 'date', dateFormat: 'time'},
        'status',
        'updatePrice',
        'fillPrice',
        {name: 'ibOrderDbId', type: 'string', reference: {type: 'IbOrder', inverse: 'ibOrderEvents'}}
    ]
});