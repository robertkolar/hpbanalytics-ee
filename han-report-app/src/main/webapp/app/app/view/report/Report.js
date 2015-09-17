/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.view.report.Report', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.layout.container.VBox',
        'Report.view.report.ReportController',
        'Report.view.report.ReportModel',
        'Report.view.report.grid.ReportsGrid',
        'Report.view.report.grid.ExecutionsGrid',
        'Report.view.report.grid.TradesGrid',
        'Report.view.report.grid.StatisticsGrid',
        'Report.common.Glyphs'
    ],

    xtype: 'report',
    header: false,
    border: false,
    controller: 'report',
    viewModel: {
        type: 'report'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [{
        xtype: 'reports-grid',
        title: 'Reports',
        reference: 'reportsGrid'
    }, {
        xtype: 'tabpanel',
        items: [{
            xtype: 'executions-grid',
            title: 'Executions',
            glyph: Report.common.Glyphs.getGlyph('orderedlist')
        }, {
            xtype: 'trades-grid',
            title: 'Trades',
            glyph: Report.common.Glyphs.getGlyph('money')
        }, {
            xtype: 'container',
            title: 'Statistics',
            glyph: Report.common.Glyphs.getGlyph('barchart'),
            items: [{
                xtype: 'statistics-grid'
            }, {
                xtype: 'toolbar',
                defaults: {
                    xtype: 'combobox',

                    editable: false,
                    queryMode: 'local',
                    displayField: 'name',
                    valueField: 'abbr'
                },
                items: [{
                    reference: 'intervalCombo',
                    fieldLabel: 'Interval',
                    width: 150,
                    labelWidth: 50,
                    store: Ext.create('Ext.data.Store', {
                        fields: ['abbr', 'name'],
                        data: [
                            {"abbr": "DAY", "name": "Daily"},
                            {"abbr": "MONTH", "name": "Monthly"}
                        ]
                    }),
                    value: 'MONTH',
                    listeners: {
                        change: 'onIntervalChange'
                    }
                }, {
                    reference: 'underlyingCombo',
                    fieldLabel: 'Underlying',
                    width: 170,
                    store: Ext.create('Ext.data.ArrayStore', {
                        fields: ['underlying']
                    }),
                    labelWidth: 70,
                    margin: '0 0 0 10',
                    listeners: {
                        change: 'onUnderlyingChange'
                    }
                }]
            }]
        }]
    }]
});