/**
 * Created by robertk on 4/17/15.
 */
Ext.define('HanGui.view.c2.C2', {
    extend: 'Ext.panel.Panel',
    xtype: 'han-c2',
    reference: 'c2',
    requires: [
        'HanGui.view.c2.C2Controller',
        'HanGui.view.c2.C2Model',
        'HanGui.view.c2.grid.RequestsGrid',
        'HanGui.view.c2.grid.SignalsGrid',
        'HanGui.view.c2.grid.SystemsGrid',
        'Ext.layout.container.VBox'
    ],
    controller: 'han-c2',
    viewModel: {
        type: 'han-c2'
    },
    header: false,
    border: false,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    scrollable: true,
    items: [{
        xtype: 'han-c2-systems-grid',
        reference: 'systemsGrid'
    }, {
        xtype: 'han-c2-requests-grid',
        reference: 'requestsGrid'
    }, {
        xtype: 'han-c2-signals-grid',
        reference: 'signalsGrid'
    }]
});