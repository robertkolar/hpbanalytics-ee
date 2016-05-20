/**
 * This class is the main view for the application. It is specified in app.js as the
 * "autoCreateViewport" property. That setting automatically applies the "viewport"
 * plugin to promote that instance of this class to the body element.
 *
 */
Ext.define('C2.view.main.Main', {
    extend: 'Ext.container.Container',
    requires: [
        'C2.common.Glyphs',
        'C2.view.c2.C2',
        'C2.view.main.MainController',
        'C2.view.main.MainModel',
        'Ext.button.Button',
        'Ext.layout.container.VBox',
        'C2.model.c2.C2Signal',
        'C2.model.c2.PublishEvent',
        'C2.model.c2.PollEvent'
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
        xtype: 'han-c2'
    }]
});
