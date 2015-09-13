/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.common.Glyphs', {
    singleton: true,

    config: {
        webFont: 'FontAwesome',
        add: 'xf067',
        edit: 'xf040',
        destroy: 'xf1f8',
        save: 'xf00c',
        cancel: 'xf0e2',
        refresh: 'xf021',
        barchart: 'xf080',
        orderedlist: 'xf0cb',
        money: 'xf0d6'
    },

    constructor: function(config) {
        this.initConfig(config);
    },

    getGlyph: function(glyph) {
        var me = this,
            font = me.getWebFont();
        if (typeof me.config[glyph] === 'undefined') {
            return false;
        }
        return me.config[glyph] + '@' + font;
    }
});