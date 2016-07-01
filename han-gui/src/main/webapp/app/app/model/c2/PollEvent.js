/**
 * Created by robertk on 4/11/15.
 */
Ext.define('HanGui.model.c2.PollEvent', {
    extend: 'Ext.data.Model',

    fields: [
        {name: 'id', type: 'string'},
        {name: 'eventDate', type: 'date', dateFormat: 'time'},
        'status',
        'c2Request',
        'c2Response',
        {name: 'c2SignalDbId', type: 'string'},
        'datePosted',
        'dateEmailed',
        'dateKilled',
        'dateExpired',
        'dateTraded',
        'tradePrice'
    ]
});