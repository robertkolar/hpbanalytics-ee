/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.view.report.ReportModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'Report.model.report.Report',
        'Report.model.report.Execution',
        'Report.model.report.SplitExecution',
        'Report.model.report.Trade',
        'Report.model.report.Statistics'
    ],

    alias: 'viewmodel.han-report',

    stores: {
        reports: {
            model: 'Report.model.report.Report',
            proxy: {
                type: 'ajax',
                //url: Report.common.Definitions.urlPrefix + '/reports';
                reader: {
                    type: 'json',
                    rootProperty: 'items',
                    totalProperty: 'total'
                }
            },
            pageSize: 10
        },
        executions: {
            model: 'Report.model.report.Execution',
            proxy: {
                type: 'ajax',
                //url: Report.common.Definitions.urlPrefix + 'reports/1/executions',
                reader: {
                    type: 'json',
                    rootProperty: 'items',
                    totalProperty: 'total'
                }
            },
            pageSize: 25
        },
        trades: {
            model: 'Report.model.report.Trade',
            proxy: {
                type: 'ajax',
                //url: Report.common.Definitions.urlPrefix + 'reports/1/trades',
                reader: {
                    type: 'json',
                    rootProperty: 'items',
                    totalProperty: 'total'
                }
            },
            pageSize: 25
        },
        statistics: {
            model: 'Report.model.report.Statistics',
            proxy: {
                type: 'ajax',
                //url: Report.common.Definitions.urlPrefix + 'reports/1/statistics/MONTH',
                reader: {
                    type: 'json',
                    rootProperty: 'items',
                    totalProperty: 'total'
                }
            },
            pageSize: 10
        },
        charts: {
            model: 'Report.model.report.Statistics',
            proxy: {
                type: 'ajax',
                //url: Report.common.Definitions.urlPrefix + 'reports/1/charts/MONTH',
                reader: {
                    type: 'json',
                    rootProperty: 'items',
                    totalProperty: 'total'
                }
            }
        }
    }
});