/**
 * Created by robertk on 4/11/15.
 */
Ext.define('C2.model.InputRequest', {
    extend: 'C2.model.Base',

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
        'tif'
    ]
});