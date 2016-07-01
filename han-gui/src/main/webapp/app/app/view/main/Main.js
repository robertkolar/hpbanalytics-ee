/**
 * This class is the main view for the application. It is specified in app.js as the
 * "autoCreateViewport" property. That setting automatically applies the "viewport"
 * plugin to promote that instance of this class to the body element.
 *
 */
Ext.define('Report.view.main.Main', {
    extend: 'Ext.container.Container',

    requires: [
        'Report.view.main.MainController',
        'Report.view.main.MainModel',
        'Report.view.report.Report',
        'C2.model.c2.C2Signal',
        'C2.model.c2.PublishEvent',
        'C2.model.c2.PollEvent'
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
        xtype: 'han-report'
    }]
});
