/**
 * Created by robertk on 4/17/15.
 */
Ext.define('C2.view.c2.C2', {
    extend: 'Ext.panel.Panel',
    xtype: 'c2',
    reference: 'c2',
    requires: [
        'C2.view.c2.C2Controller',
        'C2.view.c2.C2Model',
        'C2.view.c2.RequestsGrid',
        'C2.view.c2.SignalsGrid',
        'C2.view.c2.SystemsGrid',
        'Ext.layout.container.VBox'
    ],
    controller: 'c2',
    viewModel: {
        type: 'c2'
    },
    header: false,
    border: false,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [{
        xtype: 'systems-grid',
        reference: 'systemsGrid'
    }, {
        xtype: 'requests-grid',
        reference: 'requestsGrid'
    }, {
        xtype: 'signals-grid',
        reference: 'signalsGrid'
    }]
});