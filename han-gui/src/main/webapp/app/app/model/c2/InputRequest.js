/**
 * Created by robertk on 4/11/15.
 */
Ext.define('HanGui.model.c2.InputRequest', {
    extend: 'HanGui.model.c2.Base',

    fields: [
        {name: 'id', type: 'string'},
        {name: 'receivedDate', type: 'date', dateFormat: 'time'},
        'status',
        {name: 'statusDate', type: 'date', dateFormat: 'time'},
        'ignoreReason',
        'origin',
        'referenceId',
        'requestType',
        'action',
        'quantity',
        'symbol',
        'secType',
        'orderType',
        'orderPrice',
        'tif',
        'ocaGroup'
    ]
});