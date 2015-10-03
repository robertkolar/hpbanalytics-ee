/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.view.report.grid.ReportsGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'reports-grid',
    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'Report.view.report.ReportController'
    ],
    listeners: {
        select: 'onReportSelect'
    },
    bind: '{reports}',
    viewConfig: {
        stripeRows: true
    },
    columns: {
        defaults: {
            style: 'background-color: #157fcc; color: black;'
        },
        items: [{
            text: 'ID',
            width: 80,
            dataIndex: 'id',
            align: 'right'
        }, {
            text: 'Origin',
            width: 100,
            dataIndex: 'origin'
        }, {
            text: 'Name',
            width: 120,
            dataIndex: 'name'
        }, {
            text: 'Exec',
            width: 120,
            dataIndex: 'numExecutions',
            align: 'right'
        }, {
            xtype: 'templatecolumn',
            text: 'Trds-Open',
            width: 120,
            tpl: '{numTrades}-{numOpenTrades}',
            align: 'right'
        }, {
            xtype: 'templatecolumn',
            text: 'Undl-Open',
            width: 120,
            tpl: '{numUnderlyings}-{numOpenUnderlyings}',
            align: 'right'
        }, {
            text: 'Started',
            width: 180,
            dataIndex: 'firstExecutionDate',
            xtype: 'datecolumn',
            format: 'm/d/Y H:i:s.u'
        }, {
            text: 'Last',
            width: 180,
            dataIndex: 'lastExecutionDate',
            xtype: 'datecolumn',
            format: 'm/d/Y H:i:s.u'
        }, {
            xtype: 'widgetcolumn',
            width : 50,
            widget: {
                xtype: 'button',
                width: 30,
                tooltip: 'Analyze Report',
                handler: 'onAnalyzeReport',
                listeners: {
                    beforerender: function(c, eOpts) {
                        c.setGlyph(Report.common.Glyphs.getGlyph('gear'));
                    }
                }
            }
        }, {
            xtype: 'widgetcolumn',
            width : 50,
            widget: {
                xtype: 'button',
                width: 30,
                tooltip: 'Delete Report',
                handler: 'onDeleteReport',
                listeners: {
                    beforerender: function(c, eOpts) {
                        c.setGlyph(Report.common.Glyphs.getGlyph('delete'));
                    }
                }
            }
        }, {
            flex: 1
        }]
    },
    dockedItems: [{
        xtype: 'pagingtoolbar',
        bind: '{reports}',
        dock: 'bottom',
        displayInfo: true
    }]
});