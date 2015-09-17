/**
 * This class is the main view for the application. It is specified in app.js as the
 * "autoCreateViewport" property. That setting automatically applies the "viewport"
 * plugin to promote that instance of this class to the body element.
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('Report.view.main.Main', {
    extend: 'Ext.container.Container',

    requires: [
        'Report.view.main.MainController',
        'Report.view.main.MainModel',
        'Report.view.report.Report'
    ],

    xtype: 'app-main',
    
    controller: 'main',
    viewModel: {
        type: 'main'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    scrollable: true,
    items: [{
        xtype: 'report'
    }]
});
