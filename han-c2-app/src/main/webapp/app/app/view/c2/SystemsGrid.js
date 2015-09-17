/**
 * Created by robertk on 4/18/15.
 */
Ext.define('C2.view.c2.SystemsGrid', {
    extend: 'Ext.grid.Panel',
    requires: [
        'C2.view.c2.C2Controller',
        'Ext.form.field.Checkbox',
        'Ext.form.field.Text',
        'Ext.grid.column.Check',
        'Ext.grid.plugin.RowEditing'
    ],
    xtype: 'systems-grid',
    bind: '{c2Systems}',
    title: 'Systems',
    columns: {
        defaults: {
            style: 'background-color: #157fcc; color: black;'
        },
        items: [{
            text: 'System ID',
            width: 100,
            dataIndex: 'systemId'
        }, {
            text: 'System Name',
            width: 150,
            dataIndex: 'systemName',
            editor: {
                xtype: 'textfield',
                allowBlank: false
            }
        }, {
            text: 'Origin',
            width: 120,
            dataIndex: 'conversionOrigin',
            editor: {
                xtype: 'textfield',
                allowBlank: false
            }
        }, {
            text: 'Use Ssl',
            width: 80,
            dataIndex: 'useSsl',
            xtype: 'checkcolumn',
            editor: {
                xtype: 'checkboxfield'
            }
        }, {
            text: 'Email',
            flex: 1,
            dataIndex: 'email',
            editor: {
                xtype: 'textfield',
                allowBlank: false
            }
        }]
    },
    dockedItems: [{
        xtype: 'pagingtoolbar',
        bind: '{c2Systems}',
        dock: 'bottom',
        displayInfo: true
    }],
    plugins: {
        ptype: 'rowediting',
        clicksToEdit: 2,
        listeners: {
            edit: function (editor, ctx, eOpts) {ctx.grid.getStore().sync()}
        }
    }
});