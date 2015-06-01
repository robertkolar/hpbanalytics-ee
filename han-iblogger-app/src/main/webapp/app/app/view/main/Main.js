/**
 * This class is the main view for the application. It is specified in app.js as the
 * "autoCreateViewport" property. That setting automatically applies the "viewport"
 * plugin to promote that instance of this class to the body element.
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('IbLogger.view.main.Main', {
    extend: 'Ext.container.Container',
    requires: [
        'Ext.button.Button',
        'Ext.layout.container.VBox',
        'IbLogger.common.Glyphs',
        'IbLogger.view.iblogger.IbLogger',
        'IbLogger.view.main.MainController',
        'IbLogger.view.main.MainModel',
        'IbLogger.model.iblogger.IbOrder',
        'IbLogger.model.iblogger.IbOrderEvent',
    ],

    xtype: 'app-main',

    controller: 'main',
    viewModel: {
        type: 'main'
    },
    scrollable: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [{
        xtype: 'button',
        glyph: IbLogger.common.Glyphs.getGlyph('refresh'),
        tooltip: 'Refresh All',
        handler: 'refreshAll',
        text: 'IB Logger',
        style: {
            background: 'DodgerBlue'
        }
    }, {
        xtype: 'iblogger'
    }]
});
