/**
 * Created by robertk on 4/11/15.
 */
Ext.define('C2.model.c2.C2Signal', {
    extend: 'C2.model.c2.Base',

    fields: [
        {name: 'id', type: 'string'},
        'origin',
        'referenceId',
        'c2SignalId',
        'action',
        'quant',
        'reversalQuant',
        'reversalSignalType',
        'reversalParent',
        'symbol',
        'instrument',
        'limitPrice',
        'stopPrice',
        'duration',
        'ocaGroup',
        'pollStatus',
        'publishStatus',
        {name: 'createdDate', type: 'date', dateFormat: 'time'},
        {name: 'pollDate', type: 'date', dateFormat: 'time'},
        {name: 'publishStatusDate', type: 'date', dateFormat: 'time'},
        'tradePrice',
        'c2SystemId'
    ]
});