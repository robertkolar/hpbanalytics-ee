/**
 * Created by robertk on 9/6/15.
 */
Ext.define('Report.view.report.form.ExecutionAddForm', {
    extend: 'Ext.window.Window',

    requires: [
        'Report.common.Glyphs',
        'Report.view.report.ReportController'
    ],

    layout: 'fit',
    closable: false,
    closeAction: 'destroy',
    modal: true,
    width: 350,

    items: [{
        xtype: 'form',
        reference: 'executionAddForm',
        bodyPadding: 15,
        layout: 'anchor',
        defaults: {
            anchor: '100%',
            padding: 10,
            allowBlank: false,
            msgTarget: 'side',
            labelWidth: 80
        },
        items: [{
            xtype: 'combobox',
            name: 'action',
            editable: false,
            queryMode: 'local',
            fieldLabel: 'Action',
            store: Ext.create('Ext.data.Store', {
                fields: ['text'],
                data: [{"text": "BUY"}, {"text": "SELL"}]
            }),
            value: 'BUY'
        }, {
            xtype: 'numberfield',
            fieldLabel: 'Quantity',
            name: 'quantity',
            allowDecimals: false,
            minValue: 0,
            value: 100
        }, {
            xtype: 'textfield',
            fieldLabel: 'Underlying',
            name: 'underlying',
            minLength: 1,
            maxLength: 10,
            value: 'SPY'
        }, {
            xtype: 'textfield',
            fieldLabel: 'Symbol',
            name: 'symbol',
            minLength: 1,
            maxLength: 30,
            value: 'SPY'
        }, {
            xtype: 'combobox',
            name: 'secType',
            editable: false,
            queryMode: 'local',
            fieldLabel: 'Sec Type',
            store: Ext.create('Ext.data.Store', {
                fields: ['text'],
                data: [{"text": "STK"}, {"text": "OPT"}, {"text": "FUT"}, {"text": "CASH"}]
            }),
            value: 'STK'
        }, {
            xtype: 'numberfield',
            fieldLabel: 'Fill Price',
            name: 'fillPrice',
            decimalPrecision: 5,
            minValue: 0,
            value: 0.01
        }, {
            xtype: 'datefield',
            fieldLabel: 'Fill Date',
            name: 'fillDate',
            value: new Date(),
            format: 'm/d/Y H:i:s'
        }, {
            xtype: 'textfield',
            fieldLabel: 'Comment',
            name: 'comment',
            maxLength: 255,
            allowBlank: true
        }],
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            layout: {
                pack: 'end',
                type: 'hbox'
            },
            items: [{
                xtype: 'button',
                text: 'Submit',
                margin: '5 0 5 0',
                listeners: {
                    click: 'onSubmitAddExecution',
                    beforerender: function(c, eOpts) {
                        c.setGlyph(Report.common.Glyphs.getGlyph('save'));
                    }
                }
            }, {
                xtype: 'button',
                text: 'Cancel',
                margin: '5 10 5 10',
                listeners: {
                    click: 'onCancelAddExecution',
                    beforerender: function(c, eOpts) {
                        c.setGlyph(Report.common.Glyphs.getGlyph('cancel'));
                    }
                }
            }]
        }]
    }]
});