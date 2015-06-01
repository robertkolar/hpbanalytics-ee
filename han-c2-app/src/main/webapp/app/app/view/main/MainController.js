/**
 * This class is the main view for the application. It is specified in app.js as the
 * "autoCreateViewport" property. That setting automatically applies the "viewport"
 * plugin to promote that instance of this class to the body element.
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('C2.view.main.MainController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.main',

    refreshAll: function() {
        var me = this,
            c2 = me.lookupReference('c2');

        c2.getController().reloadStores();
    }
});
