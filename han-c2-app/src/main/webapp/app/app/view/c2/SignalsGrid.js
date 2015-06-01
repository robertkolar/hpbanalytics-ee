/**
 * Created by robertk on 4/11/15.
 */
Ext.define('C2.view.c2.SignalsGrid', {
    extend: 'Ext.grid.Panel',
    requires: [
        'C2.view.c2.C2Controller',
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging'
    ],
    xtype: 'signals-grid',
    reference: 'signalsGrid',
    title: 'C2 Signals',
    controller: 'c2',
    bind: '{c2Signals}',
    listeners: {
        'cellclick' : 'showEvents'
    },
    disableSelection: true,
    columns: [{
        text: 'ID',
        width: 60,
        dataIndex: 'id',
        align: 'right'
    }, {
        text: 'Created Date',
        width: 180,
        dataIndex: 'createdDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'Status',
        width: 105,
        dataIndex: 'publishStatus',
        renderer: 'publishStatusRenderer'
    }, {
        text: 'Origin',
        width: 100,
        dataIndex: 'origin'
    }, {
        text: 'RefID',
        width: 100,
        dataIndex: 'referenceId',
        align: 'right'
    }, {
        text: 'SystemID',
        width: 100,
        dataIndex: 'c2SystemId'
    }, {
        text: 'SigID',
        width: 90,
        dataIndex: 'c2SignalId'
    }, {
        text: 'Act',
        width: 60,
        dataIndex: 'action'
    }, {
        text: 'Qnt',
        width: 50,
        dataIndex: 'quant',
        align: 'right'
    }, {
        text: 'Symbol',
        flex: 1,
        dataIndex: 'symbol'
    }, {
        text: 'Instrm',
        width: 80,
        dataIndex: 'instrument'
    }, {
        text: 'LMT',
        width: 60,
        dataIndex: 'limitPrice',
        align: 'right'
    }, {
        text: 'STP',
        width: 60,
        dataIndex: 'stopPrice',
        align: 'right'
    }, {
        text: 'Dur',
        width: 60,
        dataIndex: 'duration'
    }, {
        text: 'RQ',
        width: 50,
        dataIndex: 'reversalQuant'
    }, {
        text: 'RType',
        width: 80,
        dataIndex: 'reversalSignalType'
    }, {
        text: 'RPar',
        width: 80,
        dataIndex: 'reversalParent'
    }, {
        text: 'Poll Date',
        width: 180,
        dataIndex: 'pollDate',
        xtype: 'datecolumn',
        format: 'm/d/Y H:i:s.u'
    }, {
        text: 'PollSts',
        width: 105,
        dataIndex: 'pollStatus',
        renderer: 'pollStatusRenderer'
    }, {
        text: 'Trd Prc',
        width: 60,
        dataIndex: 'tradePrice',
        align: 'right'
    }],
    dockedItems: [{
        xtype: 'pagingtoolbar',
        bind: '{c2Signals}',
        dock: 'bottom',
        displayInfo: true
    }]
});
